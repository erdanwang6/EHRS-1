import { Component, OnInit } from '@angular/core';
import { first } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { PeopleService } from '../people.service';
import { VisitService } from '../visit.service';
import { CreatePatientRequest, Patient } from '../_visitmodels/patient';
import { Visit } from '../_visitmodels/visit';

@Component({
  selector: 'app-viewable-visitationrecord',
  templateUrl: './viewable-visitationrecord.component.html',
  styleUrls: ['./viewable-visitationrecord.component.css']
})
export class ViewableVisitationrecordComponent implements OnInit {

  patients: Patient[] = [];
  filteredPatients: Patient[] = [];
  patientSearch = '';
  patientid = '';
  createNewPatient = false;
  visitDate = this.defaultVisitDate();
  visitReason = '';
  selectedStatus = 'Waiting for Triage';
  submitError = '';
  isSubmitting = false;
  statuses: string[] = ['Waiting for Triage', 'Waiting for Doctor', 'Waiting for Payment'];
  patientSexOptions: string[] = ['Male', 'Female', 'Other', 'Unknown'];
  newPatient = this.createEmptyPatientDraft();

  constructor(
    private route: ActivatedRoute,
    private peopleService: PeopleService,
    private visitService: VisitService,
    private router: Router
  ) { }

  ngOnInit(): void {
    const resolvedPatients = this.route.snapshot.data['patients'];
    this.patients = Array.isArray(resolvedPatients) ? resolvedPatients : [];
    this.filteredPatients = this.patients.slice();

    const routePatientId = this.route.snapshot.queryParamMap.get('patientid')
      || this.route.snapshot.queryParamMap.get('id');
    if (routePatientId) {
      this.preselectPatient(routePatientId);
    }
  }

  onPatientInputChanged(value: string): void {
    this.patientSearch = value;
    this.submitError = '';
    this.filteredPatients = this.filterPatients(value);

    const exactMatch = this.patients.find(patient => patient.namebirth.toLowerCase() === (value || '').toLowerCase());
    this.patientid = exactMatch ? exactMatch.patientid : '';
  }

  onPatientSelectionChanged(event: { option: { value: string } }): void {
    const selectedPatient = this.patients.find(patient => patient.namebirth === event.option.value);
    if (!selectedPatient) {
      return;
    }

    this.patientSearch = selectedPatient.namebirth;
    this.patientid = selectedPatient.patientid;
    this.submitError = '';
  }

  onCreateNewPatientChanged(enabled: boolean): void {
    this.createNewPatient = enabled;
    this.submitError = '';

    if (enabled) {
      this.patientSearch = '';
      this.patientid = '';
      return;
    }

    this.newPatient = this.createEmptyPatientDraft();
  }

  onSubmit(visitationForm: NgForm): void {
    if (!this.createNewPatient && !this.patientid) {
      this.submitError = 'Please choose a patient from the autocomplete list.';
      return;
    }

    if (visitationForm.invalid) {
      this.submitError = 'Please complete the required fields.';
      return;
    }

    this.isSubmitting = true;
    this.submitError = '';

    if (this.createNewPatient) {
      this.createPatientAndVisit();
      return;
    }

    this.createVisitForPatient(this.patientid);
  }

  get submitButtonLabel(): string {
    if (this.isSubmitting) {
      return this.createNewPatient ? 'Saving Patient...' : 'Submitting...';
    }

    return this.createNewPatient ? 'Save Patient and Record' : 'Submit New Record';
  }

  private createPatientAndVisit(): void {
    const payload: CreatePatientRequest = {
      firstName: this.newPatient.firstName,
      middleName: this.newPatient.middleName,
      lastName: this.newPatient.lastName,
      dateOfBirth: this.newPatient.dateOfBirth,
      sex: this.newPatient.sex
    };

    this.peopleService.createPatient(payload).pipe(first()).subscribe({
      next: createdPatient => {
        this.patients = [...this.patients, createdPatient];
        this.filteredPatients = this.patients.slice();
        this.patientid = createdPatient.patientid;
        this.patientSearch = createdPatient.namebirth;
        this.createNewPatient = false;
        this.newPatient = this.createEmptyPatientDraft();
        this.createVisitForPatient(createdPatient.patientid);
      },
      error: error => {
        this.submitError = error?.error?.message || 'Could not save the new patient.';
        this.isSubmitting = false;
      }
    });
  }

  private createVisitForPatient(patientId: string): void {
    const visit: Visit = {
      patientid: patientId,
      visit_date: this.visitDate,
      status: this.selectedStatus,
      visit_reason: this.visitReason
    };

    this.visitService.addVisit(visit).pipe(first()).subscribe({
      next: () => {
        this.router.navigate(['/home/visitation-queue']);
      },
      error: error => {
        this.submitError = error?.error?.message || 'Could not submit the visitation record.';
        this.isSubmitting = false;
      }
    });
  }

  private filterPatients(value: string): Patient[] {
    const filterValue = (value || '').toLowerCase();
    return this.patients.filter(patient => patient.namebirth.toLowerCase().includes(filterValue));
  }

  private preselectPatient(patientId: string): void {
    const selectedPatient = this.patients.find(patient => patient.patientid === patientId);
    if (!selectedPatient) {
      return;
    }

    this.patientid = selectedPatient.patientid;
    this.patientSearch = selectedPatient.namebirth;
    this.filteredPatients = this.filterPatients(this.patientSearch);
  }

  private defaultVisitDate(): string {
    return new Date().toISOString().slice(0, 16);
  }

  private createEmptyPatientDraft() {
    return {
      firstName: '',
      middleName: '',
      lastName: '',
      dateOfBirth: '',
      sex: ''
    };
  }
}
