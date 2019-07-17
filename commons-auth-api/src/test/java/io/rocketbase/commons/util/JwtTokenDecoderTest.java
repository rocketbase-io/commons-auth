package io.rocketbase.commons.util;

import org.junit.Test;

import java.time.Instant;

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
        assertThat(tokenBody.getExpiration(), equalTo(Instant.ofEpochSecond(1521821135, 0)));
        assertThat(tokenBody.getIssuedAt(), equalTo(Instant.ofEpochSecond(1521817535, 0)));
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

    /**
     * had an issue with base64.decode and base64.urlDecode...
     * just a test to be sure this don't gone be happen again :)
     */
    @Test
    public void decodeWithSpecialUrl() {
        // given
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE1NjMzNTg0NDEsImV4cCI6MTU2MzM2MjA0MSwic3ViIjoidGVzdCIsInNjb3BlcyI6WyJST0xFX0FETUlOIl0sInVzZXJfaWQiOiJiNjRiMzFmNC0yYTJhLTQ3ZDYtYTU2Zi1jMGIyNDJjZWNiZTgiLCJlbWFpbCI6InRlc3RAcm9ja2V0YmFzZS5pbyIsInBpY3R1cmUiOiJodHRwczovL3d3dy5ncmF2YXRhci5jb20vYXZhdGFyL2M4OWU2OTI3YTQ4Yjc0NGE0OGZlMTZhMWE3M2Y3ZjFiLmpwZz9zPTE2MCZkPXJldHJvIn0.1L-pGtMyBBhAChLg4TPDTy07S9fgVEozSVnGwq_n6HlyYlXUS5sodtuwLeTKtxcP2mIo2_SnGlj6sySp90YEvQ";

        /**
         * claim looks like:
         * {"iat":1563358441,"exp":1563362041,"sub":"test","scopes":["ROLE_ADMIN"],"user_id":"b64b31f4-2a2a-47d6-a56f-c0b242cecbe8","email":"test@rocketbase.io","picture":"https://www.gravatar.com/avatar/c89e6927a48b744a48fe16a1a73f7f1b.jpg?s=160&d=retro"}
         */

        // when
        JwtTokenBody tokenBody = JwtTokenDecoder.decodeTokenBody(token);

        // then
        assertThat(tokenBody, notNullValue());
        assertThat(tokenBody.getUserId(), is("b64b31f4-2a2a-47d6-a56f-c0b242cecbe8"));
        assertThat(tokenBody.getUsername(), is("test"));
        assertThat(tokenBody.getExpiration(), is(Instant.ofEpochSecond(1563362041, 0)));
        assertThat(tokenBody.getScopes(), containsInAnyOrder("ROLE_ADMIN"));


    }
}