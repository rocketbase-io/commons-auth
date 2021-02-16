package io.rocketbase.commons.service.user;

import com.google.common.collect.Sets;
import io.rocketbase.commons.model.SimpleAppUserToken;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;

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
        activeUserStore.addUser(new SimpleAppUserToken("1", "user-1", Sets.newHashSet("TEST")));
        activeUserStore.addUser(new SimpleAppUserToken("2", "user-2", Sets.newHashSet("TEST")));

        // then
        assertThat(activeUserStore.getUserCount(), equalTo(2L));

        // when
        Thread.sleep(200);
        activeUserStore.addUser(new SimpleAppUserToken("1", "user-1", Sets.newHashSet("TEST")));
        assertThat(activeUserStore.getUserCount(), equalTo(1L));

        // when
        Thread.sleep(200);
        assertThat(activeUserStore.getUserCount(), equalTo(0L));
    }
}