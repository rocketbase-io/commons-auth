package io.rocketbase.commons.util;

import io.rocketbase.commons.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


@Slf4j
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
properties = {"auth.entity.prefix=test_"})
class CommonsAuthCollectionNameResolverIntegrationTest {

    @Resource
    private ConfigurableBeanFactory configurableBeanFactory;

    @Test
    void group() {
        // given
        CommonsAuthCollectionNameResolver collectionNameResolver = new CommonsAuthCollectionNameResolver(configurableBeanFactory);
        // when
        String collectionName = collectionNameResolver.group();
        // then
        assertThat(collectionName, notNullValue());
        assertThat(collectionName, equalTo("test_group"));
    }
}