//This file defines the functionality of the Doctor Diagnosis Page, including submitting the diagnosis 
//and acts as a hub for submitting vendor orders
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ConsultationServiceService } from '../consultation-service.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Entry } from '../_visitmodels/entry';
import { AuthService } from '../auth.service';
import { PatientWorkflowContextService } from '../patient-workflow-context.service';
import { VisitationQueueService } from '../visitation-queue.service';

@Component({
  selector: 'app-doctor-diagnosis',
  templateUrl: './doctor-diagnosis.component.html',
  styleUrls: ['./doctor-diagnosis.component.css']
})
export class DoctorDiagnosisComponent implements OnInit {
  public userForm:FormGroup;
  remarks: string='';
  doctorID = '';
  oldStatus = 'Waiting for Doctor';
  newStatus = 'Ready for Checkout';
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private service: ConsultationServiceService,
    private fb: FormBuilder,
    private authService: AuthService,
    private workflowContext: PatientWorkflowContextService,
    private queueService: VisitationQueueService
  ) { 
    this.userForm = this.fb.group({remarks:''});
  }

  diagnosisList: any;
  ngOnInit(){
    this.applyRouteContext();
    this.ensureWorkflowContext();
    this.getDataFromApi();
    this.doctorID = this.authService.getCurrentUser()?.doctorEmployeeId || '';
    if (!this.doctorID) {
      this.service.getDoctorID().subscribe(data => {
        this.doctorID = data;
      });
    }
  }
  
  getDataFromApi()
  {
    this.service.getDiagnosisList().subscribe(data =>{
      this.diagnosisList = data;
    });
  }
  onSubmitPrescription() {
    this.router.navigate(['/home/order-prescription'], { queryParams: this.workflowContext.getQueryParams() });
  }

  onSubmitRadiology() {
    this.router.navigate(['/home/order-radiology'], { queryParams: this.workflowContext.getQueryParams() });
  }

  onSubmitLabTest() {
    this.router.navigate(['/home/order-lab-test'], { queryParams: this.workflowContext.getQueryParams() });
  }

  onSubmitDiagnosis() {
    const context = this.workflowContext.getContext();
    this.remarks = this.userForm.get('remarks')?.value;
    const visitData = {
      visit_id: context.visitid,
      patientid: context.patientid,
      visit_summary: this.remarks,
      newStatus: this.newStatus,
      doctor_id: this.doctorID,
      status: context.status || this.oldStatus
    };
    this.service.updateVisitSummary(visitData).subscribe(data => {
      this.workflowContext.setContext({ status: this.newStatus });
      console.log("Patient sent to Checkout");
    });
    this.router.navigateByUrl(this.authService.getHomeRoute());
  }

  private applyRouteContext(): void {
    const context = this.workflowContext.setFromQueryParams(this.route.snapshot.queryParams);
    if (context.status) {
      this.oldStatus = context.status;
    }
  }

  private ensureWorkflowContext(): void {
    const context = this.workflowContext.getContext();
    if (context.patientid && context.visitid) {
      return;
    }

    this.queueService.queueDoctor().subscribe(entries => {
      if (entries.length > 0) {
        this.applyEntryContext(entries[0]);
        return;
      }

      this.queueService.getQueue().subscribe(allEntries => {
        this.applyEntryContext(allEntries[0]);
      });
    });
  }

  private applyEntryContext(entry?: Entry): void {
    const context = this.workflowContext.setFromEntry(entry);
    if (context.status) {
      this.oldStatus = context.status;
    }
  }
}
