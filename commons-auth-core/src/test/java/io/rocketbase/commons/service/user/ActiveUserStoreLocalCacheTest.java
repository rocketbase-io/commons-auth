package io.rocketbase.commons.service.user;

import io.rocketbase.commons.model.SimpleAppUserToken;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ActiveUserStoreLocalCacheTest {

    @Test
    public void shouldRemoveInvalided() throws Exception {
        // given
        ActiveUserStoreLocalCache activeUserStore = new ActiveUserStoreLocalCache(150);
        activeUserStore.applicationEventPublisher = new ApplicationEventPublisher() {
            @Override
            public void publishEvent(Object event) {

            }
        };
        // when
        activeUserStore.addUser(new SimpleAppUserToken("1", "user-1", Arrays.asList("TEST")));
        activeUserStore.addUser(new SimpleAppUserToken("2", "user-2", Arrays.asList("TEST")));

        // then
        assertThat(activeUserStore.getUserCount(), equalTo(2L));

        // when
        Thread.sleep(200);
        activeUserStore.addUser(new SimpleAppUserToken("1", "user-1", Arrays.asList("TEST")));
        assertThat(activeUserStore.getUserCount(), equalTo(1L));

        // when
        Thread.sleep(200);
        assertThat(activeUserStore.getUserCount(), equalTo(0L));
    }
}