import { Component, OnInit } from '@angular/core';
import { Params } from '@angular/router';
import { AuthService } from '../auth.service';
import { PatientWorkflowContextService } from '../patient-workflow-context.service';

interface MenuItem {
  label: string;
  route: string;
  usesWorkflowContext?: boolean;
}

interface MenuSection {
  title: string;
  role: string;
  items: MenuItem[];
}

const patientContextItems: MenuItem[] = [
  { label: 'Patient Visitation', route: 'doctornurse-view-visitation', usesWorkflowContext: true },
  { label: 'Patient Medical', route: 'doctornurse-view-medical', usesWorkflowContext: true }
];

const medicalRecordItems: MenuItem[] = [
  { label: 'Demographics', route: 'medrec-demographics', usesWorkflowContext: true },
  { label: 'Vitals', route: 'medrec-vitals', usesWorkflowContext: true },
  { label: 'Medications', route: 'medrec-medications', usesWorkflowContext: true },
  { label: 'Labs', route: 'medrec-labs', usesWorkflowContext: true },
  { label: 'Diagnosis Records', route: 'medrec-diagnosis', usesWorkflowContext: true },
  { label: 'Treatment', route: 'medrec-treatment', usesWorkflowContext: true },
  { label: 'Imaging', route: 'medrec-imaging', usesWorkflowContext: true }
];

const doctorItems: MenuItem[] = [
  { label: 'Doctor Landing', route: 'doctorlanding' },
  { label: 'Doctor Overview', route: 'DoctorOverview' },
  { label: 'Add Diagnosis', route: 'diagnosis' },
  { label: 'Order Prescription', route: 'order-prescription' },
  { label: 'Order Radiology', route: 'order-radiology' },
  { label: 'Order Lab Test', route: 'order-lab-test' },
  ...patientContextItems,
  ...medicalRecordItems
];

const nurseItems: MenuItem[] = [
  { label: 'Nurse Landing', route: 'nurselanding' },
  { label: 'Nurse Overview', route: 'NurseOverview' },
  ...patientContextItems,
  ...medicalRecordItems
];

const intakeItems: MenuItem[] = [
  { label: 'Visitation Queue', route: 'visitation-queue' },
  { label: 'Create Visitation Record', route: 'visitation-record' },
  { label: 'Checkout Patient', route: 'checkout-patient' }
];

const checkoutItems: MenuItem[] = [
  { label: 'Visitation Queue', route: 'visitation-queue' },
  { label: 'Checkout Patient', route: 'checkout-patient' },
  { label: 'Checkout', route: 'checkout' }
];

function uniqueItems(...groups: MenuItem[][]): MenuItem[] {
  const seenRoutes = new Set<string>();
  const items: MenuItem[] = [];

  groups.flat().forEach(item => {
    if (!seenRoutes.has(item.route)) {
      seenRoutes.add(item.route);
      items.push(item);
    }
  });

  return items;
}

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {

  private readonly sections: MenuSection[] = [
    { title: 'Doctor', role: 'DOCTOR', items: doctorItems },
    { title: 'Nurse', role: 'NURSE', items: nurseItems },
    { title: 'Intake', role: 'INTAKE', items: intakeItems },
    { title: 'Checkout', role: 'CHECKOUT', items: checkoutItems },
    { title: 'Admin', role: 'ADMIN', items: uniqueItems(doctorItems, nurseItems, intakeItems, checkoutItems) }
  ];

  constructor(
    private workflowContext: PatientWorkflowContextService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
  }

  get visibleSections(): MenuSection[] {
    if (this.authService.hasAnyRole(['ADMIN'])) {
      return this.sections;
    }

    return this.sections.filter(section => this.authService.hasAnyRole([section.role]));
  }

  getQueryParams(item: MenuItem): Params | null {
    return item.usesWorkflowContext ? this.workflowContext.getQueryParams() : null;
  }
}
