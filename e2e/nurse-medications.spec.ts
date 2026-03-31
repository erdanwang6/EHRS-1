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

test('nurse can open the medications page from the side menu and save a medication for the current patient', async ({ page, request }) => {
  const suffix = `${Date.now()}`;
  const medicationName = `Amoxicillin ${suffix}`;
  const created = await createPatientAndVisit(
    request,
    `Medication${suffix}`,
    'Patient',
    '1990-12-11',
    `Medication workflow ${suffix}`,
    '2026-03-29T08:45:00'
  );

  await loginAsNurse(page);
  await page.goto(`/home/NurseOverview?patientid=${created.patient.patientid}&visitid=${created.visit.visit_id}&status=Waiting%20for%20Triage`);

  await page.getByRole('button', { name: 'Medications' }).click();

  await expect(page).toHaveURL(/\/home\/medrec-medications/);
  await expect(page.getByTestId('medications-patient-name')).toHaveText(`Medication${suffix} Patient`);

  await page.locator('input[formcontrolname="medicationName"]').fill(medicationName);
  await page.locator('input[formcontrolname="dosageInstruction"]').fill('500 mg capsule');
  await page.locator('input[formcontrolname="frequency"]').fill('Twice daily');
  await page.locator('input[formcontrolname="duration"]').fill('7 days');
  await page.getByRole('button', { name: 'Save Medication' }).click();

  await expect(page.getByTestId('medications-save-message')).toContainText('Medication saved');
  await expect(page.getByTestId('medication-row').first()).toContainText(medicationName);

  const medicationsResponse = await request.get(`${backendBaseUrl}/api/v1/medical-records/medications?patientid=${created.patient.patientid}&visitid=${created.visit.visit_id}`);
  expect(medicationsResponse.ok()).toBeTruthy();
  const medicationsPayload = await medicationsResponse.json();

  expect(medicationsPayload).toEqual(
    expect.arrayContaining([
      expect.objectContaining({
        patientid: created.patient.patientid,
        visitid: created.visit.visit_id,
        medication_name: medicationName,
        dosage_instruction: '500 mg capsule',
        frequency: 'Twice daily',
        duration: '7 days',
        medication_status: 'ACTIVE'
      })
    ])
  );
});
