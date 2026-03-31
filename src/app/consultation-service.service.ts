import { Injectable } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { MedicationReference, SaveVisitMedicationRequest, VisitMedication } from './_visitmodels/medications';
import { PatientVitals, SavePatientVitalsRequest } from './_visitmodels/vitals';

@Injectable({
  providedIn: 'root'
})
export class ConsultationServiceService {

  constructor(private http : HttpClient) { }

  getDiagnosisList(){
    return this.http.get(`${environment.visitAPI}/DiagnosisList`)
  }

  getVisitationHistory(patientid:string){
    return this.http.get(`${environment.visitAPI}/VisitationHistory`, {params:{patientid:patientid}})
  }
  
  getMedicalRecord(patientid:string){
    return this.http.get(`${environment.visitAPI}/MedicalRecord`, {params:{patientid:patientid}})
  }

  getPatientVitals(patientid: string, visitid?: string): Observable<PatientVitals>{
    const params: Record<string, string> = { patientid };
    if (visitid) {
      params['visitid'] = visitid;
    }

    return this.http.get<PatientVitals>(`${environment.visitAPI}/api/v1/medical-records/vitals`, { params });
  }

  savePatientVitals(payload: SavePatientVitalsRequest): Observable<PatientVitals>{
    return this.http.post<PatientVitals>(`${environment.visitAPI}/api/v1/medical-records/vitals`, payload);
  }

  getMedicationReferences(): Observable<MedicationReference[]>{
    return this.http.get<MedicationReference[]>(`${environment.visitAPI}/api/v1/medical-records/medications/reference`);
  }

  getVisitMedications(patientid: string, visitid?: string): Observable<VisitMedication[]>{
    const params: Record<string, string> = { patientid };
    if (visitid) {
      params['visitid'] = visitid;
    }

    return this.http.get<VisitMedication[]>(`${environment.visitAPI}/api/v1/medical-records/medications`, { params });
  }

  saveVisitMedication(payload: SaveVisitMedicationRequest): Observable<VisitMedication>{
    return this.http.post<VisitMedication>(`${environment.visitAPI}/api/v1/medical-records/medications`, payload);
  }

  getDoctorID(): Observable<string>{
    return this.http.get(`${environment.visitAPI}/doctorID`, { responseType: 'text' })
  }

  getNurseID(): Observable<string>{
    return this.http.get(`${environment.visitAPI}/nurseID`, { responseType: 'text' })
  }

  addOrder(orderResource){
    return this.http.post(`${environment.visitAPI}/order`, orderResource)
  }

  updateVisitSummary(visitResource){
    return this.http.put(`${environment.visitAPI}/updateVisitSummary`, visitResource)
  }
}
