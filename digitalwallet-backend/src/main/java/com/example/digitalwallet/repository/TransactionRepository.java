package com.example.digitalwallet.repository;

import com.example.digitalwallet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findTransactionsByWallet_Id(int walletId);

    List<Transaction> findAllByWallet_Customer_User_Id(Integer userId);

    @Query(value = "SELECT * FROM transactions WHERE id = :userId ORDER BY date DESC LIMIT :number", nativeQuery = true)
    List<Transaction> findTransactionsByRecentDate(@Param("userId") int userId, @Param("number") int number);

    List<Transaction> findByWallet_Customer_User_IdOrderByDateDesc(Integer userId, org.springframework.data.domain.Pageable pageable);

    // findBy + İlişki(Wallet_Customer_User_Id) + And + DeğişkenAdı(SpendingCategory)
    List<Transaction> findByWallet_Customer_User_IdAndSpendingCategory(
            int userId,
            Transaction.SpendingCategory spendingCategory
    );


}
