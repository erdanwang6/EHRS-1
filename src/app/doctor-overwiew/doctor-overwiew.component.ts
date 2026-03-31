import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ConsultationServiceService } from '../consultation-service.service';
import { VisitMedication } from '../_visitmodels/medications';
import { PatientDetail } from '../_visitmodels/patient';
import { PatientVitals } from '../_visitmodels/vitals';
import { PatientWorkflowContextService } from '../patient-workflow-context.service';
import { PeopleService } from '../people.service';

interface VisitHistoryItem {
  visit_id: string;
  visit_date: string;
  visit_reason: string;
  symptoms?: string | null;
  visit_summary?: string | null;
  status: string;
}

@Component({
  selector: 'app-doctor-overwiew',
  templateUrl: './doctor-overwiew.component.html',
  styleUrls: ['./doctor-overwiew.component.css']
})
export class DoctorOverwiewComponent implements OnInit {
  patientDetail: PatientDetail | null = null;
  currentVisit: VisitHistoryItem | null = null;
  latestVitals: PatientVitals | null = null;
  medications: VisitMedication[] = [];
  overviewMessage = 'Loading received patient details...';
  vitalsMessage = 'Loading vitals...';
  medicationsMessage = 'Loading medications...';
  private patientId = '';
  private visitId = '';

  constructor(
    private route: ActivatedRoute,
    private workflowContext: PatientWorkflowContextService,
    private peopleService: PeopleService,
    private consultationService: ConsultationServiceService
  ) { }

  ngOnInit(): void {
    const context = this.workflowContext.setFromQueryParams(this.route.snapshot.queryParams);
    this.patientId = context.patientid || this.workflowContext.getContext().patientid || '';
    this.visitId = context.visitid || this.workflowContext.getContext().visitid || '';

    if (!this.patientId) {
      this.overviewMessage = 'No patient has been received for this doctor yet.';
      this.vitalsMessage = 'Receive a patient to review vitals.';
      this.medicationsMessage = 'Receive a patient to review medications.';
      return;
    }

    forkJoin({
      patientDetail: this.peopleService.getPatientDetail(this.patientId).pipe(catchError(() => of(null))),
      visitHistory: this.consultationService.getVisitationHistory(this.patientId).pipe(catchError(() => of([]))),
      vitals: this.consultationService.getPatientVitals(this.patientId, this.visitId).pipe(catchError(() => of(null))),
      medications: this.consultationService.getVisitMedications(this.patientId, this.visitId).pipe(catchError(() => of([])))
    }).subscribe(({ patientDetail, visitHistory, vitals, medications }) => {
      const historyItems = Array.isArray(visitHistory) ? visitHistory as VisitHistoryItem[] : [];

      this.patientDetail = patientDetail;
      this.currentVisit = historyItems.find(visit => visit.visit_id === this.visitId) || historyItems[0] || null;
      this.latestVitals = vitals;
      this.medications = Array.isArray(medications) ? medications : [];

      this.overviewMessage = patientDetail
        ? `Reviewing ${patientDetail.full_name}.`
        : 'Patient details could not be loaded.';

      this.vitalsMessage = this.hasVitals
        ? 'Latest vitals loaded for the current visit.'
        : 'No vitals have been recorded for this visit yet.';

      this.medicationsMessage = this.medications.length
        ? `${this.medications.length} medication entr${this.medications.length === 1 ? 'y' : 'ies'} found for this visit.`
        : 'No medications have been recorded for this visit yet.';
    });
  }

  get currentVisitStatus(): string {
    return this.currentVisit?.status || this.workflowContext.getContext().status || 'Unknown';
  }

  get currentVisitReason(): string {
    return this.currentVisit?.visit_reason || 'Not recorded';
  }

  get currentVisitDate(): string {
    return this.currentVisit?.visit_date || 'Unavailable';
  }

  get currentSymptoms(): string {
    return this.currentVisit?.symptoms || 'Not recorded';
  }

  get currentSummary(): string {
    return this.currentVisit?.visit_summary || 'Not recorded';
  }

  get hasVitals(): boolean {
    return !!this.latestVitals && [
      this.latestVitals.systolic_bp,
      this.latestVitals.diastolic_bp,
      this.latestVitals.pulse_bpm,
      this.latestVitals.temperature_c,
      this.latestVitals.weight_kg,
      this.latestVitals.height_cm
    ].some(value => value !== null && value !== undefined);
  }
}
