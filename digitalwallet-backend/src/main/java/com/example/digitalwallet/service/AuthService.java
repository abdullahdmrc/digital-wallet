package com.example.digitalwallet.service;

import com.example.digitalwallet.dto.RegisterRequest;
import com.example.digitalwallet.model.Customer;
import com.example.digitalwallet.model.User;
import com.example.digitalwallet.repository.CustomerRepository;
import com.example.digitalwallet.repository.UserRepository;
import com.example.digitalwallet.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public String register(RegisterRequest registerRequest) {
        User user = registerRequest.getUser();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.ROLE_CUSTOMER);
        user.setVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString());
        userRepository.save(user);

        Customer customer = new Customer();
        customer.setName(registerRequest.getName());
        customer.setSurname(registerRequest.getSurname());
        customer.setTckn(registerRequest.getTckn());
        customer.setUser(user);
        customerRepository.save(customer);

        emailService.sendVerificationMail(user.getUsername(), user.getVerificationToken());

        return "Kayıt başarılı! Lütfen e-postanızı kontrol ederek hesabınızı doğrulayın.";
    }

    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Geçersiz doğrulama tokenı"));

        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return "Hesabınız başarıyla doğrulandı! Artık giriş yapabilirsiniz.";
    }


    public String login(String username, String password,
                        AuthenticationManager authenticationManager) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        User user = userRepository.findByUsername(username)
                .orElseThrow();

        if (!user.isVerified()) {
            throw new RuntimeException("Lütfen önce e-posta adresinizi doğrulayın.");
        }


        return jwtService.generateToken(user);
    }
}

