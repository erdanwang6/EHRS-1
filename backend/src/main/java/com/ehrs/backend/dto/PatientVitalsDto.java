package com.ehrs.backend.dto;

import java.math.BigDecimal;

public class PatientVitalsDto {

    private final String patientid;
    private final String visitid;
    private final BigDecimal systolic_bp;
    private final BigDecimal diastolic_bp;
    private final BigDecimal pulse_bpm;
    private final BigDecimal temperature_c;
    private final BigDecimal weight_kg;
    private final BigDecimal height_cm;
    private final String recorded_at;
    private final String recorded_by_nurse;

    public PatientVitalsDto(
            String patientid,
            String visitid,
            BigDecimal systolic_bp,
            BigDecimal diastolic_bp,
            BigDecimal pulse_bpm,
            BigDecimal temperature_c,
            BigDecimal weight_kg,
            BigDecimal height_cm,
            String recorded_at,
            String recorded_by_nurse
    ) {
        this.patientid = patientid;
        this.visitid = visitid;
        this.systolic_bp = systolic_bp;
        this.diastolic_bp = diastolic_bp;
        this.pulse_bpm = pulse_bpm;
        this.temperature_c = temperature_c;
        this.weight_kg = weight_kg;
        this.height_cm = height_cm;
        this.recorded_at = recorded_at;
        this.recorded_by_nurse = recorded_by_nurse;
    }

    public String getPatientid() {
        return patientid;
    }

    public String getVisitid() {
        return visitid;
    }

    public BigDecimal getSystolic_bp() {
        return systolic_bp;
    }

    public BigDecimal getDiastolic_bp() {
        return diastolic_bp;
    }

    public BigDecimal getPulse_bpm() {
        return pulse_bpm;
    }

    public BigDecimal getTemperature_c() {
        return temperature_c;
    }

    public BigDecimal getWeight_kg() {
        return weight_kg;
    }

    public BigDecimal getHeight_cm() {
        return height_cm;
    }

    public String getRecorded_at() {
        return recorded_at;
    }

    public String getRecorded_by_nurse() {
        return recorded_by_nurse;
    }
}
