package io.rocketbase.commons.vaadin.renderer;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.function.ValueProvider;

public class IconRenderer<SOURCE, I extends com.vaadin.flow.component.icon.Icon> {

    public static <SOURCE, I extends com.vaadin.flow.component.icon.Icon> TemplateRenderer<SOURCE> create(ValueProvider<SOURCE, I> valueProvider) {
        return TemplateRenderer.<SOURCE>of("<iron-icon icon$='[[item.icon]]'></iron-icon>")
                .withProperty("icon", v -> {
                    I i = valueProvider.apply(v);
                    if (i == null) {
                        return "";
                    }
                    return i.getElement().getAttribute("icon");
                });
    }

    public static <SOURCE> TemplateRenderer<SOURCE> createBoolean(ValueProvider<SOURCE, Boolean> valueProvider) {
        return create(v -> valueProvider.apply(v) ? VaadinIcon.CHECK_CIRCLE.create() : VaadinIcon.CLOSE.create());
    }

}
