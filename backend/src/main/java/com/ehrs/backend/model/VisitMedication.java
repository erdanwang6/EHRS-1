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
@Table(name = "VISIT_MEDICATION")
public class VisitMedication {

    @Id
    @Column(name = "visit_medication_id", nullable = false)
    private UUID visitMedicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_reference_id", nullable = false)
    private MedicationReference medicationReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescribed_by_doctor_id", nullable = false)
    private Doctor prescribedByDoctor;

    @Column(name = "dosage_instruction")
    private String dosageInstruction;

    @Column(name = "frequency")
    private String frequency;

    @Column(name = "duration")
    private String duration;

    @Column(name = "medication_status", nullable = false)
    private String medicationStatus;

    @Column(name = "prescribed_at", nullable = false)
    private LocalDateTime prescribedAt;

    public UUID getVisitMedicationId() {
        return visitMedicationId;
    }

    public void setVisitMedicationId(UUID visitMedicationId) {
        this.visitMedicationId = visitMedicationId;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public MedicationReference getMedicationReference() {
        return medicationReference;
    }

    public void setMedicationReference(MedicationReference medicationReference) {
        this.medicationReference = medicationReference;
    }

    public Doctor getPrescribedByDoctor() {
        return prescribedByDoctor;
    }

    public void setPrescribedByDoctor(Doctor prescribedByDoctor) {
        this.prescribedByDoctor = prescribedByDoctor;
    }

    public String getDosageInstruction() {
        return dosageInstruction;
    }

    public void setDosageInstruction(String dosageInstruction) {
        this.dosageInstruction = dosageInstruction;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMedicationStatus() {
        return medicationStatus;
    }

    public void setMedicationStatus(String medicationStatus) {
        this.medicationStatus = medicationStatus;
    }

    public LocalDateTime getPrescribedAt() {
        return prescribedAt;
    }

    public void setPrescribedAt(LocalDateTime prescribedAt) {
        this.prescribedAt = prescribedAt;
    }
}
