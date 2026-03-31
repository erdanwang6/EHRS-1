package com.ehrs.backend.dto;

public class PatientDetailDto {

    private final String patientid;
    private final String medical_record_number;
    private final String first_name;
    private final String middle_name;
    private final String last_name;
    private final String full_name;
    private final String date_of_birth;
    private final String sex;
    private final String status;

    public PatientDetailDto(
            String patientid,
            String medical_record_number,
            String first_name,
            String middle_name,
            String last_name,
            String full_name,
            String date_of_birth,
            String sex,
            String status
    ) {
        this.patientid = patientid;
        this.medical_record_number = medical_record_number;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.full_name = full_name;
        this.date_of_birth = date_of_birth;
        this.sex = sex;
        this.status = status;
    }

    public String getPatientid() {
        return patientid;
    }

    public String getMedical_record_number() {
        return medical_record_number;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public String getSex() {
        return sex;
    }

    public String getStatus() {
        return status;
    }
}
