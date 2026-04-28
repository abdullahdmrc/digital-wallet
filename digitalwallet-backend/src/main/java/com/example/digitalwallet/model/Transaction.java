package com.example.digitalwallet.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.Date;


@Entity
@Table(name = "Transactions")
@Data
public class Transaction {

    public enum Type { DEPOSIT, WITHDRAW }
    public enum OppositePartyType { IBAN, PAYMENT }
    public enum Status { PENDING, APPROVED, DENIED }
    public enum SpendingCategory {FOOD,SHOPPING,FUEL,CINEMA,TRAVEL,OTHER}

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
    private String oppositeParty;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private Date date = Date.from(Instant.now());

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpendingCategory spendingCategory;




}
