package com.example.digitalwallet.service;

import com.example.digitalwallet.dto.WalletRequest;
import com.example.digitalwallet.model.Customer;
import com.example.digitalwallet.model.User;
import com.example.digitalwallet.model.Wallet;
import com.example.digitalwallet.repository.CustomerRepository;
import com.example.digitalwallet.repository.UserRepository;
import com.example.digitalwallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private WalletRepository walletRepository;
    private CustomerRepository customerRepository;
    private UserRepository userRepository;

    public Wallet createWallet(WalletRequest walletRequest){
        Wallet newWallet=new Wallet();
        newWallet.setWalletName(walletRequest.getWalletName());
        newWallet.setCurrency(Wallet.Currency.valueOf(walletRequest.getCurrency()));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Customer customer = customerRepository.findByUser(user).orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
        newWallet.setCustomer(customer);
        newWallet.setActiveForShopping(true);// default
        newWallet.setActiveForWithdraw(true); // default
        newWallet.setBalance(0.0);//default
        newWallet.setUsableBalance(0.0);//default

        walletRepository.save(newWallet);
        return newWallet;

    }

}
