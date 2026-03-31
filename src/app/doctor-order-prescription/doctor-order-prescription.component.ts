//This file describes the order prescription component
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ConsultationServiceService } from '../consultation-service.service';
import * as uuid from 'uuid';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AuthService } from '../auth.service';
import { PatientWorkflowContextService } from '../patient-workflow-context.service';

@Component({
  selector: 'app-doctor-order-prescription',
  templateUrl: './doctor-order-prescription.component.html',
  styleUrls: ['./doctor-order-prescription.component.css']
})
export class DoctorOrderPrescriptionComponent implements OnInit {
  public userForm:FormGroup;
  order_name: string='';
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private service: ConsultationServiceService,
    private fb: FormBuilder,
    private authService: AuthService,
    private workflowContext: PatientWorkflowContextService
  ) { 
    this.userForm = this.fb.group({order_name:''});
  }

  
  ngOnInit(): void {
    this.getDataFromAPI()
  }

  

  myId = uuid.v4();

  doctorID = '';
  patientID = '';
  getDataFromAPI(){
    this.workflowContext.setFromQueryParams(this.route.snapshot.queryParams);
    this.patientID = this.workflowContext.getContext().patientid || '';
    this.doctorID = this.authService.getCurrentUser()?.doctorEmployeeId || '';
    if (!this.doctorID) {
      this.service.getDoctorID().subscribe(data => {
        this.doctorID = data;
      });
    }
  }
  onSubmitPrescription() {
    this.order_name = this.userForm.get('order_name')?.value;
    const orderData = {order_id: this.myId, employeeid: this.doctorID, patientid: this.patientID, order_type: 'medication', order_name: this.order_name}
    this.service.addOrder(orderData).subscribe(data => {
      console.log("Order Submitted");
    });
    this.router.navigate(['/home/diagnosis'], { queryParams: this.workflowContext.getQueryParams() });
  }
}
