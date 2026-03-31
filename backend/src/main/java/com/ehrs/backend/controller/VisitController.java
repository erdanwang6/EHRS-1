package com.ehrs.backend.controller;

import com.ehrs.backend.dto.AssignDoctorRequest;
import com.ehrs.backend.dto.CreateVisitRequest;
import com.ehrs.backend.dto.CreateVisitResponse;
import com.ehrs.backend.dto.QueueEntryDto;
import com.ehrs.backend.dto.UpdateVisitSummaryRequest;
import com.ehrs.backend.service.VisitApiService;
import javax.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VisitController {

    private final VisitApiService visitApiService;

    public VisitController(VisitApiService visitApiService) {
        this.visitApiService = visitApiService;
    }

    @PostMapping("/api/v1/visits")
    public CreateVisitResponse createVisit(
            @Valid @RequestBody CreateVisitRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return visitApiService.createVisit(request, authorizationHeader);
    }

    @GetMapping("/api/v1/queue")
    public List<QueueEntryDto> getQueue() {
        return visitApiService.getQueue();
    }

    @GetMapping("/api/v1/queue/triage")
    public List<QueueEntryDto> getTriageQueue() {
        return visitApiService.getQueueByStatus("Waiting for Triage");
    }

    @GetMapping("/api/v1/queue/doctor")
    public List<QueueEntryDto> getDoctorQueue(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return visitApiService.getDoctorQueue(authorizationHeader);
    }

    @GetMapping("/api/v1/queue/payment")
    public List<QueueEntryDto> getPaymentQueue() {
        return visitApiService.getQueueByStatus("Waiting for Payment");
    }

    @GetMapping("/api/v1/queue/checkout")
    public List<QueueEntryDto> getCheckoutQueue() {
        return visitApiService.getQueueByStatus("Ready for Checkout");
    }

    @PutMapping("/api/v1/visits/summary")
    public CreateVisitResponse updateVisitSummary(
            @Valid @RequestBody UpdateVisitSummaryRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return visitApiService.updateVisitSummary(request, authorizationHeader);
    }

    @PutMapping("/api/v1/visits/assign-doctor")
    public CreateVisitResponse assignDoctor(
            @Valid @RequestBody AssignDoctorRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return visitApiService.assignDoctor(request, authorizationHeader);
    }

    @PostMapping("/visit")
    public CreateVisitResponse createVisitLegacy(
            @Valid @RequestBody CreateVisitRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return visitApiService.createVisit(request, authorizationHeader);
    }

    @GetMapping("/queue")
    public List<QueueEntryDto> getQueueLegacy() {
        return visitApiService.getQueue();
    }

    @GetMapping("/queue/triage")
    public List<QueueEntryDto> getTriageQueueLegacy() {
        return visitApiService.getQueueByStatus("Waiting for Triage");
    }

    @GetMapping("/queue/doctor")
    public List<QueueEntryDto> getDoctorQueueLegacy(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return visitApiService.getDoctorQueue(authorizationHeader);
    }

    @GetMapping("/queue/payment")
    public List<QueueEntryDto> getPaymentQueueLegacy() {
        return visitApiService.getQueueByStatus("Waiting for Payment");
    }

    @GetMapping("/queue/checkout")
    public List<QueueEntryDto> getCheckoutQueueLegacy() {
        return visitApiService.getQueueByStatus("Ready for Checkout");
    }

    @PutMapping("/updateVisitSummary")
    public CreateVisitResponse updateVisitSummaryLegacy(
            @Valid @RequestBody UpdateVisitSummaryRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return visitApiService.updateVisitSummary(request, authorizationHeader);
    }

    @PutMapping("/assignDoctor")
    public CreateVisitResponse assignDoctorLegacy(
            @Valid @RequestBody AssignDoctorRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return visitApiService.assignDoctor(request, authorizationHeader);
    }
}
