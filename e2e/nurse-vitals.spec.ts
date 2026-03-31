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
      sex: 'Male'
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

test('nurse can open the vitals page from the side menu and save the current patient vitals', async ({ page, request }) => {
  const suffix = `${Date.now()}`;
  const created = await createPatientAndVisit(
    request,
    `Vitals${suffix}`,
    'Patient',
    '1993-07-21',
    `Vitals workflow ${suffix}`,
    '2026-03-29T08:15:00'
  );

  await loginAsNurse(page);
  await page.goto(`/home/NurseOverview?patientid=${created.patient.patientid}&visitid=${created.visit.visit_id}&status=Waiting%20for%20Triage`);

  await page.getByRole('button', { name: 'Vitals' }).click();

  await expect(page).toHaveURL(/\/home\/medrec-vitals/);
  await expect(page.getByTestId('vitals-patient-name')).toHaveText(`Vitals${suffix} Patient`);

  await page.locator('input[formcontrolname="systolicBp"]').fill('118');
  await page.locator('input[formcontrolname="diastolicBp"]').fill('76');
  await page.locator('input[formcontrolname="pulseBpm"]').fill('68');
  await page.locator('input[formcontrolname="temperatureC"]').fill('37');
  await page.locator('input[formcontrolname="weightKg"]').fill('74.8');
  await page.locator('input[formcontrolname="heightCm"]').fill('175');

  await page.getByRole('button', { name: 'Save Vitals' }).click();

  await expect(page.getByTestId('vitals-save-message')).toContainText('Vitals saved');

  const vitalsResponse = await request.get(`${backendBaseUrl}/api/v1/medical-records/vitals?patientid=${created.patient.patientid}`);
  expect(vitalsResponse.ok()).toBeTruthy();
  const vitalsPayload = await vitalsResponse.json();

  expect(vitalsPayload).toMatchObject({
    patientid: created.patient.patientid,
    visitid: created.visit.visit_id,
    systolic_bp: 118,
    diastolic_bp: 76,
    pulse_bpm: 68,
    temperature_c: 37,
    weight_kg: 74.8,
    height_cm: 175
  });
});
