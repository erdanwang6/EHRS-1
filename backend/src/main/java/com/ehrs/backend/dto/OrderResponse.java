package com.ehrs.backend.dto;

public class OrderResponse {

    private final String order_id;
    private final String order_status;
    private final String message;

    public OrderResponse(String order_id, String order_status, String message) {
        this.order_id = order_id;
        this.order_status = order_status;
        this.message = message;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getMessage() {
        return message;
    }
}
