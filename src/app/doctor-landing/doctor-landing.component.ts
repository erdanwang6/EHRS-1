import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Entry } from '../_visitmodels/entry';
import { AuthService } from '../auth.service';
import { PatientWorkflowContextService } from '../patient-workflow-context.service';
import { VisitationQueueService } from '../visitation-queue.service';

@Component({
  selector: 'app-doctor-landing',
  templateUrl: './doctor-landing.component.html',
  styleUrls: ['./doctor-landing.component.css']
})
export class DoctorLandingComponent implements OnInit {
  receivePatientMessage = '';

  constructor(
    private router: Router,
    private authService: AuthService,
    private workflowContext: PatientWorkflowContextService,
    private queueService: VisitationQueueService
  ) { }

  ngOnInit(): void {
  }

  onReceivePatientDoctor() {
    this.receivePatientMessage = '';
    this.queueService.queueDoctor().subscribe(entries => {
      const longestWaitingEntry = this.findLongestWaitingEntry(entries);
      if (longestWaitingEntry) {
        this.navigateWithEntry(longestWaitingEntry);
        return;
      }

      this.receivePatientMessage = 'No patients are currently waiting for this doctor.';
    });
  }

  private navigateWithEntry(entry?: Entry): void {
    if (entry) {
      this.workflowContext.setFromEntry(entry);
    }

    this.router.navigate(['/home/DoctorOverview'], {
      queryParams: this.workflowContext.getQueryParams()
    });
  }

  get displayName(): string {
    return this.authService.getCurrentUser()?.displayName || 'Doctor';
  }

  private findLongestWaitingEntry(entries: Entry[]): Entry | undefined {
    return (entries || [])
      .slice()
      .sort((left, right) => left.visit_date.localeCompare(right.visit_date))[0];
  }

}
