package io.rocketbase.commons.test.data;

import com.google.common.collect.ImmutableSet;
import io.rocketbase.commons.model.AppClientEntity;
import io.rocketbase.commons.test.model.SimpleAppClientEntity;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public abstract class ClientData {

    public static SimpleAppClientEntity LOCALHOST = SimpleAppClientEntity.builder()
            .id(38005919875504130L)
            .name("localhost")
            .description("local development")
            .capabilityIds(ImmutableSet.of(CapabilityData.ROOT.getId()))
            .redirectUrls(ImmutableSet.of("http://localhost:8080", "http://localhost:3000"))
            .created(Instant.ofEpochSecond(1609459200)) // 2021-01-01
            .modified(Instant.ofEpochSecond(1609459200))
            .modifiedBy("test")
            .build();

    public static SimpleAppClientEntity EXAMPLE_BLOG = SimpleAppClientEntity.builder()
            .id(38005919875504131L)
            .name("example")
            .description("website example blog")
            .capabilityIds(ImmutableSet.of(CapabilityData.API_BLOG.getId()))
            .redirectUrls(ImmutableSet.of("https://example.com"))
            .created(Instant.ofEpochSecond(1614556799)) // 2021-02-28
            .modified(Instant.ofEpochSecond(1614556799))
            .modifiedBy("test")
            .build();

    public static List<AppClientEntity> getEntities() {
        return Arrays.asList(LOCALHOST, EXAMPLE_BLOG);
    }
}
