package com.example.digitalwallet.controller;


import com.example.digitalwallet.dto.TransactionRequest;
import com.example.digitalwallet.model.Transaction;
import com.example.digitalwallet.service.impl.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final com.example.digitalwallet.service.impl.PdfService pdfService;

    @PostMapping("api/deposits")
    public ResponseEntity<Transaction> deposit(@RequestBody TransactionRequest transactionRequest){
        return ResponseEntity.ok(transactionService.deposit(transactionRequest));
    }

    @PostMapping("api/withdraws")
    public ResponseEntity<Transaction> withDraw(@RequestBody TransactionRequest transactionRequest){
        return ResponseEntity.ok(transactionService.withdraw(transactionRequest));
    }

    @GetMapping("api/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions(){
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("api/wallets/{id}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionsByWallet(@PathVariable int id){
        return ResponseEntity.ok(transactionService.getAllTransactionByWallet(id));
    }

    @PostMapping("api/transactions/approve/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Transaction> approveTransaction(@PathVariable int id) {
        return ResponseEntity.ok(transactionService.approveTransaction(id));
    }

    @PostMapping("api/transactions/deny/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Transaction> denyTransaction(@PathVariable int id) {
        return ResponseEntity.ok(transactionService.denyTransaction(id));
    }

    @PostMapping("api/transactions/transfer")
    public ResponseEntity<Transaction> transferViaIban(@RequestBody com.example.digitalwallet.dto.TransferRequest transferRequest) {
        return ResponseEntity.ok(transactionService.transferViaIban(transferRequest));
    }

    @GetMapping(value = "api/transactions/{id}/receipt", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getReceipt(@PathVariable int id) {
        Transaction transaction = transactionService.getTransactionById(id);
        byte[] pdfBytes = pdfService.generateReceiptPdf(transaction);
        
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentDispositionFormData("attachment", "dekont_" + id + ".pdf");
        
        return new ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);
    }

}
