package backend_for_react.backend_for_react.config.Delivery;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
public class DeliveryConfig {
    @Value("${spring.ghn.baseUrl}")
    private String baseUrl;

    @Value("${spring.ghn.token}")
    private String token;

    @Value("${spring.ghn.shopId}")
    private Long shopId;
}
