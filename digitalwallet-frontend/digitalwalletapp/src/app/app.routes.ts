import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { CustomerHome } from './components/customer-home/customer-home';
import { WalletDetailComponent } from './components/wallet-detail-component/wallet-detail-component';



export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'customer-home', component: CustomerHome },
  {path :'wallet-detail-component/:id',component: WalletDetailComponent}, // parametreli root 
  { path: '', redirectTo: 'login', pathMatch: 'full' }
  

]; 
                              
