package com.ehrs.backend.dto;

public class PatientSummaryDto {

    private final String patientid;
    private final String namebirth;

    public PatientSummaryDto(String patientid, String namebirth) {
        this.patientid = patientid;
        this.namebirth = namebirth;
    }

    public String getPatientid() {
        return patientid;
    }

    public String getNamebirth() {
        return namebirth;
    }
}
