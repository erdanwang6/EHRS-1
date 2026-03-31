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

test('nurse receives the longest-waiting triage patient and sees patient details plus visit history', async ({ page, request }) => {
  const suffix = `${Date.now()}`;
  const oldestPatient = await createPatientAndVisit(
    request,
    `Oldest${suffix}`,
    'NursePatient',
    '1989-05-16',
    `Oldest triage visit ${suffix}`,
    '2000-01-01T08:00:00'
  );
  await createPatientAndVisit(
    request,
    `Newest${suffix}`,
    'NursePatient',
    '1991-09-08',
    `Newest triage visit ${suffix}`,
    '2026-12-31T08:00:00'
  );

  await loginAsNurse(page);
  await page.goto('/home/nurselanding');
  await page.getByRole('button', { name: 'Receive Patient' }).click();

  await expect(page).toHaveURL(/\/home\/NurseOverview/);
  await expect(page.getByTestId('nurse-patient-name')).toHaveText(`Oldest${suffix} NursePatient`);
  await expect(page.getByTestId('nurse-patient-dob')).toHaveText('05/16/1989');
  await expect(page.getByTestId('nurse-current-visit-status')).toHaveText('Waiting for Triage');
  await expect(page.getByTestId('nurse-current-visit-id')).toHaveText(oldestPatient.visit.visit_id);
  await expect(page.getByTestId('nurse-history-summary')).toContainText('1 visit record');
  await expect(page.getByTestId('nurse-history-row').first()).toContainText(`Oldest triage visit ${suffix}`);
});
