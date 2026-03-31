import { Injectable } from '@angular/core';
import { Params } from '@angular/router';
import { Entry } from './_visitmodels/entry';

export interface PatientWorkflowContext {
  patientid?: string;
  visitid?: string;
  status?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PatientWorkflowContextService {
  private context: PatientWorkflowContext = {};

  setFromEntry(entry?: Entry): PatientWorkflowContext {
    if (!entry) {
      return this.getContext();
    }

    this.context = {
      ...this.context,
      patientid: entry.patientid,
      visitid: entry.visit_id,
      status: entry.status
    };

    return this.getContext();
  }

  setFromQueryParams(queryParams: Params): PatientWorkflowContext {
    const nextContext: PatientWorkflowContext = {};

    if (queryParams['patientid']) {
      nextContext.patientid = queryParams['patientid'];
    } else if (queryParams['id']) {
      nextContext.patientid = queryParams['id'];
    }

    if (queryParams['visitid']) {
      nextContext.visitid = queryParams['visitid'];
    } else if (queryParams['visit_id']) {
      nextContext.visitid = queryParams['visit_id'];
    }

    if (queryParams['status']) {
      nextContext.status = queryParams['status'];
    }

    return this.setContext(nextContext);
  }

  setContext(nextContext: PatientWorkflowContext): PatientWorkflowContext {
    this.context = {
      ...this.context,
      ...this.cleanContext(nextContext)
    };

    return this.getContext();
  }

  getContext(): PatientWorkflowContext {
    return { ...this.context };
  }

  clearContext(): void {
    this.context = {};
  }

  getQueryParams(): Params {
    const queryParams: Params = {};

    if (this.context.patientid) {
      queryParams['patientid'] = this.context.patientid;
    }
    if (this.context.visitid) {
      queryParams['visitid'] = this.context.visitid;
    }
    if (this.context.status) {
      queryParams['status'] = this.context.status;
    }

    return queryParams;
  }

  private cleanContext(nextContext: PatientWorkflowContext): PatientWorkflowContext {
    const cleaned: PatientWorkflowContext = {};

    if (nextContext.patientid) {
      cleaned.patientid = nextContext.patientid;
    }
    if (nextContext.visitid) {
      cleaned.visitid = nextContext.visitid;
    }
    if (nextContext.status) {
      cleaned.status = nextContext.status;
    }

    return cleaned;
  }
}
