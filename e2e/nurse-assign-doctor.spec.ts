import { expect, Page, APIRequestContext, test } from '@playwright/test';

const backendBaseUrl = 'http://127.0.0.1:8081';

async function loginAsNurse(page: Page) {
  await page.goto('/login');
  await page.locator('input[formcontrolname="username"]').fill('nurse.walker');
  await page.locator('input[formcontrolname="password"]').fill('Nurse123!');
  await page.getByRole('button', { name: 'LOG IN' }).click();
  await expect(page).toHaveURL(/\/home\/nurselanding/);
}

async function createPatientAndVisit(
  request: APIRequestContext,
  firstName: string,
  lastName: string,
  dateOfBirth: string,
  visitReason: string,
  visitDate: string
) {
  const patientResponse = await request.post(`${backendBaseUrl}/person/patient`, {
    data: {
      firstName,
      lastName,
      dateOfBirth,
      sex: 'Female'
    }
  });

  expect(patientResponse.ok()).toBeTruthy();
  const patient = await patientResponse.json();

  const visitResponse = await request.post(`${backendBaseUrl}/visit`, {
    data: {
      patientid: patient.patientid,
      visit_date: visitDate,
      status: 'Waiting for Triage',
      visit_reason: visitReason
    }
  });

  expect(visitResponse.ok()).toBeTruthy();
  const visit = await visitResponse.json();

  return { patient, visit };
}

async function getFirstDoctor(request: APIRequestContext) {
  const doctorsResponse = await request.get(`${backendBaseUrl}/person/doctor`);
  expect(doctorsResponse.ok()).toBeTruthy();
  const doctors = await doctorsResponse.json();
  expect(doctors.length).toBeGreaterThan(0);
  return doctors[0];
}

test('nurse overview keeps only assign doctor action and assigns the selected doctor to the current visit', async ({ page, request }) => {
  const suffix = `${Date.now()}`;
  const created = await createPatientAndVisit(
    request,
    `Assign${suffix}`,
    'DoctorPatient',
    '1994-01-20',
    `Assign doctor workflow ${suffix}`,
    '2026-03-30T09:15:00'
  );
  const doctor = await getFirstDoctor(request);

  await loginAsNurse(page);
  await page.goto(`/home/NurseOverview?patientid=${created.patient.patientid}&visitid=${created.visit.visit_id}&status=Waiting%20for%20Triage`);

  await expect(page.getByRole('button', { name: 'Add Vitals' })).toHaveCount(0);
  await expect(page.getByRole('button', { name: 'View Patient Visit Record' })).toHaveCount(0);
  await expect(page.getByRole('button', { name: 'View Patient Medical Record' })).toHaveCount(0);
  await expect(page.getByRole('button', { name: 'Draw Blood' })).toHaveCount(0);
  await expect(page.getByRole('button', { name: 'Administrater Vaccine' })).toHaveCount(0);
  await expect(page.getByRole('button', { name: 'Test/Treatment' })).toHaveCount(0);
  await expect(page.getByRole('button', { name: 'Checkout' })).toHaveCount(0);

  await page.getByRole('combobox', { name: 'Doctor' }).click();
  await page.getByRole('option', { name: doctor.name_birth }).click();
  await page.getByRole('button', { name: 'Assign Doctor' }).click();

  await expect(page.getByTestId('assign-doctor-message')).toContainText(doctor.name_birth);
  await expect(page.getByTestId('nurse-current-visit-status')).toHaveText('Waiting for Doctor');
  await expect(page.getByTestId('nurse-assigned-doctor')).toHaveText(doctor.name_birth);

  const doctorQueueResponse = await request.get(`${backendBaseUrl}/queue/doctor`);
  expect(doctorQueueResponse.ok()).toBeTruthy();
  const doctorQueue = await doctorQueueResponse.json();

  expect(doctorQueue).toEqual(
    expect.arrayContaining([
      expect.objectContaining({
        visit_id: created.visit.visit_id,
        patientid: created.patient.patientid,
        doctor: doctor.name_birth,
        status: 'Waiting for Doctor'
      })
    ])
  );
});
