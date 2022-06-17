package io.github.aliothliu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author liubin
 **/
@SpringBootApplication
@EnableJpaRepositories
@ActiveProfiles("h2")
public class LocalRbacApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocalRbacApplication.class, args);
    }
}
