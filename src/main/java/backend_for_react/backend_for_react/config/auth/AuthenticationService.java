package backend_for_react.backend_for_react.config.auth;

import backend_for_react.backend_for_react.common.enums.*;
import backend_for_react.backend_for_react.controller.request.Authentication.*;
import backend_for_react.backend_for_react.controller.request.User.UserCreationRequest;
import backend_for_react.backend_for_react.controller.response.AuthenticationResponse;
import backend_for_react.backend_for_react.controller.response.IntrospectResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.ErrorResponse;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.model.UserRank;
import backend_for_react.backend_for_react.repository.RoleRepository;
import backend_for_react.backend_for_react.model.Role;
import backend_for_react.backend_for_react.common.enums.RoleType;
import backend_for_react.backend_for_react.repository.UserRankRepository;
import backend_for_react.backend_for_react.repository.UserRepository;
import backend_for_react.backend_for_react.service.OTPService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/***
 * Xu li JWT
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class AuthenticationService {
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected Long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected Long REFRESHABLE_DURATION;

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    UserRankRepository userRankRepository;

    OTPService otpService;



    RoleRepository roleRepository;

    RedisTemplate<String, Object> redisTemplate;

    public AuthenticationResponse authication (AuthenticationRequest req){
        var user = userRepository.findByUsername(req.getUsername()).orElseThrow(()-> new EntityNotFoundException("Username not found"));
        if(user.getStatus().equals(UserStatus.INACTIVE)){
            throw new BusinessException(ErrorCode.UNAUTHENTICATED , "Your account is inactive");
        }
        boolean authenticated =  passwordEncoder.matches(req.getPassword(), user.getPassword());
        if (!authenticated){
            throw new BusinessException(ErrorCode.UNAUTHENTICATED, "Invalid password");
        }
        if(user.getStatus().equals(UserStatus.NONE)){
            throw new BusinessException(ErrorCode.NOT_VERIFY, "Your account is not verify" ,
                    ErrorResponse.VerifyAccountData.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .build());
        }
        String token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .role(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .expiredAt(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .build();

    }

    /***
     * service xac nhan 1 token request co hop le hay khong
     * @param req
     * @return
     * @throws ParseException
     * @throws JOSEException
     */
    @Transactional
    public IntrospectResponse introspect(IntrospectRequest req) throws ParseException, JOSEException {
        String token = req.getToken();
        boolean isValid = true;
        User user = null;
        UserStatus status = null;
        List<String> roles = new ArrayList<>();

        try {
            SignedJWT signedJWT = verifyToken(token, false);
            String username = signedJWT.getJWTClaimsSet().getSubject();
            log.info("username {}", username);

            if(username != null){
                // Tìm user từ database
                user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST , MessageError.USER_NOT_FOUND));
            }else {
                log.info("username null");
                String email = signedJWT.getJWTClaimsSet().getStringClaim("email");
                user = userRepository.findByProviderAndEmail("google",email)
                        .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST , MessageError.USER_NOT_FOUND));
            }
            status = user.getStatus();
            log.info("user: {}",user);
            if (!CollectionUtils.isEmpty(user.getRoles())) {
                for (var role : user.getRoles()) {
                    roles.add("ROLE_" + role.getName());
                }
            }
        } catch (BusinessException ex) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .fullName(user.getFullName())
                .email(user.getEmail())
                .gender(user.getGender())
                .avatar(user.getAvatarImage())
                .status(status)
                .roles(roles)
                .build();
    }

    public void logout(LogoutRequest req) throws ParseException, JOSEException {
        var signedJWT = verifyToken(req.getToken() , true);
        String jit = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        // Tính TTL (milliseconds đến khi token hết hạn)
        long ttlMillis = expiryTime.getTime() - System.currentTimeMillis();

        // Nếu token đã hết hạn thì không cần lưu vào Redis
        if (ttlMillis <= 0) {
            return;
        }

        // Chuyển TTL sang giây (Redis TTL hoạt động tính theo giây hoặc ms)
        long ttlSeconds = ttlMillis / 1000;

        // Lưu token vào Redis với thời gian sống = thời gian còn lại của JWT
        redisTemplate.opsForValue()
                .set("invalidToken:" + jit, true, ttlSeconds, TimeUnit.SECONDS);

    }
    public AuthenticationResponse refreshToken(RefreshRequest req) throws ParseException, JOSEException {
        var signJWT = verifyToken(req.getToken() , true);

        var jit = signJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        long ttlMillis = expiryTime.getTime() - System.currentTimeMillis();
        if (ttlMillis > 0) {
            redisTemplate.opsForValue()
                    .set("invalidToken:" + jit, true, ttlMillis, TimeUnit.MILLISECONDS);
        }

        var user = userRepository.findByUsername(signJWT.getJWTClaimsSet().getSubject()).orElseThrow(
                ()-> new BusinessException(ErrorCode.NOT_EXISTED , MessageError.USER_NOT_FOUND));
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }
    private SignedJWT verifyToken(String token , boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expityTime = (isRefresh)
                        ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION,ChronoUnit.SECONDS).toEpochMilli())
                        : signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);
        if(!(verified && expityTime.after(new Date()))){
            throw new BusinessException(ErrorCode.UNAUTHENTICATED, MessageError.UNAUTHENTICATED);
        }
        String jit = signedJWT.getJWTClaimsSet().getJWTID();
        boolean isBlacklisted = redisTemplate.hasKey("invalidToken:" + jit);
        if(isBlacklisted) throw new BusinessException(ErrorCode.UNAUTHENTICATED,MessageError.TOKEN_INVALID);
        return signedJWT;
    }

    public String generateToken(User user){
//        token : header + payload + serectKey => hash => token
//        Thong tin loai token ,thong tin thuat toan , thuat toan HS512
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

//        Noi dung token
//        Data trong body goi la Claim
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername()) // noi dung dai dien cho user dang nhap he thong
                .issuer("huy.com") // thuong la domain (xac dinh issuer tu ai)
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli())) // thoi gian het hieu luc
                .jwtID(UUID.randomUUID().toString())     // token id
                .claim("scope", buildScope(user)) // thong tin them custom , vi du id , role
                .claim("email" ,user.getEmail())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject()); // tao payload object

        JWSObject jwsObject = new JWSObject(header,payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));  // ki bang thuat toan MAC
            return jwsObject.serialize(); // dua ve string
        } catch (Exception e) {
            log.error("can not create token");
            e.printStackTrace();
        }
        return null;
    }
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
            });

        return stringJoiner.toString();
    }

    @Transactional
    public Long register(RegisterRequest req) throws IOException {
        log.info("Service create user");
        if(userRepository.existsByUsername(req.getUsername()))
            throw new BusinessException(ErrorCode.EXISTED,MessageError.USERNAME_EXISTED);
        User user = new User();
        user.setFullName(req.getFullName());
        user.setGender(req.getGender());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUsername(req.getUsername());
        user.setStatus(UserStatus.NONE);
        user.setTotalSpent(BigDecimal.ZERO);
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        UserRank userRank = userRankRepository.findByName(Rank.BRONZE.name())
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"User rank not found"));
        user.setUserRank(userRank);

        Role role = roleRepository.findByName(RoleType.USER.name())
                .orElseThrow(()-> new BusinessException(ErrorCode.NOT_EXISTED , MessageError.ROLE_NOT_FOUND));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);

        otpService.sendOTP(user, OTPType.VERIFICATION);
        return user.getId();
    }
    public Long save(UserCreationRequest req) throws IOException {
        log.info("Service create user");
        if(userRepository.existsByUsername(req.getUsername()))
            throw new BusinessException(ErrorCode.EXISTED,MessageError.USERNAME_EXISTED);
        User user = new User();
        user.setFullName(req.getFullName());
        user.setGender(req.getGender());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUsername(req.getUsername());
        user.setStatus(UserStatus.NONE);
        user.setTotalSpent(BigDecimal.ZERO);
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        UserRank userRank = userRankRepository.findByName(Rank.BRONZE.name())
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"User rank not found"));
        user.setUserRank(userRank);

        Set<Role> roles = new HashSet<>();
        for(Long roleId : req.getRoleId()){
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(()-> new BusinessException(ErrorCode.NOT_EXISTED , MessageError.ROLE_NOT_FOUND));
            roles.add(role);
        }
        user.setRoles(roles);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        return user.getId();
    }
}
