package com.ehrs.backend.dto;

public class MedicalRecordItemDto {

    private final String section_type;
    private final String section_title;
    private final String section_data;
    private final String recorded_at;
    private final String recorded_by;

    public MedicalRecordItemDto(
            String section_type,
            String section_title,
            String section_data,
            String recorded_at,
            String recorded_by
    ) {
        this.section_type = section_type;
        this.section_title = section_title;
        this.section_data = section_data;
        this.recorded_at = recorded_at;
        this.recorded_by = recorded_by;
    }

    public String getSection_type() {
        return section_type;
    }

    public String getSection_title() {
        return section_title;
    }

    public String getSection_data() {
        return section_data;
    }

    public String getRecorded_at() {
        return recorded_at;
    }

    public String getRecorded_by() {
        return recorded_by;
    }
}
