package io.rocketbase.commons.service;

import lombok.SneakyThrows;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class SimpleTokenServiceTest {

    @Test
    public void testValidToken() {
        // given
        // when
        String token = SimpleTokenService.generateToken("heino", 10);
        SimpleTokenService.Token parsed = SimpleTokenService.parseToken(token);
        // then
        assertThat(parsed.isValid(), equalTo(true));
        assertThat(parsed.getUsername(), equalTo("heino"));
    }

    @SneakyThrows
    @Test
    public void testInvalidToken() {
        // given
        // when
        String token = SimpleTokenService.generateToken("heino", 0);
        SimpleTokenService.Token parsed = SimpleTokenService.parseToken(token);

        Thread.sleep(1001);
        // then
        assertThat(parsed.isValid(), equalTo(false));
    }

    @Test
    public void testCurruptedToken() {
        // given
        // when
        // then
        assertThat(SimpleTokenService.parseToken("adasdsdasddd").isValid(), equalTo(false));
    }

}