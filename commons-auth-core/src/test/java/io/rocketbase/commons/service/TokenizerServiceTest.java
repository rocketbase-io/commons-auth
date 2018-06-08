package io.rocketbase.commons.service;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TokenizerServiceTest {

    @Test
    public void testSimpleToken() {
        // given
        TokenizerService service = new TokenizerService();

        // when
        String key = service.generateToken("user", null, 30);
        TokenizerService.Token token = service.parseToken(key);

        // then
        assertThat(key, notNullValue());
        assertThat(token.isValid(), equalTo(true));
        assertThat(token.getUsername(), equalTo("user"));
        assertThat(token.getExp(), greaterThanOrEqualTo(LocalDateTime.now().plusMinutes(29)));
        assertThat(token.getExp(), lessThanOrEqualTo(LocalDateTime.now().plusMinutes(31)));
        assertThat(token.getKeyValues(), nullValue());
    }

}