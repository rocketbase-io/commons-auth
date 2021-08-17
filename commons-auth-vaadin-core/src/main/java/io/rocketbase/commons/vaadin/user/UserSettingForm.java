package io.rocketbase.commons.vaadin.user;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import io.rocketbase.commons.model.user.SimpleUserSetting;
import io.rocketbase.commons.model.user.UserSetting;
import org.vaadin.firitin.components.formlayout.VFormLayout;
import org.vaadin.firitin.components.textfield.VTextField;

import java.util.Arrays;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class UserSettingForm extends AbstractCompositeField<FormLayout, UserSettingForm, UserSetting> {

    private TextField locale;
    private Select<String> currentTimeZone;

    private TextField dateFormat;
    private TextField timeFormat;
    private TextField dateTimeFormat;

    public UserSettingForm() {
        super(new SimpleUserSetting());
        initFields();
    }

    protected void initFields() {
        locale = new VTextField().withFullWidth();

        currentTimeZone = new Select<>();
        currentTimeZone.setItems(Arrays.stream(TimeZone.getAvailableIDs()).sorted().collect(Collectors.toList()));
        currentTimeZone.setWidthFull();

        dateFormat = new VTextField().withFullWidth();
        timeFormat = new VTextField().withFullWidth();
        dateTimeFormat = new VTextField().withFullWidth();
    }

    @Override
    protected void setPresentationValue(UserSetting userSetting) {
        if (userSetting == null) {
            locale.clear();
            currentTimeZone.clear();
            dateFormat.clear();
            timeFormat.clear();
            dateTimeFormat.clear();
            return;
        }
        locale.setValue(userSetting.getLocale());
        currentTimeZone.setValue(userSetting.getCurrentTimeZone());
        dateFormat.setValue(userSetting.getDateFormat());
        timeFormat.setValue(userSetting.getTimeFormat());
        dateTimeFormat.setValue(userSetting.getDateTimeFormat());
    }

    @Override
    protected FormLayout initContent() {
        return new VFormLayout()
                .withFullWidth()
                .withResponsiveStepsOneCol(FormLayout.ResponsiveStep.LabelsPosition.TOP)
                .withFormItem(locale, getTranslation("user.locale"), 1)
                .withFormItem(currentTimeZone, getTranslation("user.currentTimeZone"), 1)
                .withFormItem(dateFormat, getTranslation("user.dateFormat"), 1)
                .withFormItem(timeFormat, getTranslation("user.timeFormat"), 1)
                .withFormItem(dateTimeFormat, getTranslation("user.dateTimeFormat"), 1);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        locale.setReadOnly(readOnly);
        currentTimeZone.setReadOnly(readOnly);
        dateFormat.setReadOnly(readOnly);
        timeFormat.setReadOnly(readOnly);
        dateTimeFormat.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return locale.isReadOnly();
    }
}
