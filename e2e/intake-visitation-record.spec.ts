import { expect, Page, test } from '@playwright/test';

const backendBaseUrl = 'http://127.0.0.1:8081';

async function loginAsIntake(page: Page) {
  await page.goto('/login');
  await page.locator('input[formcontrolname="username"]').fill('intake.ross');
  await page.locator('input[formcontrolname="password"]').fill('Intake123!');
  await page.getByRole('button', { name: 'LOG IN' }).click();
  await expect(page).toHaveURL(/\/home\/visitation-queue/);
}

test('intake can create a new patient and visitation record from the UI', async ({ page, request }) => {
  const uniqueSuffix = `${Date.now()}`;
  const firstName = `Playwright${uniqueSuffix}`;
  const lastName = 'Patient';
  const dateOfBirth = '1992-03-14';
  const visitReason = `Playwright intake ${uniqueSuffix}`;
  const expectedNameBirth = `${firstName} ${lastName} 03/14/1992`;

  await loginAsIntake(page);
  await page.goto('/home/visitation-record');

  await page.locator('input[name="createNewPatient"]').check({ force: true });
  await page.locator('input[name="newPatientFirstName"]').fill(firstName);
  await page.locator('input[name="newPatientLastName"]').fill(lastName);
  await page.locator('input[name="newPatientDateOfBirth"]').fill(dateOfBirth);
  await page.locator('textarea[name="visitReason"]').fill(visitReason);

  const createPatientResponsePromise = page.waitForResponse(response =>
    response.url().endsWith('/person/patient') && response.request().method() === 'POST'
  );
  const createVisitResponsePromise = page.waitForResponse(response =>
    response.url().endsWith('/visit') && response.request().method() === 'POST'
  );

  await page.getByRole('button', { name: 'Save Patient and Record' }).click();

  const createPatientResponse = await createPatientResponsePromise;
  const createPatientPayload = await createPatientResponse.json();
  const createVisitResponse = await createVisitResponsePromise;
  const createVisitPayload = await createVisitResponse.json();

  expect(createPatientResponse.ok()).toBeTruthy();
  expect(createVisitResponse.ok()).toBeTruthy();

  await expect(page).toHaveURL(/\/home\/visitation-queue/);

  const patientsResponse = await request.get(`${backendBaseUrl}/person/patient`);
  expect(patientsResponse.ok()).toBeTruthy();
  const patients = await patientsResponse.json();

  expect(patients).toEqual(
    expect.arrayContaining([
      expect.objectContaining({
        patientid: createPatientPayload.patientid,
        namebirth: expectedNameBirth
      })
    ])
  );

  const triageQueueResponse = await request.get(`${backendBaseUrl}/queue/triage`);
  expect(triageQueueResponse.ok()).toBeTruthy();
  const triageQueue = await triageQueueResponse.json();

  expect(triageQueue).toEqual(
    expect.arrayContaining([
      expect.objectContaining({
        visit_id: createVisitPayload.visit_id,
        patientid: createPatientPayload.patientid,
        patient_name: `${firstName} ${lastName}`,
        status: 'Waiting for Triage'
      })
    ])
  );
});
