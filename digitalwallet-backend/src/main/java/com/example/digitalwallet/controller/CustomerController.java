package com.example.digitalwallet.controller;

import com.example.digitalwallet.dto.WalletRequest;
import com.example.digitalwallet.model.Wallet;
import com.example.digitalwallet.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private CustomerService customerService;

    @PostMapping("api/wallets")
    public ResponseEntity<Wallet> createWallet(@RequestBody WalletRequest walletRequest){
        return ResponseEntity.ok(customerService.createWallet(walletRequest));
    }
}
