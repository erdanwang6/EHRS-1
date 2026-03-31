import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Entry } from '../_visitmodels/entry';
import { AuthService } from '../auth.service';
import { PatientWorkflowContextService } from '../patient-workflow-context.service';
import { VisitationQueueService } from '../visitation-queue.service';

@Component({
  selector: 'app-nurse-landing',
  templateUrl: './nurse-landing.component.html',
  styleUrls: ['./nurse-landing.component.css']
})
export class NurseLandingComponent implements OnInit {
  receivePatientMessage = '';

  constructor(
    private router: Router,
    private authService: AuthService,
    private workflowContext: PatientWorkflowContextService,
    private queueService: VisitationQueueService
  ) { }

  ngOnInit(): void {
  }


  onReceivePatient() {
    this.receivePatientMessage = '';
    this.queueService.queueTriage().subscribe(entries => {
      const longestWaitingEntry = this.findLongestWaitingEntry(entries);
      if (longestWaitingEntry) {
        this.navigateWithEntry(longestWaitingEntry);
        return;
      }

      this.receivePatientMessage = 'No patients are currently waiting for triage.';
    });
  }

  private navigateWithEntry(entry?: Entry): void {
    if (entry) {
      this.workflowContext.setFromEntry(entry);
    }

    this.router.navigate(['/home/NurseOverview'], {
      queryParams: this.workflowContext.getQueryParams()
    });
  }

  get displayName(): string {
    return this.authService.getCurrentUser()?.displayName || 'Nurse';
  }

  private findLongestWaitingEntry(entries: Entry[]): Entry | undefined {
    return (entries || [])
      .slice()
      .sort((left, right) => left.visit_date.localeCompare(right.visit_date))[0];
  }

}
