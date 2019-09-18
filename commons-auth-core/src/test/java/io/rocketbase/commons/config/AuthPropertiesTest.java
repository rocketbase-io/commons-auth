package io.rocketbase.commons.config;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class AuthPropertiesTest {

    @Test
    public void testTransferPrefix() {
        assertThat(AuthProperties.transferPrefix(null), equalTo(""));
        assertThat(AuthProperties.transferPrefix("/"), equalTo(""));
        assertThat(AuthProperties.transferPrefix("/ab"), equalTo("/ab"));
        assertThat(AuthProperties.transferPrefix("ab"), equalTo("/ab"));
        assertThat(AuthProperties.transferPrefix("ab/"), equalTo("/ab"));
    }
}