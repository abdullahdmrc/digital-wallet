import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-transaction-creation',
  imports: [CommonModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, ReactiveFormsModule],
  templateUrl: './transaction-creation.html',
  styleUrl: './transaction-creation.css',
})
export class TransactionCreation {
  // burda deposit ve withdraw işlemlerini handle edicez burda id alıyoruz requestten onu aynı sekılde routtan alıcaz
  // direk kullanıcı gırmıycek , diğer taraftan currency ı de oto almak daha mantıklı cunku zaten wallet detailden
  // girip işlemleri  yaptıgından detaylarını goruntuledıgımız kartın currecnysını backend tarafına ıletebılırız.

  private fb =inject(FormBuilder)
  private dialogRef=inject(MatDialogRef<TransactionCreation>)

  transactionForm = this.fb.group({
    amount: ['', [Validators.required, Validators.min(0.1)]],
    type: ['', Validators.required], 
    oppositePartyType: [''],
    oppositeParty: ['', Validators.required],
    spendingCategory: ['']
  });

  ngOnInit() {
    this.transactionForm.get('type')?.valueChanges.subscribe(type => {
      if (type === 'TRANSFER') {
        this.transactionForm.get('oppositePartyType')?.clearValidators();
        this.transactionForm.get('spendingCategory')?.clearValidators();
      } else {
        this.transactionForm.get('oppositePartyType')?.setValidators([Validators.required]);
        this.transactionForm.get('spendingCategory')?.setValidators([Validators.required]);
      }
      this.transactionForm.get('oppositePartyType')?.updateValueAndValidity();
      this.transactionForm.get('spendingCategory')?.updateValueAndValidity();
    });
  }

  onSave() {
    if (this.transactionForm.valid) {
      this.dialogRef.close(this.transactionForm.value);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }

}
