package com.example.digitalwallet.service;

import com.example.digitalwallet.dto.TransactionRequest;
import com.example.digitalwallet.model.Transaction;
import com.example.digitalwallet.model.User;
import com.example.digitalwallet.model.Wallet;
import com.example.digitalwallet.repository.TransactionRepository;
import com.example.digitalwallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public Transaction deposit(TransactionRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.Type.DEPOSIT);
        transaction.setOppositeParty(request.getOppositeParty());
        transaction.setOppositePartyType(request.getOppositePartyType());

        if (request.getAmount() >= 1000) {
            transaction.setStatus(Transaction.Status.PENDING);
        } else {
            transaction.setStatus(Transaction.Status.APPROVED);
            wallet.setBalance(wallet.getBalance() + request.getAmount());
            walletRepository.save(wallet);
        }

        return transactionRepository.save(transaction);
    }


    public Transaction withdraw(TransactionRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));


        if (wallet.getUsableBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.Type.WITHDRAW);
        transaction.setOppositeParty(request.getOppositeParty());
        transaction.setOppositePartyType(request.getOppositePartyType());

        if (request.getAmount() >= 1000) {
            transaction.setStatus(Transaction.Status.PENDING);
        } else {
            transaction.setStatus(Transaction.Status.APPROVED);
            wallet.setBalance(wallet.getBalance() - request.getAmount());
            walletRepository.save(wallet);
        }

        return transactionRepository.save(transaction);
    }

    @Transactional // bu key word hem approve hem de bakiye yansıması ıcın gereklı bır notasyon
    public Transaction approveTransaction(int transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (transaction.getStatus() != Transaction.Status.PENDING) {
            throw new RuntimeException("Only pending transactions can be approved");
        }
        Wallet wallet = transaction.getWallet();

        if (transaction.getType() == Transaction.Type.DEPOSIT) {
            wallet.setBalance(wallet.getBalance() + transaction.getAmount());
        } else if (transaction.getType() == Transaction.Type.WITHDRAW) {
            if (wallet.getBalance() < transaction.getAmount()) {
                throw new RuntimeException("Insufficient balance for withdrawal approval");
            }
            wallet.setBalance(wallet.getBalance() - transaction.getAmount());
        }

        transaction.setStatus(Transaction.Status.APPROVED);
        walletRepository.save(wallet);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction denyTransaction(int transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (transaction.getStatus() != Transaction.Status.PENDING) {
            throw new RuntimeException("Only pending transactions can be denied");
        }
        transaction.setStatus(Transaction.Status.DENIED);
        return transactionRepository.save(transaction);
    }



    public List<Transaction> getAllTransactions(){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        if (currentUser.getRole() == User.Role.ROLE_EMPLOYEE) {

            return transactionRepository.findAll();
        } else {

            return transactionRepository.findAllByWallet_Customer_User_Id(currentUser.getId());
        }
    }


    public List<Transaction> getAllTransactionByWallet(int id){
        return transactionRepository.findTransactionsByWallet_Id(id);
    }



}
