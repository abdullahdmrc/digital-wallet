package com.example.digitalwallet.dto;

import com.example.digitalwallet.model.Transaction;
import lombok.Data;

@Data
public class TransactionRequest {
    private int walletId;
    private double amount;
    private Transaction.Type type;
    private Transaction.OppositePartyType oppositePartyType;
    private String oppositeParty;

}
