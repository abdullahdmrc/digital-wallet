import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { CustomerHome } from './components/customer-home/customer-home';



export const routes: Routes = [
   { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'customer-home', component: CustomerHome },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
 

]; 
                              
