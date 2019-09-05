package io.rocketbase.commons.test;


import lombok.Getter;
import org.junit.Rule;

public abstract class BaseUserIntegrationTest extends BaseIntegrationTest {

    @Getter
    @Rule
    public SmtpServerRule smtpServerRule = new SmtpServerRule();

    @Override
    public String getBaseUrl() {
        return super.getBaseUrl() + "/test";
    }
}
