package io.rocketbase.commons.service;

import io.rocketbase.commons.service.VerificationLinkService.ActionType;
import io.rocketbase.commons.service.VerificationLinkService.VerificationToken;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class VerificationLinkServiceTest {

    @Test
    public void generateKey() {
        // given
        VerificationLinkService service = new VerificationLinkService();
        service.initKey("HdGGvwVS8BEW81oEUzB3IisOACY1ioLV");

        // when
        String key = service.generateKey("user", ActionType.VERIFICATION, 30);

        // then
        assertThat(key, notNullValue());
        assertThat(key.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$"), equalTo(true));
    }

    @Test
    public void parseKey() {
        // given
        VerificationLinkService service = new VerificationLinkService();
        service.initKey("HdGGvwVS8BEW81oEUzB3IisOACY1ioLV");
        String key = service.generateKey("user", ActionType.VERIFICATION, 30);

        // when
        VerificationToken token = service.parseKey(key);

        // then
        assertThat(token, notNullValue());
        assertThat(token.isValid(ActionType.VERIFICATION), equalTo(true));
        assertThat(token.isValid(ActionType.PASSWORD_RESET), equalTo(false));
        assertThat(token.getUsername(), equalTo("user"));
        assertThat(token.getType(), equalTo(ActionType.VERIFICATION));
        assertThat(token.getExp(), greaterThan(LocalDateTime.now().plusMinutes(29)));
        assertThat(token.getExp(), lessThan(LocalDateTime.now().plusMinutes(31)));
    }
}