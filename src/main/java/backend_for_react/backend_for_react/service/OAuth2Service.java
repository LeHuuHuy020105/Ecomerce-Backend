package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.Gender;
import backend_for_react.backend_for_react.common.enums.Rank;
import backend_for_react.backend_for_react.common.enums.RoleType;
import backend_for_react.backend_for_react.common.enums.UserStatus;
import backend_for_react.backend_for_react.config.auth.AuthenticationService;
import backend_for_react.backend_for_react.controller.response.AuthenticationResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.Role;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.model.UserRank;
import backend_for_react.backend_for_react.repository.RoleRepository;
import backend_for_react.backend_for_react.repository.UserRankRepository;
import backend_for_react.backend_for_react.repository.UserRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "OAUTH2-SERVICE")
public class OAuth2Service {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRankRepository userRankRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationService authenticationService; // service generate JWT của bạn

    public AuthenticationResponse loginWithGoogle(String accessToken) {
        Map<String, Object> googleUserInfo = fetchGoogleUserInfo(accessToken);

        String email = extractEmail(googleUserInfo);
        String fullName = extractFullName(googleUserInfo);
        String picture = extractPicture(googleUserInfo);
        LocalDate birthday = extractBirthday(googleUserInfo);
        log.info("Gender : {}", extractGender(googleUserInfo));
        Gender gender = Gender.valueOf(extractGender(googleUserInfo));

        // Lấy Google ID duy nhất (providerId)
        // resourceName = "people/1234567890"
        String resourceName = (String) googleUserInfo.get("resourceName");
        String googleId = resourceName != null ? resourceName.replace("people/", "") : email;

        User user = findOrCreateUser(email, fullName, picture, birthday, "google", googleId , gender);

        String jwt = authenticationService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwt)
                .authenticated(true)
                .build();
    }


    // ================= Helper Methods =================

    private Map<String, Object> fetchGoogleUserInfo(String accessToken) {
        String url = "https://people.googleapis.com/v1/people/me"
                + "?personFields=birthdays,genders,names,photos,emailAddresses";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = null;
        try {
            response = restTemplate.exchange(
                    url, HttpMethod.GET, request, Map.class);
        }catch (HttpClientErrorException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Google access token is expired or invalid");
        }
        return response.getBody();
    }

    private String extractEmail(Map<String, Object> body) {
        return ((Map)((List<?>) body.get("emailAddresses")).get(0))
                .get("value").toString();
    }

    private String extractFullName(Map<String, Object> body) {
        return ((Map)((List<?>) body.get("names")).get(0))
                .get("displayName").toString();
    }

    private String extractPicture(Map<String, Object> body) {
        return ((Map)((List<?>) body.get("photos")).get(0))
                .get("url").toString();
    }

    private LocalDate extractBirthday(Map<String, Object> body) {
        if (!body.containsKey("birthdays")) {
            return null;
        }
        Map<String, Object> b = (Map<String, Object>) ((List<?>) body.get("birthdays")).get(0);
        Map<String, Object> date = (Map<String, Object>) b.get("date");

        Integer year = (Integer) date.get("year");
        Integer month = (Integer) date.get("month");
        Integer day = (Integer) date.get("day");

        if (year != null && month != null && day != null) {
            return LocalDate.of(year, month, day);
        }
        return null;
    }
    private String extractGender(Map<String, Object> body) {
        if(!body.containsKey("genders")) {
            return null;
        }
        Map<String, Object> g = (Map<String, Object>) ((List<?>) body.get("genders")).get(0);
        return ((String) g.get("value")).toUpperCase();
    }
    private User findOrCreateUser(String email, String fullName, String picture, LocalDate birthday, String provider, String providerId , Gender gender) {
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    Role role = roleRepository.findByName(RoleType.USER.name())
                            .orElseThrow(() -> new BusinessException(
                                    ErrorCode.BAD_REQUEST,
                                    MessageError.ROLE_NOT_FOUND));

                    UserRank userRank = userRankRepository.findByName(Rank.BRONZE.name())
                            .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "User rank not found"));

                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFullName(fullName);
                    newUser.setAvatarImage(picture);
                    newUser.setDateOfBirth(birthday);
                    newUser.setStatus(UserStatus.ACTIVE);
                    newUser.setProvider(provider);       // <--- lưu provider
                    newUser.setProviderId(providerId);   // <--- lưu providerId
                    newUser.setUserRank(userRank);
                    newUser.setRoles(Set.of(role));
                    newUser.setGender(gender);
                    newUser.setTotalSpent(BigDecimal.ZERO);
                    newUser.setEmailVerified(true);
                    newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    return userRepository.save(newUser);
                });
    }
}
