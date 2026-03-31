import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
    if (!this.authService.isAuthenticated()) {
      return this.router.createUrlTree(['/login'], {
        queryParams: state.url ? { returnUrl: state.url } : {}
      });
    }

    const allowedRoles = route.data['roles'] as string[] || [];
    if (!allowedRoles.length || this.authService.hasAnyRole(allowedRoles)) {
      return true;
    }

    return this.router.createUrlTree([this.authService.getHomeRoute()]);
  }
}
