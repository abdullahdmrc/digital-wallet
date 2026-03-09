package com.example.digitalwallet.service;


import com.example.digitalwallet.dto.TransactionRequest;
import com.example.digitalwallet.model.Transaction;
import com.example.digitalwallet.model.Wallet;
import com.example.digitalwallet.repository.TransactionRepository;
import com.example.digitalwallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Executable;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

     @Mock
     private TransactionRepository transactionRepository;

     @Mock
     private WalletRepository walletRepository;

     @InjectMocks
     private TransactionService transactionService;

    @Test
    public void pendingDepositTest() {
        TransactionRequest request = new TransactionRequest();
        request.setWalletId(1);
        request.setAmount(1500);
        request.setOppositeParty("TR11111111");
        request.setOppositePartyType(Transaction.OppositePartyType.IBAN);

        Wallet wallet = new Wallet();
        wallet.setId(1);
        wallet.setBalance(500.0);
        wallet.setUsableBalance(500.0);

        when(walletRepository.findById(1)).thenReturn(Optional.of(wallet));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.deposit(request);

        assertNotNull(result);

        assertEquals(Transaction.Status.PENDING, result.getStatus());
        assertEquals(1500, result.getAmount());
        assertEquals(500.0, wallet.getBalance());
        assertEquals(500.0, wallet.getUsableBalance());


        verify(walletRepository, times(1)).findById(1);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

     @Test
     public void approvedDepositTest(){
         TransactionRequest request = new TransactionRequest();
         request.setWalletId(1);
         request.setAmount(700);
         request.setOppositeParty("TR11111111");
         request.setOppositePartyType(Transaction.OppositePartyType.IBAN);

         Wallet wallet = new Wallet();
         wallet.setId(1);
         wallet.setBalance(500.0);
         wallet.setUsableBalance(500.0);

         when(walletRepository.findById(1)).thenReturn(Optional.of(wallet));

         when(transactionRepository.save(any(Transaction.class))).
                 thenAnswer(invocation -> invocation.getArgument(0));

         Transaction result=transactionService.deposit(request);

         assertNotNull(result);

         assertEquals(Transaction.Status.APPROVED,result.getStatus());
         assertEquals(700,result.getAmount());
         assertEquals(1200,wallet.getBalance());
         assertEquals(1200,wallet.getUsableBalance());

         verify(walletRepository,times(1)).save(wallet);
         verify(transactionRepository, times(1)).save(any(Transaction.class));

     }

     @Test
     public void withdrawInSufficientBalanceTest(){
         TransactionRequest request = new TransactionRequest();
         request.setWalletId(1);
         request.setAmount(700);
         request.setOppositeParty("TR11111111");
         request.setOppositePartyType(Transaction.OppositePartyType.IBAN);

         Wallet wallet = new Wallet();
         wallet.setId(1);
         wallet.setBalance(500.0);
         wallet.setUsableBalance(500.0);
         when(walletRepository.findById(1)).thenReturn(Optional.of(wallet));

         // hata yakalamak için böyle bir yöntem izliyoruz
         RuntimeException runtimeException=assertThrows(RuntimeException.class,() -> transactionService.withdraw(request));

         assertEquals("Insufficient balance",runtimeException.getMessage());

         verify(walletRepository,never()).save(wallet);
         verify(transactionRepository,never()).save(any(Transaction.class));
     }

    @Test
    public void approveTransactionWithPendingStatusWithDepositTypeTest() {
        int transactionId = 1;

        Wallet wallet = new Wallet();
        wallet.setBalance(1000.0);
        wallet.setUsableBalance(1000.0);

        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setAmount(500.0);
        transaction.setType(Transaction.Type.DEPOSIT);
        transaction.setStatus(Transaction.Status.PENDING);
        transaction.setWallet(wallet);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.approveTransaction(transactionId);

        assertNotNull(result);
        assertEquals(Transaction.Status.APPROVED, result.getStatus());
        assertEquals(1500.0, result.getWallet().getBalance());
        assertEquals(1500.0, result.getWallet().getUsableBalance());


        verify(transactionRepository,times(1)).findById(transactionId);
        verify(walletRepository,times(1)).save(wallet);
        verify(transactionRepository,times(1)).save(any(Transaction.class));
    }

    @Test
    public void approveTransactionWithPendingStatusWithWithDrawTypeTest(){
        int transactionId = 1;

        Wallet wallet = new Wallet();
        wallet.setBalance(1000.0);
        wallet.setUsableBalance(1000.0);

        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setAmount(500.0);
        transaction.setType(Transaction.Type.WITHDRAW);
        transaction.setStatus(Transaction.Status.PENDING);
        transaction.setWallet(wallet);


    }


}
