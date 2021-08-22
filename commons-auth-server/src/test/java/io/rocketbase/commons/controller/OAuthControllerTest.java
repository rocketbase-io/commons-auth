package io.rocketbase.commons.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.handler.LoginSuccessCookieHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Slf4j
public class OAuthControllerTest extends BaseIntegrationTest {

    @Test
    void authWithKnownToken() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        String redirectUri = "http://example.com/product/123?test=1234";
        String state = "abc";
        ExtractableResponse<Response> extract = RestAssured.given()
                .queryParam("client_id", "123")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", state)
                .cookie(LoginSuccessCookieHandler.AUTH_REMEMBER, getTokenProvider("marten").getRefreshToken())
                .when()
                .post(getBaseUrl() + "/oauth/auth")
                .then()
                .statusCode(302)
                .extract();


        String redirectResponse = extract.header("Location");
        assertThat(redirectResponse, notNullValue());
        assertThat(redirectResponse, startsWith(redirectUri));
        assertThat(redirectResponse, containsString("code="));
        assertThat(redirectResponse, containsString("state=" + state));
    }

    @Test
    void requestToken() {
    }

}