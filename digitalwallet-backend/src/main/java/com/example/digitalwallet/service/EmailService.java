package com.example.digitalwallet.service;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationMail(String toMail,String token){
        String verificationLink = "http://localhost:8081/api/auth/verify?token=" + token;
        SimpleMailMessage mailMessage=new SimpleMailMessage();
        mailMessage.setFrom("projectdigitalwallet@gmail.com");
        mailMessage.setTo(toMail);
        mailMessage.setSubject("Hesabınızı doğrulayın");
        mailMessage.setText("Linke tıklayarak hesabınızı aktif edebilirsiniz = " + verificationLink);

        mailSender.send(mailMessage);
    }

}
