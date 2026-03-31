import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ConsultationServiceService } from '../consultation-service.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PatientWorkflowContextService } from '../patient-workflow-context.service';

@Component({
	selector: 'app-doctornurse-view-visitation',
	templateUrl: './doctornurse-view-visitation.component.html',
	styleUrls: ['./doctornurse-view-visitation.component.css']
})
export class DoctorNurseViewVisitationComponent implements OnInit {
	visitationData:any[] = [];
	historySummary = 'Loading visitation history...';

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
			this.visitationData = [];
			this.historySummary = 'No visitation history found for that patient.';
			return;
		}

		this.service.getVisitationHistory(patientId).subscribe(data =>{
			this.visitationData = Array.isArray(data) ? data : [data];
			this.historySummary = this.visitationData.length
				? this.visitationData
						.map(visit => `${visit.visit_date} | ${visit.visit_reason} | ${visit.status}`)
						.join(' || ')
				: 'No visitation history found for that patient.';
		});
	}
}
