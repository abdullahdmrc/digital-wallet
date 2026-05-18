import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { CustomerHome } from './components/customer-home/customer-home';
import { WalletDetailComponent } from './components/wallet-detail-component/wallet-detail-component';
import { authGuardGuard } from './guards/auth-guard-guard';
import { Approvedeny } from './components/approvedeny/approvedeny';
import { Admin } from './components/admin/admin';
import { UnauthorizedComponent } from './components/unauthorized-component/unauthorized-component';
import { AuthLayout } from './layouts/auth-layout/auth-layout';
import { MainLayout } from './layouts/main-layout/main-layout';

export const routes: Routes = [
 
  { 
    path: '',
    component: AuthLayout,
    children: [
      { path: 'login', component: Login },
      { path: 'register', component: Register },
     
      { path: '', redirectTo: 'login', pathMatch: 'full' }
    ]
  },

  
  { 
    path: '',
    component: MainLayout,
    children: [
      { path: 'customer-home', component: CustomerHome, canActivate:[authGuardGuard], data: { role: 'ROLE_CUSTOMER' } },
      { path: 'approve-deny', component: Approvedeny, canActivate:[authGuardGuard], data: { role: 'ROLE_EMPLOYEE' } },
      { path: 'wallet-detail-component/:id', component: WalletDetailComponent },
      { path: 'admin-panel', component: Admin, canActivate:[authGuardGuard], data:{ role: 'ROLE_EMPLOYEE' } },
      { path: 'unauthorized', component: UnauthorizedComponent },
      
      { path: '', redirectTo: 'customer-home', pathMatch: 'full' },
    ]
  },

  
  { path: '**', redirectTo: 'auth/login' }
];