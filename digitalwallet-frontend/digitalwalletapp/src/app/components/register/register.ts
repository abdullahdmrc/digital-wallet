import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';

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
    MatFormFieldModule
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
    tckn: ['', Validators.required],
    username: ['', [Validators.required]],
    password: ['', Validators.required]
  });

  onRegister() {
    
    if (this.registerForm.invalid) {
      alert("alanları doğru doldurun")
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
}