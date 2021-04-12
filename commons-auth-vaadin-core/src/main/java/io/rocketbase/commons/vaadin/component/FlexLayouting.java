package io.rocketbase.commons.vaadin.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import org.vaadin.firitin.components.html.VLabel;
import org.vaadin.firitin.fluency.ui.FluentComponent;

@CssImport("./io/rocketbase/flex-layouting.css")
public class FlexLayouting extends Composite<FlexLayout>
        implements HasStyle, HasSize, FluentComponent<FlexLayouting> {

    private static final String CLASS_NAME = "flex-layouting";

    protected final FlexLayout header;
    protected final FlexLayout content;
    protected final FlexLayout footer;

    public FlexLayouting() {
        addClassName(CLASS_NAME);

        header = new FlexLayout();
        header.addClassName(CLASS_NAME + "__header");

        content = new FlexLayout();
        content.addClassName(CLASS_NAME + "__content");
        content.setFlexDirection(FlexDirection.COLUMN);

        footer = new FlexLayout();
        footer.addClassName(CLASS_NAME + "__footer");

        getContent().add(header, content, footer);
    }

    public void setHeader(Component... components) {
        this.header.removeAll();
        this.header.add(components);

        setClassName("has-header", components != null && components.length > 0);
    }

    public void setHeader(String header) {
        setHeader(new VLabel(header).withClassName("header"));
    }

    public FlexLayouting withHeader(String header) {
        setHeader(header);
        return this;
    }

    public FlexLayouting withHeader(Component... components) {
        setHeader(components);
        return this;
    }

    public void setContent(Component... components) {
        this.content.removeAll();
        this.content.add(components);
    }

    public FlexLayouting withContent(Component... components) {
        setContent(components);
        return this;
    }

    public void setFooter(Component... components) {
        this.footer.removeAll();
        this.footer.add(components);
        setClassName("has-footer", components != null && components.length > 0);
    }

    public FlexLayouting withFooter(Component... components) {
        setFooter(components);
        return this;
    }
}
