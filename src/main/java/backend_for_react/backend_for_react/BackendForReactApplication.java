package backend_for_react.backend_for_react;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendForReactApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendForReactApplication.class, args);
	}

}
