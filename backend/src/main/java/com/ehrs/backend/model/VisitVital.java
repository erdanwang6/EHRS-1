package com.ehrs.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "VISIT_VITAL")
public class VisitVital {

    @Id
    @Column(name = "visit_vital_id", nullable = false)
    private UUID visitVitalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_nurse_id")
    private Nurse recordedByNurse;

    @Column(name = "temperature_c")
    private BigDecimal temperatureC;

    @Column(name = "pulse_bpm")
    private BigDecimal pulseBpm;

    @Column(name = "systolic_bp")
    private BigDecimal systolicBp;

    @Column(name = "diastolic_bp")
    private BigDecimal diastolicBp;

    @Column(name = "respiratory_rate")
    private BigDecimal respiratoryRate;

    @Column(name = "oxygen_saturation")
    private BigDecimal oxygenSaturation;

    @Column(name = "height_cm")
    private BigDecimal heightCm;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    public UUID getVisitVitalId() {
        return visitVitalId;
    }

    public void setVisitVitalId(UUID visitVitalId) {
        this.visitVitalId = visitVitalId;
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

    public Nurse getRecordedByNurse() {
        return recordedByNurse;
    }

    public void setRecordedByNurse(Nurse recordedByNurse) {
        this.recordedByNurse = recordedByNurse;
    }

    public BigDecimal getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(BigDecimal temperatureC) {
        this.temperatureC = temperatureC;
    }

    public BigDecimal getPulseBpm() {
        return pulseBpm;
    }

    public void setPulseBpm(BigDecimal pulseBpm) {
        this.pulseBpm = pulseBpm;
    }

    public BigDecimal getSystolicBp() {
        return systolicBp;
    }

    public void setSystolicBp(BigDecimal systolicBp) {
        this.systolicBp = systolicBp;
    }

    public BigDecimal getDiastolicBp() {
        return diastolicBp;
    }

    public void setDiastolicBp(BigDecimal diastolicBp) {
        this.diastolicBp = diastolicBp;
    }

    public BigDecimal getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(BigDecimal respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public BigDecimal getOxygenSaturation() {
        return oxygenSaturation;
    }

    public void setOxygenSaturation(BigDecimal oxygenSaturation) {
        this.oxygenSaturation = oxygenSaturation;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
