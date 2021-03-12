package io.rocketbase.commons.test.data;

import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.model.AppCapabilityEntity;
import io.rocketbase.commons.test.model.SimpleAppCapabilityEntity;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;


public abstract class CapabilityData {

    public static SimpleAppCapabilityEntity ROOT = SimpleAppCapabilityEntity.builder()
            .id(AppCapabilityRead.ROOT.getId())
            .key(AppCapabilityRead.ROOT.getKey())
            .description(AppCapabilityRead.ROOT.getDescription())
            .parentId(AppCapabilityRead.ROOT.getParentId())
            .keyPath(AppCapabilityRead.ROOT.getKeyPath())
            .withChildren(AppCapabilityRead.ROOT.isWithChildren())
            .created(AppCapabilityRead.ROOT.getCreated())
            .build();

    public static SimpleAppCapabilityEntity USER_OBJECT = SimpleAppCapabilityEntity.builder()
            .id(38005747978840060L)
            .key("user")
            .description("user objects root")
            .parentId(AppCapabilityRead.ROOT.getId())
            .keyPath("user")
            .withChildren(true)
            .created(Instant.ofEpochSecond(1609459200)) // 2021-01-01
            .build();

    public static SimpleAppCapabilityEntity USER_READ = SimpleAppCapabilityEntity.builder()
            .id(38005747978840061L)
            .key("read")
            .description("allow to read user")
            .parentId(USER_OBJECT.getId())
            .keyPath("user.read")
            .withChildren(false)
            .created(Instant.ofEpochSecond(1612137600)) // 2021-02-01
            .build();

    public static SimpleAppCapabilityEntity USER_WRITE = SimpleAppCapabilityEntity.builder()
            .id(38005747978840062L)
            .key("write")
            .description("allow to write user")
            .parentId(USER_OBJECT.getId())
            .keyPath("user.write")
            .withChildren(false)
            .created(Instant.ofEpochSecond(1612137600)) // 2021-02-01
            .build();

    public static SimpleAppCapabilityEntity API_ROOT = SimpleAppCapabilityEntity.builder()
            .id(38005747978840070L)
            .key("api")
            .description("api root")
            .parentId(AppCapabilityRead.ROOT.getId())
            .keyPath("api")
            .withChildren(true)
            .created(Instant.ofEpochSecond(1612137599)) // 2021-01-31
            .build();

    public static SimpleAppCapabilityEntity API_BLOG = SimpleAppCapabilityEntity.builder()
            .id(38005747978840071L)
            .key("blog")
            .description("blog objects root")
            .parentId(API_ROOT.getId())
            .keyPath("api.blog")
            .withChildren(true)
            .created(Instant.ofEpochSecond(1612137599)) // 2021-01-31
            .build();

    public static SimpleAppCapabilityEntity API_BLOG_CRUD = SimpleAppCapabilityEntity.builder()
            .id(38005747978840072L)
            .key("crud")
            .description("allow to delete blog entries")
            .parentId(API_BLOG.getId())
            .keyPath("api.blog.crud")
            .withChildren(false)
            .created(Instant.ofEpochSecond(1612137599)) // 2021-01-31
            .build();

    public static SimpleAppCapabilityEntity API_BLOG_PUBLISH = SimpleAppCapabilityEntity.builder()
            .id(38005747978840073L)
            .key("publish")
            .description("allow to publish blog ")
            .parentId(API_BLOG.getId())
            .keyPath("api.blog.publish")
            .withChildren(false)
            .created(Instant.ofEpochSecond(1612137599)) // 2021-01-31
            .build();

    public static SimpleAppCapabilityEntity API_ASSET = SimpleAppCapabilityEntity.builder()
            .id(38005747978840074L)
            .key("asset")
            .description("asset objects root")
            .parentId(API_ROOT.getId())
            .keyPath("api.asset")
            .withChildren(false)
            .created(Instant.ofEpochSecond(1614556799)) // 2021-02-28
            .build();

    public static List<AppCapabilityEntity> getEntities() {
        return Arrays.asList(ROOT, USER_OBJECT, USER_READ, USER_WRITE, API_ROOT, API_BLOG, API_BLOG_CRUD, API_BLOG_PUBLISH, API_ASSET);
    }

}
