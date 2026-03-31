package com.ehrs.backend.controller;

import com.ehrs.backend.dto.CreatePatientRequest;
import com.ehrs.backend.dto.DoctorSummaryDto;
import com.ehrs.backend.dto.PatientDetailDto;
import com.ehrs.backend.dto.PatientSummaryDto;
import com.ehrs.backend.service.PeopleApiService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PeopleController {

    private final PeopleApiService peopleApiService;

    public PeopleController(PeopleApiService peopleApiService) {
        this.peopleApiService = peopleApiService;
    }

    @GetMapping("/api/v1/people/patients")
    public List<PatientSummaryDto> getPatients() {
        return peopleApiService.getPatients();
    }

    @PostMapping("/api/v1/people/patients")
    public PatientSummaryDto createPatient(@Valid @RequestBody CreatePatientRequest request) {
        return peopleApiService.createPatient(request);
    }

    @GetMapping("/api/v1/people/patients/{patientid}")
    public PatientDetailDto getPatient(@PathVariable String patientid) {
        return peopleApiService.getPatient(patientid);
    }

    @GetMapping("/api/v1/people/doctors")
    public List<DoctorSummaryDto> getDoctors() {
        return peopleApiService.getDoctors();
    }

    @GetMapping("/person/patient")
    public List<PatientSummaryDto> getPatientsLegacy() {
        return peopleApiService.getPatients();
    }

    @PostMapping("/person/patient")
    public PatientSummaryDto createPatientLegacy(@Valid @RequestBody CreatePatientRequest request) {
        return peopleApiService.createPatient(request);
    }

    @GetMapping("/person/patient/{patientid}")
    public PatientDetailDto getPatientLegacy(@PathVariable String patientid) {
        return peopleApiService.getPatient(patientid);
    }

    @GetMapping("/person/doctor")
    public List<DoctorSummaryDto> getDoctorsLegacy() {
        return peopleApiService.getDoctors();
    }
}
