package io.rocketbase.commons.vaadin.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

import java.util.ArrayList;
import java.util.List;

@CssImport("./io/rocketbase/button-bar.css")
public class ButtonBar extends Composite<FlexLayout> implements HasStyle {

    private static final String CLASS_WRAPPER = "button-bar__wrapper";
    private static final String CLASS_RESPONSIVE = "button-bar__wrapper--responsive";

    private FlexLayout content;
    private final List<Component> buttons = new ArrayList<>();

    private boolean responsive = false;

    public void addButton(Component button) {
        buttons.add(button);
        checkRepaint();
    }

    public ButtonBar withButton(Component button) {
        addButton(button);
        checkRepaint();
        return this;
    }

    public ButtonBar responsive() {
        responsive = true;
        checkResponsive();
        return this;
    }

    public Component getButtonAtIndex(int index) {
        return buttons.get(index);
    }

    @Override
    protected FlexLayout initContent() {
        content = new FlexLayout();
        content.addClassName(CLASS_WRAPPER);
        checkResponsive();
        repaintButtons();
        return content;
    }

    protected void checkResponsive() {
        if (content != null) {
            if (responsive) {
                content.addClassName(CLASS_RESPONSIVE);
            } else {
                content.removeClassName(CLASS_RESPONSIVE);
            }
        }
    }

    protected void checkRepaint() {
        if (content != null) {
            repaintButtons();
        }
    }

    protected void repaintButtons() {
        content.removeAll();
        content.add(buttons.toArray(new Component[]{}));
    }
}
