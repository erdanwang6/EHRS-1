import { APIRequestContext, expect, Page, test } from '@playwright/test';

const backendBaseUrl = 'http://127.0.0.1:8081';
const patientId = '00000040-0000-0000-0000-000000000001';

async function loginAsDoctor(page: Page) {
  await page.goto('/login');
  await page.locator('input[formcontrolname="username"]').fill('dr.chen');
  await page.locator('input[formcontrolname="password"]').fill('Doctor123!');
  await page.getByRole('button', { name: 'LOG IN' }).click();
  await expect(page).toHaveURL(/\/home\/doctorlanding/);
}

async function getFirstDoctor(request: APIRequestContext) {
  const doctorsResponse = await request.get(`${backendBaseUrl}/person/doctor`);
  expect(doctorsResponse.ok()).toBeTruthy();
  const doctors = await doctorsResponse.json();
  expect(doctors.length).toBeGreaterThan(0);
  return doctors[0];
}

test('doctor workflow orders medication and sends visit to checkout', async ({ page, request }) => {
  const createVisitResponse = await request.post(`${backendBaseUrl}/visit`, {
    data: {
      patientid: patientId,
      visit_date: '1400-01-01T08:00:00',
      status: 'Waiting for Doctor',
      visit_reason: 'Playwright doctor workflow'
    }
  });

  expect(createVisitResponse.ok()).toBeTruthy();
  const createdVisit = await createVisitResponse.json();
  const visitId = createdVisit.visit_id as string;
  const doctor = await getFirstDoctor(request);

  const assignDoctorResponse = await request.put(`${backendBaseUrl}/assignDoctor`, {
    data: {
      visitid: visitId,
      employeeid: doctor.employeeid
    }
  });

  expect(assignDoctorResponse.ok()).toBeTruthy();

  await loginAsDoctor(page);
  await page.getByRole('button', { name: 'Receive Patient' }).click();

  await expect(page).toHaveURL(/\/home\/DoctorOverview/);
  await expect(page).toHaveURL(new RegExp(`visitid=${visitId}`));

  await page.getByRole('button', { name: 'Add Diagnosis' }).click();
  await expect(page).toHaveURL(/\/home\/diagnosis/);
  await expect(page.locator('#Diagnosis option')).toHaveCount(4);

  await page.locator('#remarks').fill('Playwright pre-order remarks');
  await page.locator('app-doctor-diagnosis .orderPrescription').getByRole('button', { name: 'Order Prescription' }).click();

  await expect(page).toHaveURL(/\/home\/order-prescription/);
  await page.locator('#order_name').fill('Playwright medication');

  const orderResponsePromise = page.waitForResponse(response =>
    response.url().endsWith('/order') && response.request().method() === 'POST'
  );
  await page.locator('app-doctor-order-prescription .prescriptionSelect').getByRole('button', { name: 'Submit Order' }).click();
  const orderResponse = await orderResponsePromise;

  expect(orderResponse.ok()).toBeTruthy();
  await expect(page).toHaveURL(/\/home\/diagnosis/);

  await page.locator('#remarks').fill('Playwright diagnosis submitted');
  const diagnosisResponsePromise = page.waitForResponse(response =>
    response.url().endsWith('/updateVisitSummary') && response.request().method() === 'PUT'
  );
  await page.locator('app-doctor-diagnosis .submitDiagnosis').getByRole('button', { name: 'Submit Diagnosis' }).click();
  const diagnosisResponse = await diagnosisResponsePromise;

  expect(diagnosisResponse.ok()).toBeTruthy();
  await expect(page).toHaveURL(/\/home\/doctorlanding/);

  const checkoutQueueResponse = await request.get(`${backendBaseUrl}/queue/checkout`);
  expect(checkoutQueueResponse.ok()).toBeTruthy();

  const checkoutQueue = await checkoutQueueResponse.json();
  expect(checkoutQueue).toEqual(
    expect.arrayContaining([
      expect.objectContaining({
        visit_id: visitId,
        patientid: patientId,
        status: 'Ready for Checkout'
      })
    ])
  );
});
