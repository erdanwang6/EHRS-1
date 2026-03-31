package com.ehrs.backend.dto;

public class DiagnosisListItemDto {

    private final String icd19_code;
    private final String diagnosis_name;

    public DiagnosisListItemDto(String icd19_code, String diagnosis_name) {
        this.icd19_code = icd19_code;
        this.diagnosis_name = diagnosis_name;
    }

    public String getIcd19_code() {
        return icd19_code;
    }

    public String getDiagnosis_name() {
        return diagnosis_name;
    }
}
