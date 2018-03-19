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

    public static class TemplateConfigBuilder {

        private static ColorStyle BASE_STYLE = new ColorStyle("ffffff", "457B9D");
        @Getter
        private Map<String, Object> template = new HashMap<>();

        private TemplateConfigBuilder() {
        }

        public static TemplateConfigBuilder build() {
            return new TemplateConfigBuilder();
        }

        public TemplateConfigBuilder header(String title) {
            template.put("header", new Header(title, BASE_STYLE));
            return this;
        }

        public TemplateConfigBuilder headerWithStyling(String title, String colorText, String colorBg) {
            template.put("header", new Header(title, new ColorStyle(colorText, colorBg)));
            return this;
        }

        public TemplateConfigBuilder title(String title) {
            template.put("title", title);
            return this;
        }

        public TemplateConfigBuilder action(String url, String text) {
            template.put("action", new Action(url, text, BASE_STYLE));
            return this;
        }

        public TemplateConfigBuilder actionWithStyling(String url, String text, String colorText, String colorBg) {
            template.put("action", new Action(url, text, new ColorStyle(colorText, colorBg)));
            return this;
        }

        public TemplateConfigBuilder copyright(String url, String name) {
            template.put("copyright", new Copyright(url, name));
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

        @RequiredArgsConstructor
        @Data
        private static class ColorStyle {
            private final String text;
            private final String bg;
        }

        @RequiredArgsConstructor
        @Data
        private static class Header {
            private final String title;
            private final ColorStyle color;
        }

        @RequiredArgsConstructor
        @Data
        private static class Action {
            private final String url;
            private final String text;
            private final ColorStyle color;
        }

        @RequiredArgsConstructor
        @Data
        private static class Copyright {
            private final String url;
            private final String name;
        }

    }

}
