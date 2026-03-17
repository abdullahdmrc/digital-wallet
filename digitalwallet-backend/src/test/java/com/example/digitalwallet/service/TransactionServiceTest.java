package com.example.digitalwallet.service;


import com.example.digitalwallet.dto.TransactionRequest;
import com.example.digitalwallet.model.Transaction;
import com.example.digitalwallet.model.User;
import com.example.digitalwallet.model.Wallet;
import com.example.digitalwallet.repository.TransactionRepository;
import com.example.digitalwallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Executable;
import java.util.List;
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
     public void pendingWithDrawTest(){
         TransactionRequest request = new TransactionRequest();
         request.setWalletId(1);
         request.setAmount(1200);
         request.setOppositeParty("TR11111111");
         request.setOppositePartyType(Transaction.OppositePartyType.IBAN);

         Wallet wallet = new Wallet();
         wallet.setId(1);
         wallet.setBalance(5000.0);
         wallet.setUsableBalance(5000.0);

         when(walletRepository.findById(1)).thenReturn(Optional.of(wallet));

         when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

         Transaction result = transactionService.withdraw(request);

         assertNotNull(result);

         assertEquals(Transaction.Status.PENDING, result.getStatus());
         assertEquals(1200, result.getAmount());
         assertEquals(5000.0, wallet.getBalance());
         assertEquals(3800.0, wallet.getUsableBalance());


         verify(walletRepository, times(1)).findById(1);
         verify(walletRepository, never()).save(any(Wallet.class));
         verify(transactionRepository, times(1)).save(any(Transaction.class));
     }

     @Test
     public void approvedWithDrawTest(){
         TransactionRequest request = new TransactionRequest();
         request.setWalletId(1);
         request.setAmount(900);
         request.setOppositeParty("TR11111111");
         request.setOppositePartyType(Transaction.OppositePartyType.IBAN);

         Wallet wallet = new Wallet();
         wallet.setId(1);
         wallet.setBalance(5000.0);
         wallet.setUsableBalance(5000.0);

         when(walletRepository.findById(1)).thenReturn(Optional.of(wallet));

         when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

         Transaction result = transactionService.withdraw(request);

         assertNotNull(result);

         assertEquals(Transaction.Status.APPROVED, result.getStatus());
         assertEquals(900, result.getAmount());
         assertEquals(4100.0, wallet.getBalance());
         assertEquals(4100.0, wallet.getUsableBalance());


         verify(walletRepository, times(1)).findById(1);
         verify(walletRepository, times(1)).save(any(Wallet.class));
         verify(transactionRepository, times(1)).save(any(Transaction.class));
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
    public void approveTransactionWithPendingStatusWithWithDrawTypeSufficientTest(){
        int transactionId = 1;

        Wallet wallet = new Wallet();
        wallet.setBalance(4000.0);
        wallet.setUsableBalance(4000.0);

        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setAmount(1500.0);
        transaction.setType(Transaction.Type.WITHDRAW);
        transaction.setStatus(Transaction.Status.PENDING);
        transaction.setWallet(wallet);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.approveTransaction(transactionId);

        assertNotNull(result);
        assertEquals(Transaction.Status.APPROVED, result.getStatus());
        assertEquals(2500.0, result.getWallet().getBalance());

        verify(transactionRepository,times(1)).findById(transactionId);
        verify(walletRepository,times(1)).save(wallet);
        verify(transactionRepository,times(1)).save(any(Transaction.class));


    }

    @Test
    public void inSufficientWithDrawApproveTransactionTest(){
        int transactionId = 1;

        Wallet wallet = new Wallet();
        wallet.setBalance(1000.0);
        wallet.setUsableBalance(1000.0);

        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setAmount(1500.0);
        transaction.setType(Transaction.Type.WITHDRAW);
        transaction.setStatus(Transaction.Status.PENDING);
        transaction.setWallet(wallet);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        RuntimeException runtimeException=assertThrows(RuntimeException.class,() -> transactionService.approveTransaction(transactionId));
        assertEquals("Insufficient balance for withdrawal approval",runtimeException.getMessage());

        verify(transactionRepository,times(1)).findById(transactionId);
        verify(walletRepository,never()).save(wallet);
        verify(transactionRepository,never()).save(any(Transaction.class));
    }

    @Test
    public void denyDepositTransaction(){
        int transactionId = 1;

        Wallet wallet = new Wallet();
        wallet.setBalance(1000.0);
        wallet.setUsableBalance(1000.0);

        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setAmount(1500.0);
        transaction.setType(Transaction.Type.DEPOSIT);
        transaction.setStatus(Transaction.Status.PENDING);
        transaction.setWallet(wallet);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.denyTransaction(transactionId);

        assertNotNull(result);
        assertEquals(Transaction.Status.DENIED, result.getStatus());
        assertEquals(1000.0, result.getWallet().getBalance());
        assertEquals(1000.0, result.getWallet().getUsableBalance());


        verify(transactionRepository,times(1)).findById(transactionId);
        verify(walletRepository,never()).save(wallet);
        verify(transactionRepository,times(1)).save(any(Transaction.class));
    }

    @Test
    public void denyWithDrawTransactionTest(){
        int transactionId = 1;

        Wallet wallet = new Wallet();
        wallet.setBalance(2500.0);
        wallet.setUsableBalance(2500.0);

        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setAmount(1500.0);
        transaction.setType(Transaction.Type.WITHDRAW);
        transaction.setStatus(Transaction.Status.PENDING);
        transaction.setWallet(wallet);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.denyTransaction(transactionId);

        assertNotNull(result);
        assertEquals(Transaction.Status.DENIED, result.getStatus());
        assertEquals(2500.0, result.getWallet().getBalance());
        assertEquals(4000.0, result.getWallet().getUsableBalance());//2500 idi kullanılabilir, reddeildiği için +1500 ekledik


        verify(transactionRepository,times(1)).findById(transactionId);
        verify(walletRepository,times(1)).save(wallet);
        verify(transactionRepository,times(1)).save(any(Transaction.class));
    }

    @Test
    public void getAllTransactionsForEmployeeTest(){

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        User mockUser=new User();
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockUser.setRole(User.Role.ROLE_EMPLOYEE);
        List<Transaction> transactionList=List.of(new Transaction(),new Transaction());
        when(transactionRepository.findAll()).thenReturn(transactionList);

        List<Transaction> transactions=transactionService.getAllTransactions();

        assertNotNull(transactions);
        assertEquals(transactionList.size(),transactions.size());

        verify(transactionRepository,times(1)).findAll();
        verify(transactionRepository,never()).findAllByWallet_Customer_User_Id(mockUser.getId());

    }

    @Test
    public void getAllTransactionsForCustomerTest(){

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        User mockUser=new User();
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockUser.setRole(User.Role.ROLE_CUSTOMER);
        List<Transaction> transactionList=List.of(new Transaction(),new Transaction());
        when(transactionRepository.findAllByWallet_Customer_User_Id(mockUser.getId())).thenReturn(transactionList);

        List<Transaction> transactions=transactionService.getAllTransactions();

        assertNotNull(transactions);
        assertEquals(transactionList.size(),transactions.size());

        verify(transactionRepository,never()).findAll();
        verify(transactionRepository,times(1)).findAllByWallet_Customer_User_Id(mockUser.getId());

    }
    @Test
    public void getAllTransactionsByWalletTest(){
        int id=1;

        List<Transaction> transactions=List.of(new Transaction(),new Transaction(), new Transaction());
        when(transactionRepository.findTransactionsByWallet_Id(id)).thenReturn(transactions);

        List<Transaction> transactionList=transactionService.getAllTransactionByWallet(id);

        assertNotNull(transactionList);
        assertEquals(transactions.size(),transactionList.size());

        verify(transactionRepository,times(1)).findTransactionsByWallet_Id(id);

    }


}
