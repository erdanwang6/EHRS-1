import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ViewableAreaComponent } from './viewable-area/viewable-area.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import {ViewableMedicalrecordsDemographicsComponent} from './viewable-medicalrecords-demographics/viewable-medicalrecords-demographics.component';
import {ViewableMedicalrecordsDiagnosisComponent} from './viewable-medicalrecords-diagnosis/viewable-medicalrecords-diagnosis.component';
import {ViewableMedicalrecordsImagingComponent} from './viewable-medicalrecords-imaging/viewable-medicalrecords-imaging.component';
import {ViewableMedicalrecordsLabsComponent} from './viewable-medicalrecords-labs/viewable-medicalrecords-labs.component';
import {ViewableMedicalrecordsMedicationsComponent} from './viewable-medicalrecords-medications/viewable-medicalrecords-medications.component';
import {ViewableMedicalrecordsTreatmentComponent} from './viewable-medicalrecords-treatment/viewable-medicalrecords-treatment.component';
import {ViewableMedicalrecordsVitalsComponent} from './viewable-medicalrecords-vitals/viewable-medicalrecords-vitals.component';
import { ViewableVisitationrecordComponent } from './viewable-visitationrecord/viewable-visitationrecord.component';
import { ViewableVisitationcheckoutComponent } from './viewable-visitationcheckout/viewable-visitationcheckout.component';
import { ViewableVisitationqueueComponent } from './viewable-visitationqueue/viewable-visitationqueue.component';
import { ViewablePatientcheckoutComponent } from './viewable-patientcheckout/viewable-patientcheckout.component';
import { ViewableVisitationrecordResolver } from './viewable-visitationrecord/viewable-visitationrecord.resolver';
import { DoctorDiagnosisComponent } from './doctor-diagnosis/doctor-diagnosis.component';
import { DoctorOrderPrescriptionComponent } from './doctor-order-prescription/doctor-order-prescription.component';
import { DoctorOrderRadiologyComponent } from './doctor-order-radiology/doctor-order-radiology.component';
import { DoctorOrderLabtestComponent } from './doctor-order-labtest/doctor-order-labtest.component';
import { DoctorNurseViewVisitationComponent } from './doctornurse-view-visitation/doctornurse-view-visitation.component';
import { DoctorNurseViewMedicalComponent } from './doctornurse-view-medical/doctornurse-view-medical.component';
import { DoctorLandingComponent } from './doctor-landing/doctor-landing.component';
import { DoctorOverwiewComponent } from './doctor-overwiew/doctor-overwiew.component';
import { NurseLandingComponent } from './nurse-landing/nurse-landing.component';
import { NurseOverwiewComponent } from './nurse-overwiew/nurse-overwiew.component';
import { AuthGuard } from './auth.guard';
import { LoginRedirectGuard } from './login-redirect.guard';
import { RoleGuard } from './role.guard';

const routes: Routes = [
  { path: '', redirectTo:'/login', pathMatch:"full"},
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard], canActivateChild: [AuthGuard], children: [
    { path: 'medrec-demographics', component: ViewableMedicalrecordsDemographicsComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'NURSE', 'ADMIN'] }},
    { path: 'medrec-vitals', component: ViewableMedicalrecordsVitalsComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'NURSE', 'ADMIN'] }},
    { path: 'medrec-medications', component: ViewableMedicalrecordsMedicationsComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'NURSE', 'ADMIN'] }},
    { path: 'medrec-labs', component: ViewableMedicalrecordsLabsComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'NURSE', 'ADMIN'] }},
    { path: 'medrec-diagnosis', component: ViewableMedicalrecordsDiagnosisComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'NURSE', 'ADMIN'] }},
    { path: 'medrec-treatment', component: ViewableMedicalrecordsTreatmentComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'NURSE', 'ADMIN'] }},
    { path: 'medrec-imaging', component: ViewableMedicalrecordsImagingComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'NURSE', 'ADMIN'] }},
    { path: 'visitation-record', component: ViewableVisitationrecordComponent, resolve: {patients: ViewableVisitationrecordResolver}, canActivate: [RoleGuard], data: { roles: ['INTAKE', 'ADMIN'] }},
    { path: 'visitation-queue', component: ViewableVisitationqueueComponent, canActivate: [RoleGuard], data: { roles: ['INTAKE', 'CHECKOUT', 'ADMIN'] }},
    { path: 'checkout-patient', component: ViewableVisitationcheckoutComponent, canActivate: [RoleGuard], data: { roles: ['INTAKE', 'CHECKOUT', 'ADMIN'] }},
    { path: 'checkout', component: ViewablePatientcheckoutComponent, canActivate: [RoleGuard], data: { roles: ['CHECKOUT', 'ADMIN'] }},
    { path: 'diagnosis', component: DoctorDiagnosisComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'ADMIN'] }},
    { path: 'doctornurse-view-visitation', component: DoctorNurseViewVisitationComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'NURSE', 'ADMIN'] }},
    { path: 'doctornurse-view-medical', component: DoctorNurseViewMedicalComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'NURSE', 'ADMIN'] }},
    { path: 'order-prescription', component: DoctorOrderPrescriptionComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'ADMIN'] }},
    { path: 'order-radiology', component: DoctorOrderRadiologyComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'ADMIN'] }},
    { path: 'order-lab-test', component: DoctorOrderLabtestComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'ADMIN'] }},
    { path: 'nurselanding', component: NurseLandingComponent, canActivate: [RoleGuard], data: { roles: ['NURSE', 'ADMIN'] }},
    { path: 'doctorlanding', component: DoctorLandingComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'ADMIN'] }},
    { path: 'DoctorOverview', component: DoctorOverwiewComponent, canActivate: [RoleGuard], data: { roles: ['DOCTOR', 'ADMIN'] }},
    { path: 'NurseOverview', component: NurseOverwiewComponent, canActivate: [RoleGuard], data: { roles: ['NURSE', 'ADMIN'] }}  ]},
  { path: 'login', component: LoginComponent, canActivate: [LoginRedirectGuard] },
  { path: '**', component: PageNotFoundComponent }  // Wildcard route for a 404 page
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
