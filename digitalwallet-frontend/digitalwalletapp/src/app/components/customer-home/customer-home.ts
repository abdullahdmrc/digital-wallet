import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { WalletService } from '../../services/WalletService';
import { AuthService } from '../../services/AuthService';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDialog } from '@angular/material/dialog';
import { WalletCreateDialog } from '../wallet-create-dialog/wallet-create-dialog';
import { Observable } from 'rxjs';
@Component({
  selector: 'app-customer-home',
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule, RouterLink],
  templateUrl: './customer-home.html',
  styleUrl: './customer-home.css',
})
export class CustomerHome implements OnInit {

  walletService = inject(WalletService);
  authService = inject(AuthService);
  router = inject(Router);
  dialog = inject(MatDialog)


  wallets$!: Observable<any>;

  ngOnInit(): void {
    this.wallets$ = this.walletService.getWallets();
  }

  goDetailPage(id: number) {
    this.router.navigate(['/wallet-detail-component', id]) // parametreyi root a ilettik
  }



  getWallets() {
    this.walletService.getWallets().subscribe({
      next: (data) => {
        this.wallets$ = this.walletService.getWallets();

        console.log("Gelen veriler:", data); // Konsoldan kontrol et
      },
      error: (err) => console.error("Hata oluştu:", err)
    });
  }


  //kullanıcı cüzdan olusturmak ıcın butona bastıgında dıalog açılcak
  openCreateDialog() {
    const dialogRef = this.dialog.open(WalletCreateDialog, {
      width: '400px'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.createWallet(result);
      }
    });
  }

  createWallet(data: any) {
    this.walletService.createWallet(data).subscribe({
      next: () => {

        this.getWallets();
      },
      error: (err) => console.error('Hata:', err)
    });
  }

  logOut() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }



}
