package com.ehrs.backend.dto;

public class VisitHistoryItemDto {

    private final String visit_id;
    private final String visit_date;
    private final String visit_reason;
    private final String symptoms;
    private final String visit_summary;
    private final String status;

    public VisitHistoryItemDto(
            String visit_id,
            String visit_date,
            String visit_reason,
            String symptoms,
            String visit_summary,
            String status
    ) {
        this.visit_id = visit_id;
        this.visit_date = visit_date;
        this.visit_reason = visit_reason;
        this.symptoms = symptoms;
        this.visit_summary = visit_summary;
        this.status = status;
    }

    public String getVisit_id() {
        return visit_id;
    }

    public String getVisit_date() {
        return visit_date;
    }

    public String getVisit_reason() {
        return visit_reason;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getVisit_summary() {
        return visit_summary;
    }

    public String getStatus() {
        return status;
    }
}
