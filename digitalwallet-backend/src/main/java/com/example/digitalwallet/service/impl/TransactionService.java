package com.example.digitalwallet.service.impl;

import com.example.digitalwallet.dto.TransactionRequest;
import com.example.digitalwallet.model.Transaction;
import com.example.digitalwallet.model.User;
import com.example.digitalwallet.model.Wallet;
import com.example.digitalwallet.repository.TransactionRepository;
import com.example.digitalwallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public Transaction deposit(TransactionRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.Type.DEPOSIT);
        transaction.setOppositeParty(request.getOppositeParty());
        transaction.setOppositePartyType(request.getOppositePartyType());
        if (request.getSpendingCategory() == null) {
            throw new IllegalArgumentException("Spending category cannot be null");
        }
        transaction.setSpendingCategory(request.getSpendingCategory());

        if (request.getAmount() >= 1000) {
            transaction.setStatus(Transaction.Status.PENDING);
        } else {
            transaction.setStatus(Transaction.Status.APPROVED);
            wallet.setBalance(wallet.getBalance() + request.getAmount());
            wallet.setUsableBalance(wallet.getUsableBalance() + request.getAmount());
            walletRepository.save(wallet);
        }

        return transactionRepository.save(transaction);
    }

    @Transactional
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
        if (request.getSpendingCategory() == null) {
            throw new IllegalArgumentException("Spending category cannot be null");
        }
        transaction.setSpendingCategory(request.getSpendingCategory());

        if (request.getAmount() >= 1000) {
            transaction.setStatus(Transaction.Status.PENDING);
            wallet.setUsableBalance(wallet.getUsableBalance() - request.getAmount());
        } else {
            transaction.setStatus(Transaction.Status.APPROVED);
            wallet.setBalance(wallet.getBalance() - request.getAmount());
            wallet.setUsableBalance(wallet.getUsableBalance() - request.getAmount());
            walletRepository.save(wallet);
        }

        return transactionRepository.save(transaction);
    }

    @Transactional // bu key word hem approve hem de bakiye yansıması ıcın gereklı bır notasyon
    public Transaction approveTransaction(int transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Wallet wallet = transaction.getWallet();
        User user=wallet.getCustomer().getUser();


        if (transaction.getType() == Transaction.Type.DEPOSIT) {
            wallet.setBalance(wallet.getBalance() + transaction.getAmount());
            wallet.setUsableBalance(wallet.getUsableBalance() + transaction.getAmount());
        } else if (transaction.getType() == Transaction.Type.WITHDRAW) {
            if (wallet.getBalance() < transaction.getAmount()) {
                throw new RuntimeException("Insufficient balance for withdrawal approval");
            }
            wallet.setBalance(wallet.getBalance() - transaction.getAmount());
        }

        transaction.setStatus(Transaction.Status.APPROVED);
        walletRepository.save(wallet);

        SimpleMailMessage mailMessage=new SimpleMailMessage();
        mailMessage.setFrom("projectdigitalwallet@gmail.com");
        mailMessage.setTo(user.getUsername());
        mailMessage.setSubject(" İşleminiz hakkında");
        mailMessage.setText(transaction.getId()+" numaralı işleminiz onaylanmıştır ve cüzdan bakiyeniz güncellenmiştir.");
        mailSender.send(mailMessage);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction denyTransaction(int transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Wallet wallet = transaction.getWallet();
        User user=wallet.getCustomer().getUser();

        if (transaction.getType() == Transaction.Type.WITHDRAW) {
            wallet.setUsableBalance(wallet.getUsableBalance() + transaction.getAmount());
            walletRepository.save(wallet);
        }
        transaction.setStatus(Transaction.Status.DENIED);

        SimpleMailMessage mailMessage=new SimpleMailMessage();
        mailMessage.setFrom("projectdigitalwallet@gmail.com");
        mailMessage.setTo(user.getUsername());
        mailMessage.setSubject(" İşleminiz hakkında ");
        mailMessage.setText(transaction.getId()+ " numaralı işleminiz reddeilmiştir , cüzdan bakiyeniz değişmemiştir.");
        mailSender.send(mailMessage);
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

    public Transaction getTransactionById(int id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Transactional
    public Transaction transferViaIban(com.example.digitalwallet.dto.TransferRequest request) {
        Wallet senderWallet = walletRepository.findById(request.getSenderWalletId())
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        if (senderWallet.isBlocked() || !senderWallet.isActiveForWithdraw()) {
            throw new RuntimeException("Sender wallet is blocked or not active for withdrawal");
        }

        if (senderWallet.getUsableBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        Wallet targetWallet = walletRepository.findByIban(request.getTargetIban())
                .orElseThrow(() -> new RuntimeException("Target IBAN not found"));

        if (targetWallet.isBlocked()) {
            throw new RuntimeException("Target wallet is blocked");
        }

        // Create withdrawal transaction for sender
        Transaction senderTx = new Transaction();
        senderTx.setWallet(senderWallet);
        senderTx.setAmount(request.getAmount());
        senderTx.setType(Transaction.Type.WITHDRAW);
        senderTx.setOppositePartyType(Transaction.OppositePartyType.IBAN);
        senderTx.setOppositeParty(request.getTargetIban());
        senderTx.setSpendingCategory(Transaction.SpendingCategory.OTHER);
        senderTx.setStatus(Transaction.Status.APPROVED);

        senderWallet.setBalance(senderWallet.getBalance() - request.getAmount());
        senderWallet.setUsableBalance(senderWallet.getUsableBalance() - request.getAmount());

        // Create deposit transaction for receiver
        Transaction receiverTx = new Transaction();
        receiverTx.setWallet(targetWallet);
        receiverTx.setAmount(request.getAmount());
        receiverTx.setType(Transaction.Type.DEPOSIT);
        receiverTx.setOppositePartyType(Transaction.OppositePartyType.IBAN);
        receiverTx.setOppositeParty(senderWallet.getIban() != null ? senderWallet.getIban() : "Bilinmeyen Cüzdan");
        receiverTx.setSpendingCategory(Transaction.SpendingCategory.OTHER);
        receiverTx.setStatus(Transaction.Status.APPROVED);

        targetWallet.setBalance(targetWallet.getBalance() + request.getAmount());
        targetWallet.setUsableBalance(targetWallet.getUsableBalance() + request.getAmount());

        walletRepository.save(senderWallet);
        walletRepository.save(targetWallet);
        transactionRepository.save(receiverTx);

        return transactionRepository.save(senderTx);
    }
}
