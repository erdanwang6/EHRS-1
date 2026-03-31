export interface AuthUser {
  userId: string;
  username: string;
  firstName: string;
  lastName: string;
  displayName: string;
  roles: string[];
  primaryRole: string;
  homeRoute: string;
  doctorEmployeeId?: string | null;
  nurseId?: string | null;
  nurseEmployeeId?: string | null;
}

export interface LoginResponse {
  token: string;
  user: AuthUser;
}

export interface AuthSession {
  token: string;
  user: AuthUser;
}
