package com.example.digitalwallet.controller;

import com.example.digitalwallet.dto.WalletRequest;
import com.example.digitalwallet.model.Wallet;
import com.example.digitalwallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("api/wallets")
    public ResponseEntity<Wallet> createWallet(@RequestBody WalletRequest walletRequest){
        return ResponseEntity.ok(walletService.createWallet(walletRequest));
    }

    @GetMapping("api/wallets")
    public ResponseEntity<List<Wallet>> getAllWallet()
    {
        return ResponseEntity.ok(walletService.getAllWallets());
    }


}
