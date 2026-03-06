import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WalletService } from '../../services/WalletService';
import { DecimalPipe } from '@angular/common';
import { NgClass } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Observable, BehaviorSubject, switchMap } from 'rxjs';

@Component({
  selector: 'app-approvedeny',
  standalone: true, // Standalone component yapısı
  imports: [DecimalPipe, NgClass, MatButtonModule, MatCardModule, MatIconModule, MatInputModule, MatFormFieldModule, CommonModule],
  templateUrl: './approvedeny.html',
  styleUrl: './approvedeny.css',
})
export class Approvedeny implements OnInit {
  private walletService = inject(WalletService);


  private refreshTrigger = new BehaviorSubject<void>(undefined);
  transactions$!: Observable<any[]>;

  ngOnInit(): void {
    
    this.transactions$ = this.refreshTrigger.pipe(
      switchMap(() => this.walletService.getAllTransactions() as Observable<any[]>)
    );
  }

  refreshData() {
    this.refreshTrigger.next();
  }

  
  hasStatus(transactions: any[], status: string): boolean {
    return transactions ? transactions.some((tx: any) => tx.status === status) : false;
  }

  approve(id: any) {
    this.walletService.approveTransaction(id).subscribe({
      next: () => {
        console.log("İşlem onaylandı");
        this.refreshData(); 
      },
      error: err => console.error("Onay hatası:", err)
    });
  }

  deny(id: any) {
    this.walletService.denyTransaction(id).subscribe({
      next: () => {
        console.log("İşlem reddedildi");
        this.refreshData(); 
      },
      error: err => console.error("Red hatası: ", err)
    });
  }
}