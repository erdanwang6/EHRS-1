export interface Patient {
    patientid: string;
    namebirth: string;
}

export interface PatientDetail {
    patientid: string;
    medical_record_number: string;
    first_name: string;
    middle_name?: string | null;
    last_name: string;
    full_name: string;
    date_of_birth: string;
    sex?: string | null;
    status: string;
}

export interface CreatePatientRequest {
    firstName: string;
    middleName?: string;
    lastName: string;
    dateOfBirth: string;
    sex?: string;
}
