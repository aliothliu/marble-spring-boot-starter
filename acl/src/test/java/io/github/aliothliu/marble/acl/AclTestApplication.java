package io.github.aliothliu.marble.acl;

import io.github.aliothliu.marble.acl.jpa.AclJpaRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "io.github.aliothliu", repositoryFactoryBeanClass = AclJpaRepositoryFactoryBean.class)
public class AclTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(AclTestApplication.class, args);
    }
}
