package backend_for_react.backend_for_react.config.Delivery;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
public class DeliveryConfig {
    @Value("${spring.ghn.baseUrlDev}")
    private String baseUrl;

    @Value("${spring.ghn.token}")
    private String token;

    @Value("${spring.ghn.shopId}")
    private Long shopId;

    @Value("${spring.ghn.token-dev}")
    private String tokenDev;

    @Value("${spring.ghn.shopId}")
    private Long shopIdDev;

    @Value("${spring.ghn.baseUrlProd}")
    private String baseUrlProd;

}
