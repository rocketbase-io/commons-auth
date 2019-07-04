package io.rocketbase.commons.dto.authentication;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JwtTokenBundleTest {

    @Test
    public void shouldcalculateAccessTokenExpiryCorrectly() throws Exception {
        // given
        String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE1NjIyMzc3MDksImV4cCI6MTU2MjI0MTMwOSwic3ViIjoib3JiaXQtc2VydmljZSIsInNjb3BlcyI6WyJST0xFX1NFUlZJQ0UiXSwidXNlcl9pZCI6IjQ5NTc4Njc2LWFiN2EtNDAzMC1iMjUyLWI4Yzc0Zjg0NzAxYiIsImdpdmVuX25hbWUiOiJPcmJpdCIsImZhbWlseV9uYW1lIjoiU2VydmljZSIsImVtYWlsIjoidGVhbUByb2NrZXRiYXNlLmlvIiwicGljdHVyZSI6Imh0dHBzOi8vd3d3LmdyYXZhdGFyLmNvbS9hdmF0YXIvZjZmNmZhZmFlN2IxMjUzNjdmYmFhYjBhNDgyZTFjZjIuanBnP3M9MTYwJmQ9cmV0cm8ifQ.RDhDFcuZqHyQCiBmwMX7DuZOaWRsIUnah4zmD_Uu1pdhIfdnrD01YM-sotHnJ624RINT_jcz93NXUZ_NahpkPw";

        JwtTokenBundle jwtTokenBundle = JwtTokenBundle.builder()
                .token(accessToken)
                .refreshToken(null)
                .build();
        // when
        LocalDateTime result = jwtTokenBundle.getAccessTokenExpiryDate();

        // then
        LocalDateTime of = LocalDateTime.of(2019, 7, 4, 11, 55, 9);
        assertThat(result, is(of));
    }

    @Test
    public void shouldcalculateRefreshTokenExpiryCorrectly() throws Exception {
        // given
        String refreshToken = "eyJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE1NjIyMzc3MDksImV4cCI6MTU2NDgyOTcwOSwic3ViIjoib3JiaXQtc2VydmljZSIsInVzZXJfaWQiOiI0OTU3ODY3Ni1hYjdhLTQwMzAtYjI1Mi1iOGM3NGY4NDcwMWIiLCJzY29wZXMiOlsiUkVGUkVTSF9UT0tFTiJdfQ.RDD5pepq7PbICYL0XJjveoGNfpQsHmO5h8v-e5Qc2AHkKC9X7kUKbdF2E-cXQVNE9T7dWKXx-ZqsSKu6TFqe-w";

        JwtTokenBundle jwtTokenBundle = JwtTokenBundle.builder()
                .token(null)
                .refreshToken(refreshToken)
                .build();
        // when
        LocalDateTime result = jwtTokenBundle.getRefreshTokenExpiryDate();

        // then
        LocalDateTime of = LocalDateTime.of(2019, 8, 3, 10, 55, 9);
        assertThat(result, is(of));
    }

}