package com.ehrs.backend.dto;

public class QueueEntryDto {

    private final String visit_id;
    private final String patientid;
    private final String patient_name;
    private final String visit_date;
    private final String nurse;
    private final String doctor;
    private final String status;

    public QueueEntryDto(
            String visit_id,
            String patientid,
            String patient_name,
            String visit_date,
            String nurse,
            String doctor,
            String status
    ) {
        this.visit_id = visit_id;
        this.patientid = patientid;
        this.patient_name = patient_name;
        this.visit_date = visit_date;
        this.nurse = nurse;
        this.doctor = doctor;
        this.status = status;
    }

    public String getVisit_id() {
        return visit_id;
    }

    public String getPatientid() {
        return patientid;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public String getVisit_date() {
        return visit_date;
    }

    public String getNurse() {
        return nurse;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getStatus() {
        return status;
    }
}
