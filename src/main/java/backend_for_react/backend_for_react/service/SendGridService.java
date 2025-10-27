package backend_for_react.backend_for_react.service;


import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "SEND-GRID")
public class SendGridService {
    private final SendGrid sendGrid;

    @Value("${spring.sendgrid.from-email}")
    private String from;

    @Value("${spring.sendgrid.otp-valid-minutes}")
    private Integer otpValidMinutes;

    public void sendEmail(String to, String subject, String body) {
        Email fromEmail = new Email(from);
        Email toEmail = new Email(to);

        Content content = new Content("text/plain", body);
        Mail mail = new Mail();

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            if(response.getStatusCode() == 202) {
                log.info("Email sent successfully");
            }else {
                log.error("Email sent failed");
            }
        }catch (IOException e){
            log.error("Email sent failed, error: {}", e.getMessage());
        }
    }
    public void emailVerification(String to, String name) throws IOException {
        log.info("Email verification started");


        Email fromEmail = new Email(from,"EvoMart");
        Email toEmail = new Email(to);

        String subject = "Verification email";
        String verificationLink = "http://localhost:8080/email-verification";

        Map<String , String> map = new HashMap<>();
        map.put("name", name);
        map.put("subject",subject);
        map.put("verification_link", verificationLink);
        map.put("expire_minutes",String.valueOf(otpValidMinutes));

        Mail mail = new Mail();
        mail.setFrom(fromEmail);
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);

        //Add to dynamic data

        map.forEach(personalization::addDynamicTemplateData);
        mail.addPersonalization(personalization);
        mail.setTemplateId("d-5922cc58601a480db52dc873a2f12965");

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);
        if(response.getStatusCode() == 200) {
            log.info("Email sent successfully");
        }else {
            log.error("Email sent failed");
        }
    }

    public void emailWithOTP(String to, String name ,String code) throws IOException {
        log.info("Email verification started");
        log.info("to: {}", to);
        log.info("from : {}", from);

        log.info("Sendgrid {}" ,sendGrid);

        Email fromEmail = new Email(from,"EvoMart");
        Email toEmail = new Email(to);

        String subject = "Verification email";

        Map<String , String> map = new HashMap<>();
        map.put("name", name);
        map.put("subject",subject);
        map.put("verification-code", code);
        map.put("expire_minutes",String.valueOf(otpValidMinutes));

        Mail mail = new Mail();
        mail.setFrom(fromEmail);
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);

        //Add to dynamic data

        map.forEach(personalization::addDynamicTemplateData);
        mail.addPersonalization(personalization);
        mail.setTemplateId("d-1cc4e97fc6e9404eaa0d392665313df2");

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);
        log.info("response code: {}", response.getStatusCode());
        if(response.getStatusCode() == 202) {
            log.info("Email sent successfully");
        }else {
            log.error("Email sent failed");
        }
    }
}
