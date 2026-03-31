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
@Table(name = "ORDER_ITEM")
public class OrderItem {

    @Id
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordering_doctor_id", nullable = false)
    private Doctor orderingDoctor;

    @Column(name = "order_type", nullable = false)
    private String orderType;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "order_status", nullable = false)
    private String orderStatus;

    @Column(name = "notes")
    private String notes;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setOrderingDoctor(Doctor orderingDoctor) {
        this.orderingDoctor = orderingDoctor;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
