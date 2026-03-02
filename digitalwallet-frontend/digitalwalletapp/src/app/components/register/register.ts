import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators ,AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';

import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTooltip } from '@angular/material/tooltip';
import { DOCUMENT } from '@angular/core';

import { AuthService } from '../../services/AuthService';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatFormFieldModule,
    MatTooltip,
    
  ],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  

  registerForm = this.fb.group({
    name: ['', Validators.required],
    surname: ['', Validators.required],
    tckn: ['', Validators.required,Validators.minLength(11)],
    username: ['', [Validators.required]],
    password: ['', [Validators.required,Validators.minLength(6),this.hasNumber,this.hasSpecialCharacter,this.hasUppercase]]
  });

  onRegister() {
    
    if (this.registerForm.invalid) {
      alert("Gerekli alanları düzgün doldurduğunuzdan emin olun")
      return;
    }
      
    const requestData = {
      name: this.registerForm.value.name,
      surname: this.registerForm.value.surname,
      tckn: this.registerForm.value.tckn,
      user: {
        username: this.registerForm.value.username,
        password: this.registerForm.value.password
      }
    };

    this.authService.register(requestData)
      .subscribe({
        next: () => {
          alert("Kayıt başarılı!");
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error(err);
          alert("Kayıt başarısız!");
        }
      });
  }
  // şifrede en az bir buyuk harf var mı kontrol
    hasUppercase(control: AbstractControl) {
        const value = control.value;
        if (value && !/[A-Z]/.test(value)) {
            return { uppercase: true };
        }
        return null;
    }

    // şifrede en az bir sayı var mı kontrol
    hasNumber(control: AbstractControl) {
        const value = control.value;
        if (value && !/\d/.test(value)) {
            return { number: true };
        }
        return null;
    }

    // şifrede en az bir özel karakter var mı kontrol
    hasSpecialCharacter(control: AbstractControl) {
        const value = control.value;
        if (value && !/[!@#$%^&*(),.?":{}|<>]/.test(value)) {
            return { specialCharacter: true };
        }
        return null;
    }
}