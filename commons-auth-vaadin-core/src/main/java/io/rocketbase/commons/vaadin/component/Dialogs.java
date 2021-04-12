package io.rocketbase.commons.vaadin.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.dialog.VDialog;
import org.vaadin.firitin.components.html.VH3;
import org.vaadin.firitin.components.html.VLabel;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import java.util.function.Consumer;

public abstract class Dialogs {
    public static VDialog openConfirmDialog(String headline, @Nullable String question, @Nullable String okCaption, Runnable onOk) {
        return buildConfirmDialog(headline, question, okCaption, onOk).getFirst();
    }

    public static VDialog openConfirmDelete(Runnable onDelete) {
        UI ui = UI.getCurrent();
        return openConfirmDelete(ui.getTranslation("user.buttonDelete"), ui.getTranslation("user.buttonCancel"), ui.getTranslation("user.deleteDataset"), ui.getTranslation("user.areYouSure"), onDelete);
    }

    public static VDialog openConfirmDelete(String yes, String no, @Nullable String headline, @Nullable String question, Runnable onDelete) {
        Pair<VDialog, ButtonBar> pair = buildConfirmDialog(headline, question, yes, onDelete);
        // recolor delete
        Button yesBtn = (Button) pair.getSecond().getButtonAtIndex(0);
        yesBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        // add cancel
        Button noBtn = (Button) pair.getSecond().getButtonAtIndex(1);
        noBtn.setText(no);
        noBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        return pair.getFirst();
    }

    private static Pair<VDialog, ButtonBar> buildConfirmDialog(String headline, @Nullable String question, @Nullable String okCaption, Runnable onOk) {
        VDialog dialog = new VDialog();

        ButtonBar buttonBar = new ButtonBar();

        buttonBar.addButton(new VButton(okCaption, e -> {
            onOk.run();
            dialog.close();
        }).withFullWidth().withThemeVariants(ButtonVariant.LUMO_PRIMARY));

        buttonBar.addButton(new VButton(UI.getCurrent().getTranslation("user.buttonCancel"), e -> dialog.close())
                .withFullWidth().withThemeVariants(ButtonVariant.LUMO_TERTIARY));


        VVerticalLayout layout = new VVerticalLayout()
                .withPadding(false);
        if (headline != null) {
            layout.add(new VLabel(headline)
                    .withStyle("font-size", "var(--lumo-font-size-xl)")
                    .withStyle("font-weight", "bolder")
                    .withStyle("text-align", "center")
                    .withFullWidth());
        }
        if (question != null) {
            layout.add(new VLabel(question)
                    .withStyle("text-align", "center")
                    .withFullWidth());
        }
        layout.add(buttonBar);

        dialog.add(layout);

        dialog.setCloseOnOutsideClick(false);
        dialog.withMinWidth("300px");
        dialog.open();
        return Pair.of(dialog, buttonBar);
    }

    public static VDialog createComplexDialog(@Nullable String headline, Component content,
                                              Consumer<VDialog> onClick) {
        return createComplexDialog(headline, content, null, new VButton(UI.getCurrent().getTranslation("user.buttonCancel")), onClick);
    }

    public static VDialog createComplexDialog(@Nullable String headline, Component content, Button button,
                                              Consumer<VDialog> onClick) {
        return createComplexDialog(headline, content, button, new VButton(UI.getCurrent().getTranslation("user.buttonCancel")), onClick);
    }

    /**
     * creates a Dialog with FlexLayouting. Adds at the end of Buttons a cancel button
     */
    public static VDialog createComplexDialog(@Nullable String headline, Component content, @Nullable Button confirm,
                                              Button closeButton, Consumer<VDialog> onClick) {
        VDialog dialog = new VDialog();

        FlexLayouting layout = new FlexLayouting();
        if (headline != null) {
            layout.setHeader(headline);
        }
        layout.setContent(content);

        ButtonBar buttonBar = new ButtonBar();
        if (confirm != null) {
            buttonBar.addButton(confirm);
            confirm.addClickListener(e -> onClick.accept(dialog));
        }
        closeButton.addClickListener(e -> dialog.close());
        buttonBar.addButton(closeButton);
        layout.setFooter(buttonBar);

        dialog.add(layout);
        return dialog;
    }

    public static VDialog pleaseWait() {
        return pleaseWait(UI.getCurrent().getTranslation("user.pleaseWait"));
    }

    public static VDialog pleaseWait(String label) {
        VDialog dialog = new VDialog();

        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        VH3 pleaseWait = new VH3(label).withFullWidth();
        pleaseWait.getElement().getStyle().set("text-align", "center");

        dialog.add(new VerticalLayout(pleaseWait, progressBar));
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.open();
        return dialog;
    }
}
