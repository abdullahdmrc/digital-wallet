package com.example.digitalwallet.service;


import com.example.digitalwallet.dto.WalletRequest;
import com.example.digitalwallet.model.Customer;
import com.example.digitalwallet.model.User;
import com.example.digitalwallet.model.Wallet;
import com.example.digitalwallet.repository.CustomerRepository;
import com.example.digitalwallet.repository.UserRepository;
import com.example.digitalwallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    public void successfullyCreateWallet() {
        WalletRequest walletRequest = new WalletRequest();
        walletRequest.setWalletName("yeni cüzdan");
        walletRequest.setCurrency("TRY");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn("abdullah");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User mockUser = new User();
        mockUser.setUsername("abdullah");

        Customer mockCustomer = new Customer();
        mockCustomer.setUser(mockUser);

        when(userRepository.findByUsername("abdullah")).thenReturn(Optional.of(mockUser));
        when(customerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockCustomer));

        //parametre olarak girilen değeri değiştirmeden geri döndür diyoruz ,getArguments bu bizim parametre dizimiz oluyor
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArguments()[0]);

        Wallet newWallet = walletService.createWallet(walletRequest);

        assertNotNull(newWallet);
        assertEquals("yeni cüzdan", newWallet.getWalletName());
        assertEquals(Wallet.Currency.TRY, newWallet.getCurrency());
        assertEquals(mockCustomer, newWallet.getCustomer());


        verify(walletRepository, times(1)).save(any(Wallet.class));

        //auth işlemini sonlandırdık
        SecurityContextHolder.clearContext();
    }

    @Test
    public void successfullyListAllWalletsForEmployee() {
        User adminUser = new User();
        adminUser.setRole(User.Role.ROLE_EMPLOYEE);

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(auth.getPrincipal()).thenReturn(adminUser);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        List<Wallet> allWallets = List.of(new Wallet());
        when(walletRepository.findAll()).thenReturn(allWallets);


        List<Wallet> result = walletService.getAllWallets();


        assertEquals(1, result.size());
        verify(walletRepository, times(1)).findAll();
        verify(walletRepository, never()).getWalletByCustomer_Id(anyInt());
    }

    @Test
    public void successfullyListCustomerWallets() {
        User customerUser = new User();
        customerUser.setId(1);
        customerUser.setRole(User.Role.ROLE_CUSTOMER);

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(auth.getPrincipal()).thenReturn(customerUser);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        List<Wallet> customerWallets = List.of(new Wallet());
        when(walletRepository.getWalletByCustomer_Id(1)).thenReturn(customerWallets);

        List<Wallet> result = walletService.getAllWallets();

        assertEquals(1, result.size());
        verify(walletRepository, times(1)).getWalletByCustomer_Id(1);
        verify(walletRepository, never()).findAll();
    }

    @Test
    public void successfullyGetWalletDetail(){
        Wallet wallet=new Wallet();
        wallet.setId(1);

        when(walletRepository.findById(1)).thenReturn(Optional.of(wallet));

        Wallet wallet1=walletService.getWalletById(1);

        assertNotNull(wallet1);
        assertEquals(wallet,wallet1);

        verify(walletRepository,times(1)).findById(1);

    }

}
