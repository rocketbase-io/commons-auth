package io.rocketbase.commons.service;

import io.rocketbase.commons.config.GravatarConfiguration;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class GravatarServiceTest {

    @Test
    public void getAvatar() {
        // given
        String email = "marten@rocketbase.io";

        // when
        String imageUrl = new GravatarService(getGravatarConfiguration()).getAvatar(email);

        // then
        assertThat(imageUrl, notNullValue());
        assertThat(imageUrl, equalTo("https://www.gravatar.com/avatar/fc40e22b7bcd7230b49c34eb113d5dbc.jpg?s=160&d=robohash"));
    }

    @Test
    public void nullAvatar() {
        // given
        String email = null;

        // when
        String imageUrl = new GravatarService(getGravatarConfiguration()).getAvatar(email);

        // then
        assertThat(imageUrl, nullValue());
    }

    private GravatarConfiguration getGravatarConfiguration() {
        return new GravatarConfiguration(true, 160, GravatarConfiguration.DefaultImage.ROBOHASH, null);
    }
}