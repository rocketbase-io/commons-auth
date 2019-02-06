package io.rocketbase.commons.converter;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.test.model.AppUserTestEntity;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AppUserConverterTest {

    @Test
    public void fromEntity() {
        // given
        AppUser entity = AppUserTestEntity.builder()
                .id("id")
                .email("email@test.io")
                .keyValueMap(ImmutableMap.<String, String>builder()
                        .put("test", "value")
                        .put("_secret", "1234")
                        .build())
                .build();

        // when
        AppUserRead appUserRead = new AppUserConverter().fromEntity(entity);

        // then
        assertThat(appUserRead, notNullValue());
        assertThat(appUserRead.getId(), equalTo("id"));
        assertThat(appUserRead.getEmail(), equalTo("email@test.io"));
        assertThat(appUserRead.getKeyValues().keySet().contains("test"), equalTo(true));
        assertThat(appUserRead.getKeyValues().keySet().contains("_secret"), equalTo(false));
    }
}