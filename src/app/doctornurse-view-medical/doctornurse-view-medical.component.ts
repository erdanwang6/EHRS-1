import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ConsultationServiceService } from '../consultation-service.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PatientWorkflowContextService } from '../patient-workflow-context.service';

@Component({
	selector: 'app-doctornurse-view-medical',
	templateUrl: './doctornurse-view-medical.component.html',
	styleUrls: ['./doctornurse-view-medical.component.css']
})
export class DoctorNurseViewMedicalComponent implements OnInit {
	medicalData:any[] = [];
	medicalMessage = 'Loading medical record...';

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private service: ConsultationServiceService,
		private fb: FormBuilder,
		private workflowContext: PatientWorkflowContextService
	) { 
		
	}

	ngOnInit(){
		this.getDataFromApi();
	}

	getDataFromApi() {
		const context = this.workflowContext.setFromQueryParams(this.route.snapshot.queryParams);
		const patientId = context.patientid || this.workflowContext.getContext().patientid;
		if (!patientId) {
			this.medicalData = [];
			this.medicalMessage = 'No medical record found for that patient.';
			return;
		}

		this.service.getMedicalRecord(patientId).subscribe(data =>{
			this.medicalData = Array.isArray(data) ? data : [data];
			this.medicalMessage = this.medicalData.length
				? `${this.medicalData.length} medical record section(s) loaded.`
				: 'No medical record found for that patient.';
		});
	}
}
