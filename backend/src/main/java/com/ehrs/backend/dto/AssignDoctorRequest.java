package com.ehrs.backend.dto;

import javax.validation.constraints.NotBlank;

public class AssignDoctorRequest {

    @NotBlank
    private String visitid;

    @NotBlank
    private String employeeid;

    public String getVisitid() {
        return visitid;
    }

    public void setVisitid(String visitid) {
        this.visitid = visitid;
    }

    public String getEmployeeid() {
        return employeeid;
    }

    public void setEmployeeid(String employeeid) {
        this.employeeid = employeeid;
    }
}
