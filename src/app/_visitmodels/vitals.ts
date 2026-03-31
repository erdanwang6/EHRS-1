export interface PatientVitals {
    patientid: string;
    visitid?: string | null;
    systolic_bp?: number | null;
    diastolic_bp?: number | null;
    pulse_bpm?: number | null;
    temperature_c?: number | null;
    weight_kg?: number | null;
    height_cm?: number | null;
    recorded_at?: string | null;
    recorded_by_nurse?: string | null;
}

export interface SavePatientVitalsRequest {
    patientid: string;
    visitid: string;
    systolic_bp: number;
    diastolic_bp: number;
    pulse_bpm: number;
    temperature_c: number;
    weight_kg: number;
    height_cm: number;
}
