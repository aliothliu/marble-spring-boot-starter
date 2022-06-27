package io.github.aliothliu.marble.acl;

import io.github.aliothliu.marble.domain.Custom;
import io.github.aliothliu.marble.domain.CustomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AclRepositoryTest {

    @Autowired
    private CustomRepository customRepository;

    @BeforeEach
    public void setup() {
        this.customRepository.save(new Custom(UUID.randomUUID().toString(), "liubin", "IT", "admin"));
    }

    @AfterEach
    public void teardown() {
        this.customRepository.deleteAll();
    }

    @Test
    public void queryByCreator() {
        this.customRepository.findAll();
    }
}
