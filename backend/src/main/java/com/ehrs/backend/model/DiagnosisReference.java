package com.ehrs.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "DIAGNOSIS_REFERENCE")
public class DiagnosisReference {

    @Id
    @Column(name = "diagnosis_reference_id", nullable = false)
    private UUID diagnosisReferenceId;

    @Column(name = "diagnosis_code", nullable = false)
    private String diagnosisCode;

    @Column(name = "diagnosis_name", nullable = false)
    private String diagnosisName;

    @Column(name = "description")
    private String description;

    @Column(name = "status", nullable = false)
    private String status;

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public String getDiagnosisName() {
        return diagnosisName;
    }
}
