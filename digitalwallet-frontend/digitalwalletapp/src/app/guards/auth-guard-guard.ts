import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/AuthService';
import { inject } from '@angular/core';
export const authGuardGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService)
  const router = inject(Router)

  const expectedRole = route.data?.['role'];
  const userRole = authService.getUserRole();
  console.log("ROLE:", userRole);
  console.log("EXPECTED:", expectedRole);
  if (!authService.getToken() || authService.isTokenExpired()) {
    router.navigate(['/login']);
    return false;
  }

  if (userRole!=='ROLE_EMPLOYEE' &&userRole !== expectedRole) {
    router.navigate(['/unauthorized']);
    return false;
  }

  return true;
};
