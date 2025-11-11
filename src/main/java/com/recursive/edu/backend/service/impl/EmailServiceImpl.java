/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.service.impl;

import com.recursive.edu.backend.service.EmailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author PrantikGuha
 * CreatedAt: {06-11-2025}
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${sendgrid.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Value("${sendgrid.otp-template-id}")
    private String otpTemplateId;

    @Override
    public void sendOtpEmail(String name, String to, String otp, String expiryTime) {
        try {
            Personalization personalization = new Personalization();
            personalization.addDynamicTemplateData("user_name", name);
            personalization.addDynamicTemplateData("otp_code", otp);
            personalization.addDynamicTemplateData("expiry_minutes", expiryTime);
            sendEmail(to, otpTemplateId, personalization, "One time password from Recursive Education");
        } catch (Exception exception) {
            log.error("Unable to send email, ", exception);
        }
    }

    private void sendEmail(String toEmail, String templateId, Personalization personalization, String subject) throws IOException {
        try {
            Email from = new Email(fromEmail, "Recursive Education");
            Email to = new Email(toEmail);

            Mail mail = new Mail();
            mail.setFrom(from);
            mail.setTemplateId(templateId);
            mail.setSubject(subject);

            personalization.addTo(to);

            mail.addPersonalization(personalization);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException ex) {
            throw ex;
        }
    }
}
