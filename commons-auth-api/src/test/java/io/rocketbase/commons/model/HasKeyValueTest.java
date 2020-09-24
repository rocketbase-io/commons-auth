package io.rocketbase.commons.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class HasKeyValueTest {

    protected AppUserRead buildSample(String key1, String value1, String key2, String value2) {
        return AppUserRead.builder()
                .keyValues(ImmutableMap.of(key1, value1, key2, value2))
                .build();
    }

    @Test
    public void hasKeyValue() {
        HasKeyValue value = buildSample("key1", "v1", "key2", "v2");
        assertThat(value.hasKeyValue("key1"), equalTo(true));
        assertThat(value.hasKeyValue("unknown"), equalTo(false));
        assertThat(value.hasKeyValue(null), equalTo(false));
    }

    @Test
    public void getKeyValue() {
        HasKeyValue value = buildSample("key1", "v1", "key2", "v2");
        assertThat(value.getKeyValue("key1"), equalTo("v1"));
        assertThat(value.getKeyValue("unknown"), nullValue());
        assertThat(value.hasKeyValue(null), equalTo(false));
    }

    @Test
    public void getKeyValueJsonParsed() {
        HasKeyValue value = buildSample("key1", "[23456, 5678]", "key2", "-");
        assertThat(value.getKeyValue("key1", new TypeReference<Collection<Integer>>() {
        }, null), equalTo(Arrays.asList(23456, 5678)));

        assertThat(value.getKeyValue("key2", new TypeReference<Collection<Integer>>() {
        }, null), nullValue());
        assertThat(value.getKeyValue("unknown", new TypeReference<Collection<Integer>>() {
        }, null), nullValue());
        assertThat(value.getKeyValue("unknown", new TypeReference<Collection<Integer>>() {
        }, Collections.emptyList()), equalTo(Collections.emptyList()));
    }

    @Test
    public void getKeyValueBoolean() {
        HasKeyValue value = buildSample("key1", "false", "key2", "-");

        assertThat(value.getKeyValueBoolean("key1", null), equalTo(false));
        assertThat(value.getKeyValueBoolean("key2", null), nullValue());
        assertThat(value.getKeyValueBoolean("key2", true), equalTo(true));
    }

    @Test
    public void getKeyValueLong() {
        HasKeyValue value = buildSample("key1", "12345", "key2", "-");

        assertThat(value.getKeyValueLong("key1", null), equalTo(12345L));
        assertThat(value.getKeyValueLong("key2", null), nullValue());
        assertThat(value.getKeyValueLong("key2", 1L), equalTo(1L));
    }

    @Test
    public void testGetKeyValueCollection() {
        HasKeyValue value = buildSample("key1", "[\"abc\", \"cdefg\"]", "key2", "-");

        assertThat(value.getKeyValueCollection("key1", null), equalTo(Arrays.asList("abc", "cdefg")));
        assertThat(value.getKeyValueCollection("key2", null), nullValue());
        assertThat(value.getKeyValueCollection("key2", Arrays.asList("1")), equalTo(Arrays.asList("1")));
    }
}