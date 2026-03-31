export interface MedicationReference {
    medication_reference_id: string;
    medication_code: string;
    medication_name: string;
    dosage_form?: string | null;
    strength?: string | null;
}

export interface VisitMedication {
    visit_medication_id: string;
    patientid: string;
    visitid: string;
    medication_reference_id: string;
    medication_name: string;
    dosage_instruction: string;
    frequency: string;
    duration: string;
    medication_status: string;
    prescribed_at: string;
    prescribed_by?: string | null;
}

export interface SaveVisitMedicationRequest {
    patientid: string;
    visitid: string;
    medication_name: string;
    dosage_instruction: string;
    frequency: string;
    duration: string;
    medication_status: string;
}
