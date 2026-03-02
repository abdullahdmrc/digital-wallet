import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { CustomerHome } from './components/customer-home/customer-home';
import { WalletDetailComponent } from './components/wallet-detail-component/wallet-detail-component';
import { authGuardGuard } from './guards/auth-guard-guard';
import { Approvedeny } from './components/approvedeny/approvedeny';



export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'customer-home', component: CustomerHome , canActivate:[authGuardGuard] ,data: { role: 'ROLE_CUSTOMER' }},
  {path:'approve-deny', component: Approvedeny, canActivate:[authGuardGuard] ,data: { role: 'ROLE_EMPLOYEE' }},
  {path :'wallet-detail-component/:id',component: WalletDetailComponent}, // parametreli root 
  { path: '', redirectTo: 'login', pathMatch: 'full' }

  

]; 
                              
