import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { Observable, BehaviorSubject, switchMap } from 'rxjs';
import { WalletService } from '../../services/WalletService';
import { Router } from '@angular/router';
import { AuthService } from '../../services/AuthService';

@Component({
  selector: 'app-admin',
  imports: [CommonModule, MatInputModule, MatButtonModule, MatCardModule, MatIconModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin implements OnInit {

  private walletService = inject(WalletService)
  private authService = inject(AuthService)
  private router = inject(Router)

  private refreshTrigger = new BehaviorSubject<void>(undefined);
  wallets$!: Observable<any>;

  ngOnInit(): void {
    this.wallets$ = this.refreshTrigger.pipe(
      switchMap(() => this.walletService.getWallets())
    );
  }

  goDetailPage(id: number) {
    this.router.navigate(['/wallet-detail-component', id])
  }

  getWallets() {
    this.refreshTrigger.next();
  }

  goToApproveDeny() {
    this.router.navigate(['approve-deny'])
  }

  logOut() {
    this.authService.logout()
    this.router.navigate(['/login'])

  }

}
