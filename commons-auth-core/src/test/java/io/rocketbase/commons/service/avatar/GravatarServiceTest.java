package io.rocketbase.commons.service.avatar;

import io.rocketbase.commons.config.GravatarProperties;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

    private GravatarProperties getGravatarConfiguration() {
        GravatarProperties gravatarProperties = new GravatarProperties();
        gravatarProperties.setSize(160);
        gravatarProperties.setImage(GravatarProperties.DefaultImage.ROBOHASH);
        return gravatarProperties;
    }
}