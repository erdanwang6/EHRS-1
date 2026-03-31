import { APIRequestContext, expect, Page, test } from '@playwright/test';

const backendBaseUrl = 'http://127.0.0.1:8081';

async function loginAsDoctor(page: Page) {
  await page.goto('/login');
  await page.locator('input[formcontrolname="username"]').fill('dr.chen');
  await page.locator('input[formcontrolname="password"]').fill('Doctor123!');
  await page.getByRole('button', { name: 'LOG IN' }).click();
  await expect(page).toHaveURL(/\/home\/doctorlanding/);
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

async function assignDoctorToVisit(request: APIRequestContext, visitId: string, employeeId: string) {
  const assignDoctorResponse = await request.put(`${backendBaseUrl}/assignDoctor`, {
    data: {
      visitid: visitId,
      employeeid: employeeId
    }
  });

  expect(assignDoctorResponse.ok()).toBeTruthy();
}

async function loginApi(request: APIRequestContext, username: string, password: string): Promise<string> {
  const response = await request.post(`${backendBaseUrl}/api/v1/auth/login`, {
    data: {
      username,
      password
    }
  });

  expect(response.ok()).toBeTruthy();
  const payload = await response.json();
  return payload.token as string;
}

async function saveVitals(request: APIRequestContext, token: string, patientId: string, visitId: string) {
  const response = await request.post(`${backendBaseUrl}/api/v1/medical-records/vitals`, {
    headers: {
      Authorization: `Bearer ${token}`
    },
    data: {
      patientid: patientId,
      visitid: visitId,
      systolic_bp: 118,
      diastolic_bp: 76,
      pulse_bpm: 68,
      temperature_c: 36.8,
      weight_kg: 74.2,
      height_cm: 177
    }
  });

  expect(response.ok()).toBeTruthy();
}

async function saveMedication(request: APIRequestContext, token: string, patientId: string, visitId: string, medicationName: string) {
  const response = await request.post(`${backendBaseUrl}/api/v1/medical-records/medications`, {
    headers: {
      Authorization: `Bearer ${token}`
    },
    data: {
      patientid: patientId,
      visitid: visitId,
      medication_name: medicationName,
      dosage_instruction: '500 mg tablet',
      frequency: 'Twice daily',
      duration: '7 days',
      medication_status: 'ACTIVE'
    }
  });

  expect(response.ok()).toBeTruthy();
}

test('doctor receive patient selects the longest-waiting visit assigned to the logged-in doctor and shows overview details', async ({ page, request }) => {
  const suffix = `${Date.now()}`;
  const doctor = await getFirstDoctor(request);
  const doctorToken = await loginApi(request, 'dr.chen', 'Doctor123!');
  const nurseToken = await loginApi(request, 'nurse.walker', 'Nurse123!');
  const oldestVisit = await createPatientAndVisit(
    request,
    `DoctorOldest${suffix}`,
    'QueuePatient',
    '1988-10-12',
    `Doctor oldest visit ${suffix}`,
    '1500-01-01T08:00:00'
  );
  const newestVisit = await createPatientAndVisit(
    request,
    `DoctorNewest${suffix}`,
    'QueuePatient',
    '1990-11-22',
    `Doctor newest visit ${suffix}`,
    '1500-01-02T10:30:00'
  );

  await assignDoctorToVisit(request, oldestVisit.visit.visit_id, doctor.employeeid);
  await assignDoctorToVisit(request, newestVisit.visit.visit_id, doctor.employeeid);
  await saveVitals(request, nurseToken, oldestVisit.patient.patientid, oldestVisit.visit.visit_id);
  await saveMedication(request, doctorToken, oldestVisit.patient.patientid, oldestVisit.visit.visit_id, `Doctor Med ${suffix}`);

  await loginAsDoctor(page);
  await page.goto('/home/doctorlanding');
  await page.getByRole('button', { name: 'Receive Patient' }).click();

  await expect(page).toHaveURL(/\/home\/DoctorOverview/);
  await expect(page).toHaveURL(new RegExp(`patientid=${oldestVisit.patient.patientid}`));
  await expect(page).toHaveURL(new RegExp(`visitid=${oldestVisit.visit.visit_id}`));
  await expect(page.getByTestId('doctor-patient-name')).toHaveText(`DoctorOldest${suffix} QueuePatient`);
  await expect(page.getByTestId('doctor-visit-reason')).toHaveText(`Doctor oldest visit ${suffix}`);
  await expect(page.getByTestId('doctor-vitals-bp')).toContainText('118 / 76');
  await expect(page.getByTestId('doctor-medication-row').first()).toContainText(`Doctor Med ${suffix}`);
});
