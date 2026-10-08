import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const token = this.authService.getToken();
    const userRole = this.authService.getUserRole();

    if (!token) {
      return this.router.parseUrl('/login');
    }

    const expectedRole = route.data['role'];
    if (expectedRole && userRole && expectedRole.toLowerCase() !== userRole.toLowerCase()) {
      if (userRole.toLowerCase() === 'admin') {
        return this.router.parseUrl('/homePage');
      } else {
        return this.router.parseUrl('/homePage');
      }
    }

    return true;
  }
}
