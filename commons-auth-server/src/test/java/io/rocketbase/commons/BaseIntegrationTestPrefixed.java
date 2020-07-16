package io.rocketbase.commons;

import io.rocketbase.commons.test.BaseIntegrationTest;

public class BaseIntegrationTestPrefixed extends BaseIntegrationTest {

    @Override
    public String getBaseUrl() {
        // added prefix for testing... see: application-test.yml -> auth.prefix
        return super.getBaseUrl() + "/test";
    }

}
