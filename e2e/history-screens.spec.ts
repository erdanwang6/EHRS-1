import { expect, Page, test } from '@playwright/test';

const backendBaseUrl = 'http://127.0.0.1:8081';
const patientId = '00000040-0000-0000-0000-000000000001';

function toLocalDateTime(date: Date): string {
  return date.toISOString().slice(0, 19);
}

async function loginAsDoctor(page: Page) {
  await page.goto('/login');
  await page.locator('input[formcontrolname="username"]').fill('dr.chen');
  await page.locator('input[formcontrolname="password"]').fill('Doctor123!');
  await page.getByRole('button', { name: 'LOG IN' }).click();
  await expect(page).toHaveURL(/\/home\/doctorlanding/);
}

test('history screens show live visitation data and medical empty state', async ({ page, request }) => {
  const visitReason = `Playwright history ${Date.now()}`;
  const visitDate = new Date(Date.now() + 60_000);

  const createVisitResponse = await request.post(`${backendBaseUrl}/visit`, {
    data: {
      patientid: patientId,
      visit_date: toLocalDateTime(visitDate),
      status: 'Waiting for Doctor',
      visit_reason: visitReason
    }
  });

  expect(createVisitResponse.ok()).toBeTruthy();
  const createdVisit = await createVisitResponse.json();
  const visitId = createdVisit.visit_id as string;

  await loginAsDoctor(page);
  await page.goto(`/home/doctornurse-view-visitation?patientid=${patientId}&visitid=${visitId}&status=Waiting%20for%20Doctor`);

  await expect(page).toHaveURL(/\/home\/doctornurse-view-visitation/);
  await expect(page.getByTestId('visitation-summary')).toContainText(visitReason);
  await expect(page.getByTestId('visitation-summary')).toContainText('Waiting for Doctor');

  await page.goto(`/home/doctornurse-view-medical?patientid=${patientId}&visitid=${visitId}&status=Waiting%20for%20Doctor`);
  await expect(page).toHaveURL(/\/home\/doctornurse-view-medical/);
  await expect(page.getByTestId('medical-message')).toContainText('No medical record found for that patient.');
});
