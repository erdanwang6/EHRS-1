package com.ehrs.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "DOCTOR")
public class Doctor {

    @Id
    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "employee_number", nullable = false)
    private String employeeNumber;

    @Column(name = "license_number", nullable = false)
    private String licenseNumber;

    @Column(name = "specialty")
    private String specialty;

    @Column(name = "department")
    private String department;

    @Column(name = "status", nullable = false)
    private String status;

    public UUID getDoctorId() {
        return doctorId;
    }

    public AppUser getUser() {
        return user;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public String getSpecialty() {
        return specialty;
    }
}
