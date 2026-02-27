package com.example.digitalwallet.controller;


import com.example.digitalwallet.dto.TransactionRequest;
import com.example.digitalwallet.model.Transaction;
import com.example.digitalwallet.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

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


}
