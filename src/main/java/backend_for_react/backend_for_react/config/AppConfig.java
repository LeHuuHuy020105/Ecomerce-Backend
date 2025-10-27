package backend_for_react.backend_for_react.config;

import backend_for_react.backend_for_react.common.enums.*;
import backend_for_react.backend_for_react.model.Permission;
import backend_for_react.backend_for_react.model.Role;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.model.UserRank;
import backend_for_react.backend_for_react.repository.PermissionRepository;
import backend_for_react.backend_for_react.repository.RoleRepository;
import backend_for_react.backend_for_react.repository.UserRankRepository;
import backend_for_react.backend_for_react.repository.UserRepository;
import com.sendgrid.SendGrid;
import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Configuration
@Slf4j
public class AppConfig {

    @Value("${spring.sendgrid.api-key}")
    private String apiKey;

    @Value("${spring.twilio.account-sid}")
    private String ACCOUNT_SID;
    @Value("${spring.twilio.auth-token}")
    private String AUTH_TOKEN;


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //    Se duoc chay khi run -> muc dich muon tao user admin
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository,
                                        RoleRepository roleRepository,
                                        PermissionRepository permissionRepository,
                                        UserRankRepository userRankRepository,
                                        PasswordEncoder passwordEncoder) {
        return args -> {

            // Step 1: Tạo tất cả Permission từ enum nếu chưa có
            for (PermissionType p : PermissionType.values()) {
                if (!permissionRepository.existsByName(p.name())) {
                    permissionRepository.save(Permission.builder()
                            .name(p.name())
                            .description("Permission: " + p.name())
                            .status(Status.ACTIVE)
                            .build());
                }
            }

            // Step 2: Tạo tất cả User Rank từ enum nếu chưa có
            for (Rank p : Rank.values()) {
                if (!userRankRepository.existsByName(p.name())) {
                    userRankRepository.save(UserRank.builder()
                            .name(p.name())
                            .level(p.getLevel())
                            .minSpent(p.getMinSpent())
                            .status(Status.ACTIVE)
                            .build());
                }
            }


            // Step 3: Tạo role ADMIN nếu chưa có
            Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
                Set<Permission> allPermissions = new HashSet<>(permissionRepository.findAll());
                Role role = Role.builder()
                        .name("ADMIN")
                        .description("Administrator")
                        .permissions(allPermissions)
                        .status(Status.ACTIVE)
                        .build();
                return roleRepository.save(role);
            });

            for (RoleType r : RoleType.values()) {
                if (!roleRepository.existsByName(r.name())) {
                    roleRepository.save(Role.builder()
                            .name(r.name())
                            .description(r.getDescription())
                            .status(Status.ACTIVE)
                            .build());
                }
            }

            // Step 4: Tạo user admin nếu chưa có
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .fullName("Admin")
                        .email("lhhuy2005@gmail.com")
                        .gender(Gender.MALE)
                        .totalSpent(BigDecimal.ZERO)
                        .emailVerified(true)
                        .password(passwordEncoder.encode("admin"))
                        .status(UserStatus.ACTIVE)
                        .roles(Set.of(adminRole))
                        .build();
                userRepository.save(admin);
                log.warn("Admin user created with default password: admin");
            }
        };
    }

    @Bean
    public SendGrid sendGrid(){
        log.info("Send grid api key :{} ", apiKey);
        return new SendGrid(apiKey);
    }

    @PostConstruct
    public void initTwilio(){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

}
