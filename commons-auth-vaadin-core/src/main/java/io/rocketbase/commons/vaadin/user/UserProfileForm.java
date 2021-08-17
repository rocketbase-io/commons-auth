package io.rocketbase.commons.vaadin.user;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import io.rocketbase.commons.dto.address.Gender;
import io.rocketbase.commons.model.user.SimpleUserProfile;
import io.rocketbase.commons.model.user.UserProfile;
import org.vaadin.firitin.components.formlayout.VFormLayout;
import org.vaadin.firitin.components.textfield.VTextArea;
import org.vaadin.firitin.components.textfield.VTextField;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.stream.Collectors;

public class UserProfileForm extends AbstractCompositeField<FormLayout, UserProfileForm, UserProfile> {

    private Select<Gender> gender;
    private TextField title;
    private TextField firstName;
    private TextField lastName;

    private TextField avatar;

    // phoneNumbers
    // onlineProfiles

    private TextArea about;
    private TextField location;
    private Select<String> country;
    private TextField jobTitle;
    private TextField organization;


    public UserProfileForm() {
        super(new SimpleUserProfile());
        initFields();
    }

    protected void initFields() {
        gender = new Select<>();
        gender.setWidthFull();
        gender.setItems(EnumSet.allOf(Gender.class));
        gender.setItemLabelGenerator(v -> v.getTranslation().getTranslated(UI.getCurrent().getLocale()));

        title = new VTextField().withFullWidth();
        firstName = new VTextField().withFullWidth();
        lastName = new VTextField().withFullWidth();

        avatar = new VTextField().withFullWidth();

        about = new VTextArea().withFullWidth();

        location = new VTextField().withFullWidth();

        country = new Select<>();
        country.setWidthFull();
        country.setItems(Arrays.stream(Locale.getISOCountries())
                .map(String::toLowerCase)
                .sorted()
                .collect(Collectors.toList()));
        country.setItemLabelGenerator(v -> v == null ? "" : String.format("%s - %s", v, new Locale("", v).getDisplayCountry(UI.getCurrent().getLocale())));

        jobTitle = new VTextField().withFullWidth();
        organization = new VTextField().withFullWidth();
    }

    @Override
    protected void setPresentationValue(UserProfile userProfile) {
        if (userProfile == null) {
            gender.clear();
            title.clear();
            firstName.clear();
            lastName.clear();
            avatar.clear();
            about.clear();
            location.clear();
            country.clear();
            jobTitle.clear();
            organization.clear();
            return;
        }
        gender.setValue(userProfile.getGender());
        title.setValue(userProfile.getTitle());
        firstName.setValue(userProfile.getFirstName());
        lastName.setValue(userProfile.getLastName());
        avatar.setValue(userProfile.getAvatar());
        about.setValue(userProfile.getAbout());
        location.setValue(userProfile.getLocation());
        country.setValue(userProfile.getCountry());
        jobTitle.setValue(userProfile.getJobTitle());
        organization.setValue(userProfile.getOrganization());
    }

    @Override
    protected FormLayout initContent() {
        return new VFormLayout()
                .withFullWidth()
                .withResponsiveStepsTwoCols(FormLayout.ResponsiveStep.LabelsPosition.TOP, "21em")
                .withFormItem(gender, getTranslation("user.gender"), 1)
                .withFormItem(title, getTranslation("user.title"), 1)
                .withFormItem(firstName, getTranslation("user.firstName"), 1)
                .withFormItem(lastName, getTranslation("user.lastName"), 1)
                .withFormItem(avatar, getTranslation("user.avatar"), 2)
                .withFormItem(about, getTranslation("user.about"), 2)
                .withFormItem(location, getTranslation("user.location"), 1)
                .withFormItem(country, getTranslation("user.country"), 1)
                .withFormItem(jobTitle, getTranslation("user.jobTitle"), 1)
                .withFormItem(organization, getTranslation("user.organization"), 1);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        gender.setReadOnly(readOnly);
        title.setReadOnly(readOnly);
        firstName.setReadOnly(readOnly);
        lastName.setReadOnly(readOnly);
        avatar.setReadOnly(readOnly);
        about.setReadOnly(readOnly);
        location.setReadOnly(readOnly);
        country.setReadOnly(readOnly);
        jobTitle.setReadOnly(readOnly);
        organization.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return gender.isReadOnly();
    }
}
