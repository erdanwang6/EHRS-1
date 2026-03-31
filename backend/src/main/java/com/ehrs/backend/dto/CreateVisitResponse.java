package com.ehrs.backend.dto;

public class CreateVisitResponse {

    private final String visit_id;
    private final String status;
    private final String message;

    public CreateVisitResponse(String visit_id, String status, String message) {
        this.visit_id = visit_id;
        this.status = status;
        this.message = message;
    }

    public String getVisit_id() {
        return visit_id;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
