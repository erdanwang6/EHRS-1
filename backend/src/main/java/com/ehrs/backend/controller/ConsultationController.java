package com.ehrs.backend.controller;

import com.ehrs.backend.dto.DiagnosisListItemDto;
import com.ehrs.backend.dto.MedicationReferenceDto;
import com.ehrs.backend.dto.MedicalRecordItemDto;
import com.ehrs.backend.dto.OrderRequest;
import com.ehrs.backend.dto.OrderResponse;
import com.ehrs.backend.dto.PatientVitalsDto;
import com.ehrs.backend.dto.SaveVisitMedicationRequest;
import com.ehrs.backend.dto.SavePatientVitalsRequest;
import com.ehrs.backend.dto.VisitMedicationDto;
import com.ehrs.backend.dto.VisitHistoryItemDto;
import com.ehrs.backend.service.ConsultationApiService;
import javax.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsultationController {

    private final ConsultationApiService consultationApiService;

    public ConsultationController(ConsultationApiService consultationApiService) {
        this.consultationApiService = consultationApiService;
    }

    @GetMapping("/api/v1/consultations/diagnoses")
    public List<DiagnosisListItemDto> getDiagnosisList() {
        return consultationApiService.getDiagnosisList();
    }

    @GetMapping("/api/v1/consultations/history")
    public List<VisitHistoryItemDto> getVisitationHistory(@RequestParam String patientid) {
        return consultationApiService.getVisitationHistory(patientid);
    }

    @GetMapping("/api/v1/medical-records")
    public List<MedicalRecordItemDto> getMedicalRecord(@RequestParam String patientid) {
        return consultationApiService.getMedicalRecord(patientid);
    }

    @GetMapping("/api/v1/medical-records/vitals")
    public PatientVitalsDto getPatientVitals(
            @RequestParam String patientid,
            @RequestParam(required = false) String visitid
    ) {
        return consultationApiService.getPatientVitals(patientid, visitid);
    }

    @GetMapping("/api/v1/medical-records/medications")
    public List<VisitMedicationDto> getVisitMedications(
            @RequestParam String patientid,
            @RequestParam(required = false) String visitid
    ) {
        return consultationApiService.getVisitMedications(patientid, visitid);
    }

    @GetMapping("/api/v1/medical-records/medications/reference")
    public List<MedicationReferenceDto> getMedicationReferences() {
        return consultationApiService.getMedicationReferences();
    }

    @GetMapping("/api/v1/people/doctor-id")
    public String getDoctorId(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return consultationApiService.getDoctorId(authorizationHeader);
    }

    @GetMapping("/api/v1/people/nurse-id")
    public String getNurseId(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return consultationApiService.getNurseId(authorizationHeader);
    }

    @PostMapping("/api/v1/orders")
    public OrderResponse createOrder(
            @Valid @RequestBody OrderRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return consultationApiService.createOrder(request, authorizationHeader);
    }

    @PostMapping("/api/v1/medical-records/vitals")
    public PatientVitalsDto savePatientVitals(
            @Valid @RequestBody SavePatientVitalsRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return consultationApiService.savePatientVitals(request, authorizationHeader);
    }

    @PostMapping("/api/v1/medical-records/medications")
    public VisitMedicationDto saveVisitMedication(
            @Valid @RequestBody SaveVisitMedicationRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return consultationApiService.saveVisitMedication(request, authorizationHeader);
    }

    @GetMapping("/DiagnosisList")
    public List<DiagnosisListItemDto> getDiagnosisListLegacy() {
        return consultationApiService.getDiagnosisList();
    }

    @GetMapping("/VisitationHistory")
    public List<VisitHistoryItemDto> getVisitationHistoryLegacy(@RequestParam String patientid) {
        return consultationApiService.getVisitationHistory(patientid);
    }

    @GetMapping("/MedicalRecord")
    public List<MedicalRecordItemDto> getMedicalRecordLegacy(@RequestParam String patientid) {
        return consultationApiService.getMedicalRecord(patientid);
    }

    @GetMapping("/doctorID")
    public String getDoctorIdLegacy(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return consultationApiService.getDoctorId(authorizationHeader);
    }

    @GetMapping("/nurseID")
    public String getNurseIdLegacy(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return consultationApiService.getNurseId(authorizationHeader);
    }

    @PostMapping("/order")
    public OrderResponse createOrderLegacy(
            @Valid @RequestBody OrderRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return consultationApiService.createOrder(request, authorizationHeader);
    }
}
