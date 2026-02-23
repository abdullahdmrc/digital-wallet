package com.example.digitalwallet.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "Transactions")
@Data
public class Transaction {

    public enum Type { DEPOSIT, WITHDRAW }
    public enum OppositePartyType { IBAN, PAYMENT }
    public enum Status { PENDING, APPROVED, DENIED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OppositePartyType oppositePartyType;

    @Column(nullable = false)
    private String oppositeParty; // bunu sor

    @Enumerated(EnumType.STRING)
    private Status status;
}
