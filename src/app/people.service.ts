import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment';
import { CreatePatientRequest, Patient, PatientDetail } from './_visitmodels/patient';
import { Doctor } from './_visitmodels/doctors';

@Injectable({
  providedIn: 'root'
})
export class PeopleService {

  constructor(private http: HttpClient){}

  getPatients(): Observable<Patient[]>{
    return this.http.get<Patient[]>(`${environment.visitAPI}/person/patient`);
  }

  createPatient(patient: CreatePatientRequest): Observable<Patient>{
    return this.http.post<Patient>(`${environment.visitAPI}/person/patient`, patient);
  }

  getPatientDetail(patientid: string): Observable<PatientDetail>{
    return this.http.get<PatientDetail>(`${environment.visitAPI}/person/patient/${patientid}`);
  }

  getDoctors(): Observable<Doctor[]>{
    return this.http.get<Doctor[]>(`${environment.visitAPI}/person/doctor`);
  }
}
