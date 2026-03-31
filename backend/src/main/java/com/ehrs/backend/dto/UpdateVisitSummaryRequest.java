package com.ehrs.backend.dto;

import javax.validation.constraints.NotBlank;

public class UpdateVisitSummaryRequest {

    private String visit_id;
    private String doctor_id;
    private String patientid;

    @NotBlank
    private String visit_summary;

    @NotBlank
    private String newStatus;

    private String status;

    public String getVisit_id() {
        return visit_id;
    }

    public void setVisit_id(String visit_id) {
        this.visit_id = visit_id;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    public String getVisit_summary() {
        return visit_summary;
    }

    public void setVisit_summary(String visit_summary) {
        this.visit_summary = visit_summary;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
