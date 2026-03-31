package com.ehrs.backend.dto;

public class DoctorSummaryDto {

    private final String employeeid;
    private final String name_birth;

    public DoctorSummaryDto(String employeeid, String name_birth) {
        this.employeeid = employeeid;
        this.name_birth = name_birth;
    }

    public String getEmployeeid() {
        return employeeid;
    }

    public String getName_birth() {
        return name_birth;
    }
}
