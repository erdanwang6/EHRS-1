import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ConsultationServiceService } from '../consultation-service.service';
import { PeopleService } from '../people.service';
import { PatientWorkflowContextService } from '../patient-workflow-context.service';
import { MedicationReference, SaveVisitMedicationRequest, VisitMedication } from '../_visitmodels/medications';
import { PatientDetail } from '../_visitmodels/patient';

@Component({
  selector: 'app-viewable-medicalrecords-medications',
  templateUrl: './viewable-medicalrecords-medications.component.html',
  styleUrls: ['./viewable-medicalrecords-medications.component.css']
})
export class ViewableMedicalrecordsMedicationsComponent implements OnInit {
  patientDetail: PatientDetail | null = null;
  medicationReferences: MedicationReference[] = [];
  filteredMedicationReferences: MedicationReference[] = [];
  medications: VisitMedication[] = [];
  patientMessage = 'Loading patient details...';
  saveMessage = '';
  saveError = '';
  isSaving = false;
  private patientId = '';
  private visitId = '';
  private visitStatus = '';

  medicationsForm = this.fb.group({
    medicationName: ['', Validators.required],
    dosageInstruction: ['', Validators.required],
    frequency: ['', Validators.required],
    duration: ['', Validators.required],
    medicationStatus: ['ACTIVE', Validators.required]
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
      this.patientMessage = 'Select a patient before editing medications.';
      this.medicationsForm.disable();
      return;
    }

    if (!this.visitId) {
      this.patientMessage = 'Select a patient visit before editing medications.';
      this.medicationsForm.disable();
      return;
    }

    forkJoin({
      patientDetail: this.peopleService.getPatientDetail(this.patientId).pipe(catchError(() => of(null))),
      medicationReferences: this.consultationService.getMedicationReferences().pipe(catchError(() => of([]))),
      medications: this.consultationService.getVisitMedications(this.patientId, this.visitId).pipe(catchError(() => of([])))
    }).subscribe(({ patientDetail, medicationReferences, medications }) => {
      this.patientDetail = patientDetail;
      this.medicationReferences = Array.isArray(medicationReferences) ? medicationReferences : [];
      this.filteredMedicationReferences = this.medicationReferences.slice();
      this.medications = Array.isArray(medications) ? medications : [];

      this.patientMessage = patientDetail
        ? `Editing medications for ${patientDetail.full_name}.`
        : 'Patient details could not be loaded.';
    });
  }

  onMedicationNameChanged(value: string): void {
    const filterValue = (value || '').toLowerCase();
    this.filteredMedicationReferences = this.medicationReferences
      .filter(reference => reference.medication_name.toLowerCase().includes(filterValue));
  }

  saveMedication(): void {
    if (!this.patientId || !this.visitId) {
      this.saveError = 'Select a patient visit before saving medications.';
      return;
    }

    if (this.medicationsForm.invalid) {
      this.medicationsForm.markAllAsTouched();
      this.saveError = 'Please complete all required medication fields.';
      return;
    }

    const payload: SaveVisitMedicationRequest = {
      patientid: this.patientId,
      visitid: this.visitId,
      medication_name: (this.medicationsForm.value.medicationName || '').trim(),
      dosage_instruction: (this.medicationsForm.value.dosageInstruction || '').trim(),
      frequency: (this.medicationsForm.value.frequency || '').trim(),
      duration: (this.medicationsForm.value.duration || '').trim(),
      medication_status: this.medicationsForm.value.medicationStatus || 'ACTIVE'
    };

    this.isSaving = true;
    this.saveMessage = '';
    this.saveError = '';

    this.consultationService.saveVisitMedication(payload).subscribe({
      next: savedMedication => {
        this.medications = [savedMedication, ...this.medications];
        this.saveMessage = `Medication saved for ${this.patientDetail?.full_name || 'the current patient'}.`;
        this.isSaving = false;

        if (!this.medicationReferences.some(reference => reference.medication_name.toLowerCase() === savedMedication.medication_name.toLowerCase())) {
          const newReference: MedicationReference = {
            medication_reference_id: savedMedication.medication_reference_id,
            medication_code: '',
            medication_name: savedMedication.medication_name
          };
          this.medicationReferences = [newReference, ...this.medicationReferences];
          this.filteredMedicationReferences = this.medicationReferences.slice();
        }

        this.medicationsForm.patchValue({
          medicationName: '',
          dosageInstruction: '',
          frequency: '',
          duration: '',
          medicationStatus: 'ACTIVE'
        });
        this.medicationsForm.markAsPristine();
        this.medicationsForm.markAsUntouched();
      },
      error: error => {
        this.saveError = error?.error?.message || 'Could not save patient medication.';
        this.isSaving = false;
      }
    });
  }

  get currentVisitStatus(): string {
    return this.visitStatus || 'Unknown';
  }
}
