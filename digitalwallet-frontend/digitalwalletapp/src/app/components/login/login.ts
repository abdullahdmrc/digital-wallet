import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';

import { AuthService } from '../../services/AuthService';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSnackBar } from '@angular/material/snack-bar';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatFormFieldModule,
    RouterLink
  ],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private snackBar=inject(MatSnackBar);

  // Reactive form oluşturuyoruz
  loginForm = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  onLogin() {
    console.log("butona basıldı")
    if (this.loginForm.invalid) return;

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.snackBar.open('Giriş başarılı ✔', 'Kapat', {
        duration: 3000,
      panelClass: ['success-snackbar']
    });
        
      const role = this.authService.getUserRole();
      if (role === 'ROLE_EMPLOYEE') {
        this.router.navigate(['/admin-panel']);
      } else if (role === 'ROLE_CUSTOMER') {
        this.router.navigate(['/customer-home']);
      }
      },
      error: err => {
        this.snackBar.open('Giriş başarısız ❌', 'Kapat', {
      duration: 3000,
      panelClass: ['error-snackbar']
      });
        console.log(err);
      }
    });
  }
}