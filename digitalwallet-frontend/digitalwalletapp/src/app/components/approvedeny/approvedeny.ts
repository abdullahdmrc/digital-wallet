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
import { Observable } from 'rxjs';



@Component({
  selector: 'app-approvedeny',
  imports: [DecimalPipe, NgClass, MatButtonModule, MatCardModule, MatIconModule, MatInputModule, MatFormFieldModule, CommonModule],
  templateUrl: './approvedeny.html',
  styleUrl: './approvedeny.css',
})
export class Approvedeny implements OnInit {
  private walletService = inject(WalletService);

  transactions: any = [];

  ngOnInit(): void {
    this.getTransactions()
  }

  getTransactions() {
    this.walletService.getAllTransactions().subscribe(
      data => {
        this.transactions = data;
      },
      error => {
        console.log("Hata: ", error);
      }
    )
  }

  hasStatus(status: string): boolean {
    return this.transactions.some((tx: any) => tx.status === status);
  }


  approve(id: any) {
  this.walletService.approveTransaction(id).subscribe({
    next: () => {
      console.log("İşlem onaylandı");
      this.getTransactions(); 
    },
    error: err => console.error("Onay hatası:", err)
  });
}

  deny(id: any){
    this.walletService.denyTransaction(id).subscribe(
     {
      next: ()=>{
        console.log("İşlem reddedildi");
        this.getTransactions();
      },
      error: err => console.error("Red hatası: ",err)
     }
    );
  }



}
