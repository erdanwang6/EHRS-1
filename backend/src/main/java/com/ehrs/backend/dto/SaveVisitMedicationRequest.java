package com.ehrs.backend.dto;

import javax.validation.constraints.NotBlank;

public class SaveVisitMedicationRequest {

    @NotBlank
    private String patientid;

    @NotBlank
    private String visitid;

    @NotBlank
    private String medication_name;

    @NotBlank
    private String dosage_instruction;

    @NotBlank
    private String frequency;

    @NotBlank
    private String duration;

    private String medication_status;

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    public String getVisitid() {
        return visitid;
    }

    public void setVisitid(String visitid) {
        this.visitid = visitid;
    }

    public String getMedication_name() {
        return medication_name;
    }

    public void setMedication_name(String medication_name) {
        this.medication_name = medication_name;
    }

    public String getDosage_instruction() {
        return dosage_instruction;
    }

    public void setDosage_instruction(String dosage_instruction) {
        this.dosage_instruction = dosage_instruction;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMedication_status() {
        return medication_status;
    }

    public void setMedication_status(String medication_status) {
        this.medication_status = medication_status;
    }
}
