package com.example.digitalwallet.model;


import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "Wallets")
@Data
public class Wallet {

    public enum Currency {
        TRY, USD, EUR
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private String walletName;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private boolean activeForShopping;
    private boolean activeForWithdraw;

    @Column(unique = true)
    private String iban;

    private boolean isBlocked = false;

    private double balance;
    private double usableBalance;
}
