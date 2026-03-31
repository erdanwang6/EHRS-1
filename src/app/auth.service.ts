import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../environments/environment';
import { AuthSession, AuthUser, LoginResponse } from './auth.models';
import { PatientWorkflowContextService } from './patient-workflow-context.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly storageKey = 'ehrs.auth.session';
  private readonly sessionSubject = new BehaviorSubject<AuthSession | null>(this.readStoredSession());

  readonly session$ = this.sessionSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
    private workflowContext: PatientWorkflowContextService
  ) { }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.visitAPI}/api/v1/auth/login`, {
      username,
      password
    }).pipe(
      tap(response => this.setSession({
        token: response.token,
        user: response.user
      }))
    );
  }

  logout(): void {
    this.clearSession();
    this.workflowContext.clearContext();
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return !!this.sessionSubject.value?.token;
  }

  getCurrentUser(): AuthUser | null {
    return this.sessionSubject.value?.user || null;
  }

  getToken(): string | null {
    return this.sessionSubject.value?.token || null;
  }

  getHomeRoute(): string {
    return this.getCurrentUser()?.homeRoute || '/home/visitation-queue';
  }

  hasAnyRole(roles: string[]): boolean {
    if (!roles.length) {
      return true;
    }

    const currentRoles = (this.getCurrentUser()?.roles || []).map(role => role.toUpperCase());
    return roles.some(role => currentRoles.includes(role.toUpperCase()));
  }

  private setSession(session: AuthSession): void {
    localStorage.setItem(this.storageKey, JSON.stringify(session));
    this.sessionSubject.next(session);
  }

  private clearSession(): void {
    localStorage.removeItem(this.storageKey);
    this.sessionSubject.next(null);
  }

  private readStoredSession(): AuthSession | null {
    try {
      const rawValue = localStorage.getItem(this.storageKey);
      return rawValue ? JSON.parse(rawValue) as AuthSession : null;
    } catch {
      localStorage.removeItem(this.storageKey);
      return null;
    }
  }
}
