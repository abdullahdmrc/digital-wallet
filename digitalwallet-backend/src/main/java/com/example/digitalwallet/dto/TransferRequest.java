package com.example.digitalwallet.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private int senderWalletId;
    private String targetIban;
    private double amount;
}
