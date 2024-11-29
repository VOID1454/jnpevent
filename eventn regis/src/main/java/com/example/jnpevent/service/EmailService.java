package com.example.jnpevent.service;

import java.util.UUID;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

     public void sendVerificationEmail(String recipientEmail) {
        // Create a verification link containing the token
        String verificationLink = "http://localhost:8080/register/verify?token=" + UUID.randomUUID().toString();
        
        // Send the email with the verification link
        // For simplicity, just log the email contents in this example
        System.out.println("Sending verification email to: " + recipientEmail);
        System.out.println("Click on the following link to verify your registration: " + verificationLink);
    }
}
