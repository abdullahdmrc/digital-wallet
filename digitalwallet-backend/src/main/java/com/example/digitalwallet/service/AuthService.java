package com.example.digitalwallet.service;

import com.example.digitalwallet.dto.RegisterRequest;
import com.example.digitalwallet.model.Customer;
import com.example.digitalwallet.model.User;
import com.example.digitalwallet.repository.CustomerRepository;
import com.example.digitalwallet.repository.UserRepository;
import com.example.digitalwallet.security.JwtService;
import com.example.digitalwallet.security.SecurityConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest registerRequest) {
        Customer customer=new Customer();
        customer.setName(registerRequest.getName());
        customer.setSurname(registerRequest.getSurname());
        customer.setTckn(registerRequest.getTckn());
        customer.setUser(registerRequest.getUser());

        registerRequest.getUser().setPassword(passwordEncoder.encode(registerRequest.getUser().getPassword()));
        registerRequest.getUser().setRole(User.Role.ROLE_CUSTOMER);

        userRepository.save(registerRequest.getUser());
        customerRepository.save(customer);


        return jwtService.generateToken(registerRequest.getUser());
    }

    public String login(String username, String password,
                        AuthenticationManager authenticationManager) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        User user = userRepository.findByUsername(username)
                .orElseThrow();

        return jwtService.generateToken(user);
    }
}

