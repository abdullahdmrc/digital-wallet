import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import {jwtDecode} from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = 'http://localhost:8081/api/auth';

  constructor(private http: HttpClient) {}

  login(data: any) {
  return this.http.post(`${this.baseUrl}/login`, data, { responseType: 'text' })
    .pipe(
      tap(token => {
        localStorage.setItem('token', token); 
      })
    );
}

register(data: any) {
  return this.http.post(`${this.baseUrl}/register`, data, { responseType: 'text' })
    .pipe(
      tap(token => {
        localStorage.setItem('token', token);
      })
    );
}
  getUserRole(): string | null {
    const token = this.getToken();
    if (!token) return null;

    const decoded: any = jwtDecode(token);
    return decoded.role;
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;

    const decoded: any = jwtDecode(token);
    const now = Date.now() / 1000;

    return decoded.exp < now;
  }

  logout() {
    localStorage.removeItem('token');
  }

  getToken() {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}