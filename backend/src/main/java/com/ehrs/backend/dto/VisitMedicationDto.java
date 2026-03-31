package com.ehrs.backend.dto;

public class VisitMedicationDto {

    private final String visit_medication_id;
    private final String patientid;
    private final String visitid;
    private final String medication_reference_id;
    private final String medication_name;
    private final String dosage_instruction;
    private final String frequency;
    private final String duration;
    private final String medication_status;
    private final String prescribed_at;
    private final String prescribed_by;

    public VisitMedicationDto(
            String visit_medication_id,
            String patientid,
            String visitid,
            String medication_reference_id,
            String medication_name,
            String dosage_instruction,
            String frequency,
            String duration,
            String medication_status,
            String prescribed_at,
            String prescribed_by
    ) {
        this.visit_medication_id = visit_medication_id;
        this.patientid = patientid;
        this.visitid = visitid;
        this.medication_reference_id = medication_reference_id;
        this.medication_name = medication_name;
        this.dosage_instruction = dosage_instruction;
        this.frequency = frequency;
        this.duration = duration;
        this.medication_status = medication_status;
        this.prescribed_at = prescribed_at;
        this.prescribed_by = prescribed_by;
    }

    public String getVisit_medication_id() {
        return visit_medication_id;
    }

    public String getPatientid() {
        return patientid;
    }

    public String getVisitid() {
        return visitid;
    }

    public String getMedication_reference_id() {
        return medication_reference_id;
    }

    public String getMedication_name() {
        return medication_name;
    }

    public String getDosage_instruction() {
        return dosage_instruction;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getDuration() {
        return duration;
    }

    public String getMedication_status() {
        return medication_status;
    }

    public String getPrescribed_at() {
        return prescribed_at;
    }

    public String getPrescribed_by() {
        return prescribed_by;
    }
}
