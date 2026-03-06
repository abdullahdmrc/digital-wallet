import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { WalletService } from '../../services/WalletService';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { NgClass } from '@angular/common';
import { DecimalPipe } from '@angular/common';
import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { TransactionCreation } from '../transaction-creation/transaction-creation';
import { Observable, BehaviorSubject, switchMap } from 'rxjs';

@Component({
  selector: 'app-wallet-detail-component',
  imports: [MatButtonModule, MatCardModule, MatIconModule, MatInputModule, MatFormFieldModule, NgClass, DecimalPipe, CommonModule],
  templateUrl: './wallet-detail-component.html',
  styleUrl: './wallet-detail-component.css',
})
export class WalletDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private walletService = inject(WalletService);
  dialog = inject(MatDialog)
  id = this.route.snapshot.paramMap.get('id');

  private refreshTrigger = new BehaviorSubject<void>(undefined);

  wallet$!: Observable<any>;
  transactions$!: Observable<any[]>;

  ngOnInit(): void {
    if (this.id) {
      this.wallet$ = this.refreshTrigger.pipe(
        switchMap(() => this.walletService.getWalletById(this.id))
      );
      this.transactions$ = this.refreshTrigger.pipe(
        switchMap(() => this.walletService.getTransactionsByWallet(this.id))
      );
    }
  }

  refreshData() {
    this.refreshTrigger.next();
  }

  deposit(data: any) {
    this.walletService.deposit(data).subscribe({
      next: () => this.refreshData(),
      error: (err) => console.error('Hata:', err)
    });
  }

  withDraw(data: any) {
    this.walletService.withDraw(data).subscribe({
      next: () => this.refreshData(),
      error: (error) => alert("Yetersiz bakiye")
    });
  }

  openCreateDialog() {
    const dialogRef = this.dialog.open(TransactionCreation, {
      width: '400px',
      data: { walletId: this.id }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Backend'deki TransactionRequest ile birebir aynı objeyi oluşturuyoruz
        const requestData = {
          walletId: Number(this.id), // String'den Number'a çevirdik
          amount: Number(result.amount), // Formdan gelen değeri sayıya çevirdik
          type: result.type,
          oppositePartyType: result.oppositePartyType,
          oppositeParty: result.oppositeParty
        };

        console.log("Gönderilen Veri:", requestData);

        // Karşılaştırma yaparken direkt objenin içindeki tipi kullan
        if (requestData.type === 'WITHDRAW') {
          this.withDraw(requestData);
        } else {
          this.deposit(requestData);
        }
      }
    });
  }

}
