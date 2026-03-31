import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ConsultationServiceService } from '../consultation-service.service';
import { PeopleService } from '../people.service';
import { Doctor } from '../_visitmodels/doctors';
import { PatientDetail } from '../_visitmodels/patient';
import { PatientWorkflowContextService } from '../patient-workflow-context.service';
import { VisitService } from '../visit.service';
@Component({
  selector: 'app-nurse-overwiew',
  templateUrl: './nurse-overwiew.component.html',
  styleUrls: ['./nurse-overwiew.component.css']
})
export class NurseOverwiewComponent implements OnInit {
  patientDetail: PatientDetail | null = null;
  visitHistory: any[] = [];
  doctorOptions: Doctor[] = [];
  selectedDoctorEmployeeId = '';
  assignedDoctorName = '';
  patientSummaryMessage = 'Loading patient details...';
  historySummaryMessage = 'Loading visit history...';
  assignMessage = '';
  assignError = '';
  isAssigning = false;
  private patientId = '';
  private visitId = '';

  constructor(
    private route: ActivatedRoute,
    private workflowContext: PatientWorkflowContextService,
    private peopleService: PeopleService,
    private consultationService: ConsultationServiceService,
    private visitService: VisitService
  ) { }

  ngOnInit(): void {
    const context = this.workflowContext.setFromQueryParams(this.route.snapshot.queryParams);
    this.patientId = context.patientid || this.workflowContext.getContext().patientid || '';
    this.visitId = context.visitid || this.workflowContext.getContext().visitid || '';

    if (!this.patientId) {
      this.patientSummaryMessage = 'No patient is currently assigned to this nurse workflow.';
      this.historySummaryMessage = 'No visit history available because no patient is selected.';
      this.loadDoctors();
      return;
    }

    forkJoin({
      patientDetail: this.peopleService.getPatientDetail(this.patientId).pipe(
        catchError(() => of(null))
      ),
      visitHistory: this.consultationService.getVisitationHistory(this.patientId).pipe(
        catchError(() => of([]))
      ),
      doctors: this.peopleService.getDoctors().pipe(
        catchError(() => of([]))
      )
    }).subscribe(({ patientDetail, visitHistory, doctors }) => {
      this.patientDetail = patientDetail;
      this.visitHistory = Array.isArray(visitHistory) ? visitHistory : [];
      this.setDoctorOptions(doctors);

      this.patientSummaryMessage = patientDetail
        ? `Viewing details for ${patientDetail.full_name}.`
        : 'Patient details could not be loaded.';

      this.historySummaryMessage = this.visitHistory.length
        ? `${this.visitHistory.length} visit record(s) found for this patient.`
        : 'No visit history found for this patient.';
    });
  }

  onAssignDoctor(): void {
    this.assignMessage = '';
    this.assignError = '';

    if (!this.canAssignDoctor) {
      this.assignError = 'Receive a patient before assigning a doctor.';
      return;
    }

    if (!this.selectedDoctorEmployeeId) {
      this.assignError = 'Choose a doctor for the current visit.';
      return;
    }

    const selectedDoctor = this.doctorOptions.find(doctor => doctor.employeeid === this.selectedDoctorEmployeeId);
    this.isAssigning = true;

    this.visitService.assignDoctor({
      visitid: this.visitId,
      employeeid: this.selectedDoctorEmployeeId
    }).subscribe({
      next: response => {
        this.assignedDoctorName = selectedDoctor?.name_birth || 'Selected doctor';
        this.assignMessage = `${this.assignedDoctorName} assigned. Visit moved to ${response.status}.`;
        this.workflowContext.setContext({
          patientid: this.patientId,
          visitid: response.visit_id,
          status: response.status
        });
        this.visitHistory = this.visitHistory.map(visit => visit.visit_id === response.visit_id
          ? { ...visit, status: response.status }
          : visit
        );
        this.isAssigning = false;
      },
      error: error => {
        this.assignError = error?.error?.message || 'Could not assign doctor to the current visit.';
        this.isAssigning = false;
      }
    });
  }

  get currentVisitStatus(): string {
    return this.workflowContext.getContext().status || 'Unknown';
  }

  get currentVisitId(): string {
    return this.workflowContext.getContext().visitid || 'Unavailable';
  }

  get canAssignDoctor(): boolean {
    return !!this.patientId && !!this.visitId;
  }

  private loadDoctors(): void {
    this.peopleService.getDoctors().pipe(catchError(() => of([]))).subscribe(doctors => {
      this.setDoctorOptions(doctors);
    });
  }

  private setDoctorOptions(doctors: Doctor[] | unknown): void {
    this.doctorOptions = Array.isArray(doctors) ? doctors : [];

    if (!this.selectedDoctorEmployeeId && this.doctorOptions.length === 1) {
      this.selectedDoctorEmployeeId = this.doctorOptions[0].employeeid;
    }
  }
}
