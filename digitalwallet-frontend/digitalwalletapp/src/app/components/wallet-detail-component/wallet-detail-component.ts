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


@Component({
  selector: 'app-wallet-detail-component',
  imports: [MatButtonModule,MatCardModule,MatIconModule,MatInputModule,MatFormFieldModule,NgClass,DecimalPipe,CommonModule],
  templateUrl: './wallet-detail-component.html',
  styleUrl: './wallet-detail-component.css',
})
export class WalletDetailComponent implements OnInit{
 private route=inject(ActivatedRoute);
 private walletService=inject(WalletService);
 dialog=inject(MatDialog)
   id=this.route.snapshot.paramMap.get('id');
 wallet : any;
 transactions: any=[];

 ngOnInit(): void {
   

   if(this.id){
    this.getWalletDetail(this.id);
    this.getTransactionsByWallet(this.id);
   }

 }

 getWalletDetail(id: String){
  this.walletService.getWalletById(id).subscribe(data =>{
    this.wallet=data
  }, error =>{
    console.log(error)
  });
 }

 getTransactionsByWallet(id: String){
  this.walletService.getTransactionsByWallet(id).subscribe(data =>{
    this.transactions=data;
  },error =>{
    console.log(error);
  })
 }

 deposit(data: any){
  
  this.walletService.deposit(data).subscribe({
      next: () => {
       this.getWalletDetail(this.id!); 
      this.getTransactionsByWallet(this.id!);
      },
      error: (err) => console.error('Hata:', err)
    })
 }

 withDraw(data: any){
  this.walletService.withDraw(data).subscribe(
    {
      next: () =>{
      this.getWalletDetail(this.id!); 
      this.getTransactionsByWallet(this.id!);
      },
      error: (error) => console.error('Hata: ',error)
    }
  )
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
