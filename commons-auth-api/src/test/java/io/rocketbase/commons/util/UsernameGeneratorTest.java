package io.rocketbase.commons.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UsernameGeneratorTest {

    @Test
    public void replaceUmlaute() {
        // given
        String input = "ÖlapalümäAß";
        // when
        String result = UsernameGenerator.replaceUmlaute(input);
        // then
        assertThat(result, is("OelapaluemaeAss"));
    }

    @Test
    public void normalizeString() {
        // given
        String input = "ÄöüMte@e° eé?ß# +´´";
        // when
        String result = UsernameGenerator.normalizeString(input);
        // then
        assertThat(result, is("aeoeuemteeeess"));
    }

    @Test
    public void byFirstAndLastName() {
        // given
        String firstName = "Ünal";
        String lastName = "MöllerPütt";
        // when
        String result = UsernameGenerator.byFirstAndLastName(firstName, lastName);
        // then
        assertThat(result, is("uenal.moellerpuett"));
    }

    @Test
    public void byEmail() {
        // given
        String email = "Süper-user@web.de";
        // when
        String result = UsernameGenerator.byEmail(email);
        // then
        assertThat(result, is("sueper-user"));
    }
}