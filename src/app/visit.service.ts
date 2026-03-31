import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AssignDoctorRequest, Visit, VisitResponse } from './_visitmodels/visit';

@Injectable({
  providedIn: 'root'
})
export class VisitService {

  constructor(private http: HttpClient) { }

  addVisit(visit: Visit): Observable<VisitResponse>{
    return this.http.post<VisitResponse>(`${environment.visitAPI}/visit`, visit);
  }

  assignDoctor(payload: AssignDoctorRequest): Observable<VisitResponse>{
    return this.http.put<VisitResponse>(`${environment.visitAPI}/api/v1/visits/assign-doctor`, payload);
  }
}
