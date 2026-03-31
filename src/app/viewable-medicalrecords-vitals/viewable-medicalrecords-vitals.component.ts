import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ConsultationServiceService } from '../consultation-service.service';
import { PeopleService } from '../people.service';
import { PatientWorkflowContextService } from '../patient-workflow-context.service';
import { PatientDetail } from '../_visitmodels/patient';
import { PatientVitals, SavePatientVitalsRequest } from '../_visitmodels/vitals';

@Component({
  selector: 'app-viewable-medicalrecords-vitals',
  templateUrl: './viewable-medicalrecords-vitals.component.html',
  styleUrls: ['./viewable-medicalrecords-vitals.component.css']
})
export class ViewableMedicalrecordsVitalsComponent implements OnInit {
  patientDetail: PatientDetail | null = null;
  patientMessage = 'Loading patient details...';
  saveMessage = '';
  saveError = '';
  isSaving = false;
  latestVitals: PatientVitals | null = null;
  private patientId = '';
  private visitId = '';
  private visitStatus = '';

  vitalsForm = this.fb.group({
    systolicBp: [null, Validators.required],
    diastolicBp: [null, Validators.required],
    pulseBpm: [null, Validators.required],
    temperatureC: [null, Validators.required],
    weightKg: [null, Validators.required],
    heightCm: [null, Validators.required]
  });

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private workflowContext: PatientWorkflowContextService,
    private peopleService: PeopleService,
    private consultationService: ConsultationServiceService
  ) { }

  ngOnInit(): void {
    const context = this.workflowContext.setFromQueryParams(this.route.snapshot.queryParams);
    this.patientId = context.patientid || this.workflowContext.getContext().patientid || '';
    this.visitId = context.visitid || this.workflowContext.getContext().visitid || '';
    this.visitStatus = context.status || this.workflowContext.getContext().status || '';

    if (!this.patientId) {
      this.patientMessage = 'Select a patient before editing vitals.';
      this.vitalsForm.disable();
      return;
    }

    if (!this.visitId) {
      this.patientMessage = 'Select a patient visit before editing vitals.';
      this.vitalsForm.disable();
      return;
    }

    forkJoin({
      patientDetail: this.peopleService.getPatientDetail(this.patientId).pipe(catchError(() => of(null))),
      vitals: this.consultationService.getPatientVitals(this.patientId, this.visitId).pipe(catchError(() => of(null)))
    }).subscribe(({ patientDetail, vitals }) => {
      this.patientDetail = patientDetail;
      this.latestVitals = vitals;

      this.patientMessage = patientDetail
        ? `Editing vitals for ${patientDetail.full_name}.`
        : 'Patient details could not be loaded.';

      if (vitals) {
        this.vitalsForm.patchValue({
          systolicBp: vitals.systolic_bp ?? null,
          diastolicBp: vitals.diastolic_bp ?? null,
          pulseBpm: vitals.pulse_bpm ?? null,
          temperatureC: vitals.temperature_c ?? null,
          weightKg: vitals.weight_kg ?? null,
          heightCm: vitals.height_cm ?? null
        });
      }
    });
  }

  saveVitals(): void {
    if (!this.patientId) {
      this.saveError = 'Select a patient before saving vitals.';
      return;
    }

    if (!this.visitId) {
      this.saveError = 'Select a patient visit before saving vitals.';
      return;
    }

    if (this.vitalsForm.invalid) {
      this.vitalsForm.markAllAsTouched();
      this.saveError = 'Please complete all required vitals fields.';
      return;
    }

    const payload: SavePatientVitalsRequest = {
      patientid: this.patientId,
      visitid: this.visitId,
      systolic_bp: Number(this.vitalsForm.value.systolicBp),
      diastolic_bp: Number(this.vitalsForm.value.diastolicBp),
      pulse_bpm: Number(this.vitalsForm.value.pulseBpm),
      temperature_c: Number(this.vitalsForm.value.temperatureC),
      weight_kg: Number(this.vitalsForm.value.weightKg),
      height_cm: Number(this.vitalsForm.value.heightCm)
    };

    this.isSaving = true;
    this.saveMessage = '';
    this.saveError = '';

    this.consultationService.savePatientVitals(payload).subscribe({
      next: savedVitals => {
        this.latestVitals = savedVitals;
        this.saveMessage = `Vitals saved for ${this.patientDetail?.full_name || 'the current patient'}.`;
        this.isSaving = false;
      },
      error: error => {
        this.saveError = error?.error?.message || 'Could not save patient vitals.';
        this.isSaving = false;
      }
    });
  }

  get currentVisitStatus(): string {
    return this.visitStatus || 'Unknown';
  }
}
