package com.example.digitalwallet.repository;

import com.example.digitalwallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Integer> {
        List<Wallet> getWalletByCustomer_Id(int customerİd);



}
