package com.javadoh.toptestquiz.utils;

/**
 * Created by lliberal on 15-12-2016.
 */

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

public class EmailSender {

    final String emailPort = "465";// gmail's smtp port 587
    final String smtpAuth = "true";
    final String starttls = "true";
    final String emailHost = "smtp.gmail.com";
    final String transportProtocol = "smtp";
    final String socketFactory = "javax.net.ssl.SSLSocketFactory";
    String fromEmail;
    String fromPassword;
    String toEmail;
    String emailSubject;
    String emailBody;
    String pathFile;

    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;

    public EmailSender() {

    }

    public EmailSender(String fromEmail, String fromPassword,
                 String toEmail, String emailSubject, String emailBody, String pathFile) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmail = toEmail;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        this.pathFile = pathFile;

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.host", emailHost);
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.socketFactory.class", socketFactory);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        emailProperties.put("mail.transport.protocol", transportProtocol);
        //PARAMETRIZACIÓN DE TIME OUT
        emailProperties.put("mail.smtp.connectiontimeout", "7000");
        emailProperties.put("mail.smtp.timeout", "7000");

        Log.i("GMail", "Mail server properties set.");
    }

    public MimeMessage createEmailMessage() throws AddressException,
            MessagingException, UnsupportedEncodingException {

        mailSession = Session.getDefaultInstance(emailProperties,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("javadoh.servicios@gmail.com","19102183");
                    }
                });

            emailMessage = new MimeMessage(mailSession);
            emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
            emailMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toEmail));

        if(pathFile != null || "".equalsIgnoreCase(pathFile)){
            //CREAMOS EL ARCHIVO ADJUNTO PARA EL EMAIL
            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(pathFile);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(pathFile);
            multipart.addBodyPart(messageBodyPart);

            BodyPart messageBodyPart2 = new MimeBodyPart();
            messageBodyPart2.setText("REPORTE DE PRUEBA");
            multipart.addBodyPart(messageBodyPart2);
            //AGREGAMOS LOS DATOS DEL ARCHIVO ADJUNTO
            emailMessage.setContent(multipart);
        }
            emailMessage.setSubject(emailSubject);
            emailMessage.setContent(emailBody, "text/html");
            Log.i("GMail", "Email Message created.");

        return emailMessage;
    }

    public void sendEmail() throws AddressException, MessagingException {


        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromEmail, fromPassword);
        Log.i("GMail","allrecipients: "+emailMessage.getAllRecipients());
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        Log.i("GMail", "Email sent successfully.");
    }

}
