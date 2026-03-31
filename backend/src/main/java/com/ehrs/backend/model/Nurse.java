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
@Table(name = "NURSE")
public class Nurse {

    @Id
    @Column(name = "nurse_id", nullable = false)
    private UUID nurseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "employee_number", nullable = false)
    private String employeeNumber;

    @Column(name = "status", nullable = false)
    private String status;

    public UUID getNurseId() {
        return nurseId;
    }

    public AppUser getUser() {
        return user;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }
}
