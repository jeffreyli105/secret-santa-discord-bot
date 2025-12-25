package com.jeffrey.services;

import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.*;

import com.jeffrey.config.BotConfig;

public class EmailService {

	public static void sendEmail(String recipient, String subject, String body) throws MessagingException {
        String from = BotConfig.getEmailFrom();
        String password = BotConfig.getAppPassword();
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Authenticator authenticator = new Authenticator() {
        	protected PasswordAuthentication getPasswordAuthentication() {
        		return new PasswordAuthentication(from, password);
        	}
        };
        
        Session session = Session.getInstance(props, authenticator);
        try {
        	Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email message sent successfully");
        } catch (MessagingException e) {
        	throw new RuntimeException(e);
        }
        
    }
}
