package com.example.digitalwallet.repository;

import com.example.digitalwallet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findTransactionsByWallet_Id(int walletId);

    List<Transaction> findAllByWallet_Customer_User_Id(Integer userId);
}
