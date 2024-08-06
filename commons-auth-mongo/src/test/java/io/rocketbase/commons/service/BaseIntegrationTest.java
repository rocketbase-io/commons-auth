package io.rocketbase.commons.service;

import io.rocketbase.commons.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
public abstract class BaseIntegrationTest {
        @Container
        protected static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4"));

        @DynamicPropertySource
        static void initTestContainerProperties(DynamicPropertyRegistry registry) {
            mongoDBContainer.start();
            log.info("Setting spring.data.mongodb.uri = {}", mongoDBContainer.getReplicaSetUrl());
            registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        }
}
