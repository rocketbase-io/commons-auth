package io.rocketbase.commons.service;

import io.rocketbase.commons.service.EmailTemplateService.TemplateConfigBuilder;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class EmailTemplateServiceTest {

    @Test
    public void buildHtmlTextTemplate() {
        // given
        String title = "Please Verify Your Account";
        String header = "Verify Your Account";
        String firstLine = "first line";
        String second = "second";
        String url = "http://localhost/?verification=test123";
        String clickHere = "click here";
        String greeting = "- cheers";
        TemplateConfigBuilder templateConfigBuilder = TemplateConfigBuilder.build()
                .title(title)
                .header(header)
                .addLine(firstLine)
                .addLine(second)
                .action(url, clickHere)
                .addGreeting(greeting)
                .receiveNote("commons-auth", "support@rocketbase.io")
                .copyright("https://www.rocketbase.io", "rocketbase.io");

        EmailTemplateService emailTemplateService = new EmailTemplateService();
        // when

        EmailTemplateService.HtmlTextEmail htmlTextEmail = emailTemplateService.buildHtmlTextTemplate(templateConfigBuilder);

        // then

        assertThat(htmlTextEmail, notNullValue());
        assertThat(htmlTextEmail.getHtml(), notNullValue());
        assertThat(htmlTextEmail.getHtml(), containsString(title));
        assertThat(htmlTextEmail.getHtml(), containsString(header));
        assertThat(htmlTextEmail.getHtml(), containsString(firstLine));
        assertThat(htmlTextEmail.getHtml(), containsString(second));
        assertThat(htmlTextEmail.getHtml(), containsString(url));
        assertThat(htmlTextEmail.getHtml(), containsString(clickHere));
        assertThat(htmlTextEmail.getHtml(), containsString(greeting));
        assertThat(htmlTextEmail.getText(), notNullValue());
        assertThat(htmlTextEmail.getText(), startsWith("Verify Your Account\n" +
                "first linesecond\n" +
                "click here -> http://localhost/?verification=test123\n" +
                "- cheers\n"));
    }
}