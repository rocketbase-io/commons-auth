package io.rocketbase.commons.service.email;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import io.rocketbase.commons.service.email.TemplateConfigBuilder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EmailTemplateService {

    private PebbleEngine engine = new PebbleEngine.Builder().build();

    @SneakyThrows
    public HtmlTextEmail buildHtmlTextTemplate(TemplateConfigBuilder configBuilder) {
        try {
            PebbleTemplate htmlTemplate = engine.getTemplate("templates/email/base.html");
            PebbleTemplate textTemplate = engine.getTemplate("templates/email/base.txt");

            Writer htmlWriter = new StringWriter();
            htmlTemplate.evaluate(htmlWriter, configBuilder.getTemplate());

            Writer textWrite = new StringWriter();
            textTemplate.evaluate(textWrite, configBuilder.getTemplate());

            return new HtmlTextEmail(htmlWriter.toString(), textWrite.toString());
        } catch (PebbleException e) {
            log.error("processing template error: {}", e.getPebbleMessage());
            throw new RuntimeException("processing template error");
        }
    }

    @RequiredArgsConstructor
    @Data
    public static class HtmlTextEmail {
        private final String html;
        private final String text;
    }
}
