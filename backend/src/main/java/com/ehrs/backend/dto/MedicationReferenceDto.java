package com.ehrs.backend.dto;

public class MedicationReferenceDto {

    private final String medication_reference_id;
    private final String medication_code;
    private final String medication_name;
    private final String dosage_form;
    private final String strength;

    public MedicationReferenceDto(
            String medication_reference_id,
            String medication_code,
            String medication_name,
            String dosage_form,
            String strength
    ) {
        this.medication_reference_id = medication_reference_id;
        this.medication_code = medication_code;
        this.medication_name = medication_name;
        this.dosage_form = dosage_form;
        this.strength = strength;
    }

    public String getMedication_reference_id() {
        return medication_reference_id;
    }

    public String getMedication_code() {
        return medication_code;
    }

    public String getMedication_name() {
        return medication_name;
    }

    public String getDosage_form() {
        return dosage_form;
    }

    public String getStrength() {
        return strength;
    }
}
