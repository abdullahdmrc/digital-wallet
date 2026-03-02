import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-wallet-create-dialog',
  imports: [MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, ReactiveFormsModule],
  templateUrl: './wallet-create-dialog.html',
  styleUrl: './wallet-create-dialog.css',
})
export class WalletCreateDialog {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<WalletCreateDialog>);

  walletForm = this.fb.group({
    walletName: ['', Validators.required],
    currency: ['TRY', Validators.required] 
  });

  onSave() {
    if (this.walletForm.valid) {
      this.dialogRef.close(this.walletForm.value);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}
