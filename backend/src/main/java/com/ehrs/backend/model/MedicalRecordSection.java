package com.ehrs.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "MEDICAL_RECORD_SECTION")
public class MedicalRecordSection {

    @Id
    @Column(name = "medical_record_section_id", nullable = false)
    private UUID medicalRecordSectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @Column(name = "section_type", nullable = false)
    private String sectionType;

    @Column(name = "section_title", nullable = false)
    private String sectionTitle;

    @Column(name = "section_data", nullable = false)
    private String sectionData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_user_id", nullable = false)
    private AppUser recordedByUser;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public String getSectionType() {
        return sectionType;
    }

    public void setMedicalRecordSectionId(UUID medicalRecordSectionId) {
        this.medicalRecordSectionId = medicalRecordSectionId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getSectionData() {
        return sectionData;
    }

    public void setSectionData(String sectionData) {
        this.sectionData = sectionData;
    }

    public AppUser getRecordedByUser() {
        return recordedByUser;
    }

    public void setRecordedByUser(AppUser recordedByUser) {
        this.recordedByUser = recordedByUser;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
