package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.model.User;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.api.UserRequest;
import io.fusionauth.domain.api.email.SendRequest;
import io.fusionauth.domain.email.EmailTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import io.fusionauth.domain.api.EmailTemplateRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "FUSHION-AUTH")
public class FusionAuthService {
    private final FusionAuthClient client;

    public boolean sendOTP(String otp, User user) {


        UUID emailTemplateId = UUID.fromString("e1adeb13-b3df-4543-8d12-4caee5655432");


        // Create the SendEmailRequest
        SendRequest request = new SendRequest();

        io.fusionauth.domain.User createUser = new io.fusionauth.domain.User();
        createUser.email= user.getEmail();
        createUser.fullName = user.getFullName();

        UserRequest userRequest = new UserRequest();
        userRequest.skipVerification=false;
        userRequest.user=createUser;

        client.createUser(UUID.randomUUID(),userRequest);
        var userTo = client.retrieveUserByEmail(user.getEmail());
        request.userIds = List.of(userTo.successResponse.user.id);
        var mapData = new HashMap<String, Object>();
        mapData.put("code", otp);
        request.requestData = mapData;

        try {
            // Send the email
            var response = client.sendEmail(emailTemplateId, request);
            log.info("Sent email response: {}", response.status);

            if (response.wasSuccessful()) {
                System.out.println("Email sent successfully!");
                return true;
            } else {
                System.err.println("Failed to send email: " + response.exception.getMessage());
                // Handle errors, e.g., print response.errorResponse
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
        return false;
    }
}
