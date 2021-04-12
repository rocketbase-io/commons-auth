package io.rocketbase.commons.vaadin.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

@CssImport("./io/rocketbase/filter-bar.css")
public class FilterBar extends Composite<FlexLayout> implements HasStyle {

    private final FlexLayout filter;
    private final ButtonBar buttonBar;

    public FilterBar(Component... components) {
        filter = new FlexLayout();
        filter.addClassName("filter-bar__content");
        if (components != null) {
            filter.add(components);
        }
        buttonBar = new ButtonBar();
    }

    public void addButton(Component button) {
        buttonBar.addButton(button);
    }

    public FilterBar withButton(Component button) {
        addButton(button);
        return this;
    }

    public void addFilter(Component filter) {
        this.filter.add(filter);
    }

    public FilterBar withFilter(Component filter) {
        addFilter(filter);
        return this;
    }

    @Override
    protected FlexLayout initContent() {
        FlexLayout content = new FlexLayout();
        content.addClassName("filter-bar__wrapper");
        content.add(filter);
        content.add(buttonBar);
        return content;
    }
}
