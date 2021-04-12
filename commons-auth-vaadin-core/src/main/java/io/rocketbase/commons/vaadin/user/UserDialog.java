package io.rocketbase.commons.vaadin.user;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import io.rocketbase.commons.api.AppCapabilityApi;
import io.rocketbase.commons.api.AppGroupApi;
import io.rocketbase.commons.api.AppTeamApi;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.appuser.AppUserUpdate;
import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.commons.util.Nulls;
import io.rocketbase.commons.vaadin.component.Dialogs;
import io.rocketbase.commons.vaadin.component.FilterBar;
import io.rocketbase.commons.vaadin.component.FlexLayouting;
import org.springframework.data.util.Pair;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.dialog.VDialog;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import java.util.function.Consumer;

public class UserDialog extends Composite<Dialog> {

    protected final Consumer<Pair<String, AppUserUpdate>> updateConsumer;
    protected final Consumer<String> deleteConsumer;
    private final AppGroupApi groupApi;
    private final AppCapabilityApi capabilityApi;
    private final AppTeamApi teamApi;

    protected FlexLayouting flexLayouting;
    protected UserForm userForm;
    protected UserProfileForm profileForm;
    protected UserSettingForm settingForm;

    protected Button deleteButton, editButton, saveButton;

    protected AppUserRead currentUser;

    public UserDialog(Consumer<Pair<String, AppUserUpdate>> updateConsumer, Consumer<String> deleteConsumer,
                      AppGroupApi groupApi, AppCapabilityApi capabilityApi, AppTeamApi teamApi) {
        this.updateConsumer = updateConsumer;
        this.deleteConsumer = deleteConsumer;
        this.groupApi = groupApi;
        this.capabilityApi = capabilityApi;
        this.teamApi = teamApi;

        userForm = new UserForm(groupApi, capabilityApi, teamApi);
        userForm.setReadOnly(true);

        profileForm = new UserProfileForm();
        profileForm.setReadOnly(true);

        settingForm = new UserSettingForm();
        settingForm.setReadOnly(true);

        flexLayouting = new FlexLayouting();
        flexLayouting.setHeader("...");
        flexLayouting.setFooter(initFooter());

        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        accordion.add(getTranslation("user.general"), userForm);
        accordion.add(getTranslation("user.profile"), profileForm);
        accordion.add(getTranslation("user.setting"), settingForm);

        flexLayouting.setContent(new VVerticalLayout(accordion));
    }

    protected FilterBar initFooter() {
        FilterBar filterBar = new FilterBar();
        deleteButton = new VButton(VaadinIcon.TRASH.create(), getTranslation("user.buttonDelete"), e -> {
            Dialogs.openConfirmDelete(() -> deleteConsumer.accept(currentUser.getId()));
        }).withThemeVariants(ButtonVariant.LUMO_ERROR)
                .withVisible(deleteConsumer != null);
        filterBar.addFilter(deleteButton);

        editButton = new VButton(VaadinIcon.PENCIL.create(), getTranslation("user.buttonEdit"), e -> {
            userForm.setReadOnly(false);
            profileForm.setReadOnly(false);
            settingForm.setReadOnly(false);
            saveButton.setVisible(true);
            editButton.setVisible(false);
        }).withVisible(updateConsumer != null)
                .withThemeVariants(ButtonVariant.LUMO_PRIMARY);
        filterBar.addFilter(editButton);

        saveButton = new VButton(getTranslation("user.buttonSave"), e -> {
            getContent().close();
        }).withVisible(false)
                .withThemeVariants(ButtonVariant.LUMO_PRIMARY);
        filterBar.addButton(saveButton);
        filterBar.addButton(new VButton(getTranslation("user.buttonCancel"), e -> getContent().close()));
        return filterBar;
    }

    public void showUser(AppUserRead user) {
        userForm.setValue(new UserForm.AppUserGeneral(user));
        profileForm.setValue(Nulls.notNull(user, AppUserRead::getProfile, null));
        settingForm.setValue(Nulls.notNull(user, AppUserRead::getSetting, null));
        flexLayouting.setHeader(getTranslation("user.editUser", Nulls.notNull(user, AppUserReference::getDisplayName)));

        userForm.setReadOnly(true);
        profileForm.setReadOnly(true);
        settingForm.setReadOnly(true);
        saveButton.setVisible(false);
        editButton.setVisible(updateConsumer != null);
        deleteButton.setVisible(deleteButton != null);
        getContent().open();
    }

    @Override
    protected Dialog initContent() {
        return new VDialog(flexLayouting)
                .withWidth("80vw")
                .withHeight("90vh")
                .withCloseOnEsc(true)
                .withCloseOnOutsideClick(true);
    }
}
