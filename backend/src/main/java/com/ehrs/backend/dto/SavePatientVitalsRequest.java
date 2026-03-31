package com.ehrs.backend.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SavePatientVitalsRequest {

    @NotBlank
    private String patientid;

    @NotBlank
    private String visitid;

    @NotNull
    private BigDecimal systolic_bp;

    @NotNull
    private BigDecimal diastolic_bp;

    @NotNull
    private BigDecimal pulse_bpm;

    @NotNull
    private BigDecimal temperature_c;

    @NotNull
    private BigDecimal weight_kg;

    @NotNull
    private BigDecimal height_cm;

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    public String getVisitid() {
        return visitid;
    }

    public void setVisitid(String visitid) {
        this.visitid = visitid;
    }

    public BigDecimal getSystolic_bp() {
        return systolic_bp;
    }

    public void setSystolic_bp(BigDecimal systolic_bp) {
        this.systolic_bp = systolic_bp;
    }

    public BigDecimal getDiastolic_bp() {
        return diastolic_bp;
    }

    public void setDiastolic_bp(BigDecimal diastolic_bp) {
        this.diastolic_bp = diastolic_bp;
    }

    public BigDecimal getPulse_bpm() {
        return pulse_bpm;
    }

    public void setPulse_bpm(BigDecimal pulse_bpm) {
        this.pulse_bpm = pulse_bpm;
    }

    public BigDecimal getTemperature_c() {
        return temperature_c;
    }

    public void setTemperature_c(BigDecimal temperature_c) {
        this.temperature_c = temperature_c;
    }

    public BigDecimal getWeight_kg() {
        return weight_kg;
    }

    public void setWeight_kg(BigDecimal weight_kg) {
        this.weight_kg = weight_kg;
    }

    public BigDecimal getHeight_cm() {
        return height_cm;
    }

    public void setHeight_cm(BigDecimal height_cm) {
        this.height_cm = height_cm;
    }
}
