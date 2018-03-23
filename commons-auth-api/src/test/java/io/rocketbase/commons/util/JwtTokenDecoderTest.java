package io.rocketbase.commons.util;

import io.rocketbase.commons.util.JwtTokenDecoder.JwtTokenBody;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JwtTokenDecoderTest {

    @Test
    public void decodeTokenBody() {
        // given
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE1MjE4MTc1MzUsImV4cCI6MTUyMTgyMTEzNSwic3ViIjoidXNlciIsInNjb3BlcyI6WyJVU0VSIl19.0mFAzASrh2RTPX-uCmiK4gHsLwypfcGOY6iEvJTIZtAWcCv0MypkHz63LzcbMWgJJTgVdv8GREghe7tdnlTxmA";

        // when
        JwtTokenBody tokenBody = JwtTokenDecoder.decodeTokenBody(token);

        // then
        assertThat(tokenBody, notNullValue());
        assertThat(tokenBody.getExpiration(), equalTo(LocalDateTime.of(2018, 3, 23, 17, 5, 35, 0)));
        assertThat(tokenBody.getIssuedAt(), equalTo(LocalDateTime.of(2018, 3, 23, 16, 5, 35, 0)));
        assertThat(tokenBody.hasRole("USER"), equalTo(true));
        assertThat(tokenBody.hasRole("--"), equalTo(false));
        assertThat(tokenBody.getUsername(), equalTo("user"));
    }

    @Test
    public void decodeInvalidTokenBody() {
        // given
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE1MjE4MddddTc1MzUsImV4cCI6MTUyMTgyMTEzNSwic3ViIjoidXNlciIsInNjb3BlcyI6WyJVU0VSIl19.0mFAzASrh2RTPX-uCmiK4gHsLwypfcGOY6iEvJTIZtAWcCv0MypkHz63LzcbMWgJJTgVdv8GREghe7tdnlTxmA";

        // when
        JwtTokenBody tokenBody = JwtTokenDecoder.decodeTokenBody(token);

        // then
        assertThat(tokenBody, nullValue());

    }
}