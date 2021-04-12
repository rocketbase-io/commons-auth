package io.rocketbase.commons.vaadin.renderer;

import com.vaadin.flow.data.renderer.BasicRenderer;
import com.vaadin.flow.function.ValueProvider;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class InstantRenderer<SOURCE> extends BasicRenderer<SOURCE, Instant> {

    private final DateTimeFormatter formatter;
    private final String nullRepresentation;

    public InstantRenderer(ValueProvider<SOURCE, Instant> valueProvider) {
        this(valueProvider, DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT), "");
    }

    public InstantRenderer(
            ValueProvider<SOURCE, Instant> valueProvider,
            DateTimeFormatter formatter) {
        this(valueProvider, formatter, "");
    }

    public InstantRenderer(
            ValueProvider<SOURCE, Instant> valueProvider,
            DateTimeFormatter formatter, String nullRepresentation) {
        super(valueProvider);

        if (formatter == null) {
            throw new IllegalArgumentException("formatter may not be null");
        }
        if (formatter.getZone() == null) {
            this.formatter = formatter.withZone(ZoneOffset.UTC);
        } else {
            this.formatter = formatter;
        }
        this.nullRepresentation = nullRepresentation;
    }

    public InstantRenderer(
            ValueProvider<SOURCE, Instant> valueProvider,
            String formatPattern) {
        this(valueProvider, formatPattern, Locale.getDefault());
    }


    public InstantRenderer(
            ValueProvider<SOURCE, Instant> valueProvider,
            String formatPattern, Locale locale) {
        this(valueProvider, formatPattern, locale, "");
    }


    public InstantRenderer(
            ValueProvider<SOURCE, Instant> valueProvider,
            String formatPattern, Locale locale, String nullRepresentation) {
        super(valueProvider);

        if (formatPattern == null) {
            throw new IllegalArgumentException(
                    "format pattern may not be null");
        }

        if (locale == null) {
            throw new IllegalArgumentException("locale may not be null");
        }

        formatter = DateTimeFormatter.ofPattern(formatPattern, locale)
                .withZone(ZoneOffset.UTC);
        this.nullRepresentation = nullRepresentation;
    }

    @Override
    protected String getFormattedValue(Instant instant) {
        return instant == null ? nullRepresentation
                : formatter.format(instant);
    }
}
