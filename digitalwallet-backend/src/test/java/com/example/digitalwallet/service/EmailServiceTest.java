package com.example.digitalwallet.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    public void successfullySendVerificationMailTest(){

        String toMail = "mail1@gmail.com";
        String token = "xyzabcdefgreter";


        emailService.sendVerificationMail(toMail, token);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

}
