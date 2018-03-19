package io.rocketbase.commons.service;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

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

    public static class TemplateConfigBuilder {

        @Getter
        private Map<String, Object> template = new HashMap<>();

        private TemplateConfigBuilder() {
        }

        public static TemplateConfigBuilder initDefault() {
            TemplateConfigBuilder self = new TemplateConfigBuilder();
            self.template.put("header.color.text", "ffffff");
            self.template.put("header.color.bg", "457B9D");
            self.template.put("action.color.text", "ffffff");
            self.template.put("action.color.bg", "457B9D");

            self.template.put("copyright.date", new Date());

            return self;
        }

        public TemplateConfigBuilder header(String title) {
            template.put("header.title", title);
            return this;
        }

        public TemplateConfigBuilder headerStyling(String colorText, String colorBg) {
            template.put("header.color.text", colorText);
            template.put("header.color.bg", colorBg);
            return this;
        }

        public TemplateConfigBuilder title(String title) {
            template.put("title", title);
            return this;
        }

        public TemplateConfigBuilder action(String url, String text) {
            template.put("action", true);
            template.put("action.url", url);
            template.put("action.text", text);
            return this;
        }

        public TemplateConfigBuilder actionStyling(String colorText, String colorBg) {
            template.put("action.color.text", colorText);
            template.put("action.color.bg", colorBg);
            return this;
        }

        public TemplateConfigBuilder copyright(String url, String name) {
            template.put("copyright", true);
            template.put("copyright.url", url);
            template.put("copyright.name", name);
            return this;
        }

        public TemplateConfigBuilder receiveNote(String serviceName, String supportEmail) {
            template.put("receiveNote", true);
            template.put("serviceName", serviceName);
            template.put("supportEmail", supportEmail);
            return this;
        }

        public TemplateConfigBuilder addLine(String line) {
            return addToList("lines", line);
        }

        public TemplateConfigBuilder addGreeting(String greeting) {
            return addToList("greetings", greeting);
        }

        private TemplateConfigBuilder addToList(String key, String text) {
            if (!template.containsKey(key)) {
                template.put(key, new ArrayList<>());
            }
            ((List) template.get(key)).add(text);
            return this;
        }

    }

}
