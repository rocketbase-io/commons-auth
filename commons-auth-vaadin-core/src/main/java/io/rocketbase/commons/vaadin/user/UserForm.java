package io.rocketbase.commons.vaadin.user;

import com.vaadin.componentfactory.multiselect.MultiComboBox;
import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import io.rocketbase.commons.api.AppCapabilityApi;
import io.rocketbase.commons.api.AppGroupApi;
import io.rocketbase.commons.api.AppTeamApi;
import io.rocketbase.commons.dto.appcapability.AppCapabilityShort;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.dto.appgroup.AppGroupShort;
import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.dto.appteam.AppTeamShort;
import io.rocketbase.commons.dto.appteam.QueryAppTeam;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import org.vaadin.firitin.components.checkbox.VCheckBox;
import org.vaadin.firitin.components.combobox.VComboBox;
import org.vaadin.firitin.components.formlayout.VFormLayout;
import org.vaadin.firitin.components.textfield.VEmailField;
import org.vaadin.firitin.components.textfield.VTextField;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UserForm extends AbstractCompositeField<FormLayout, UserForm, UserForm.AppUserGeneral> {

    @NoArgsConstructor
    @Data
    public static class AppUserGeneral {
        private String id;

        private String systemRefId;

        private String username;

        private String email;

        private Set<AppCapabilityShort> capabilities;

        private Set<AppGroupShort> groups;

        private AppTeamShort activeTeam;

        private Map<String, String> keyValues;

        private boolean enabled;

        private boolean locked;

        public AppUserGeneral(AppUserRead user) {
            if (user == null) {
                return;
            }
            id = user.getId();
            systemRefId = user.getSystemRefId();
            username = user.getUsername();
            email = user.getEmail();
            capabilities = user.getCapabilities();
            groups = user.getGroups();
            activeTeam = user.getActiveTeam() != null ? user.getActiveTeam().getTeam() : null;
            keyValues = user.getKeyValues();
            enabled = user.isEnabled();
            locked = user.isLocked();
        }
    }

    private final AppGroupApi groupApi;
    private final AppCapabilityApi capabilityApi;
    private final AppTeamApi teamApi;

    private TextField id;
    private TextField systemRefId;
    private TextField username;
    private EmailField email;
    private MultiComboBox<AppCapabilityShort> capabilities;
    private MultiComboBox<AppGroupShort> groups;
    private ComboBox<AppTeamShort> activeTeam;

    private TextField keyValues;

    private Checkbox enabled;
    private Checkbox locked;

    public UserForm(AppGroupApi groupApi, AppCapabilityApi capabilityApi, AppTeamApi teamApi) {
        super(new AppUserGeneral());
        this.groupApi = groupApi;
        this.capabilityApi = capabilityApi;
        this.teamApi = teamApi;
        initFields();
    }

    protected void initFields() {
        id = new VTextField().withFullWidth();
        systemRefId = new VTextField().withFullWidth();
        username = new VTextField().withFullWidth();

        email = new VEmailField().withFullWidth();

        capabilities = new MultiComboBox<>();
        capabilities.setItemLabelGenerator(AppCapabilityShort::getKeyPath);
        capabilities.setWidthFull();
        capabilities.setDataProvider(
                SpringDataProviderBuilder.<AppCapabilityShort, QueryAppCapability>forFunctions((page, q) -> (Page) capabilityApi.find(q, page).toPage(),
                (q) -> capabilityApi.find(q, PageRequest.of(0, 1)).getTotalElements()).build(), text -> StringUtils.hasText(text) ? QueryAppCapability.builder().keyPath(text.trim()).build() : null);


        groups = new MultiComboBox<>();
        groups.setItemLabelGenerator(AppGroupShort::getNamePath);
        groups.setWidthFull();
        groups.setDataProvider(SpringDataProviderBuilder.<AppGroupShort, QueryAppGroup>forFunctions((page, q) -> (Page) groupApi.find(q, page).toPage(),
                (q) -> groupApi.find(q, PageRequest.of(0, 1)).getTotalElements()).build(), text -> StringUtils.hasText(text) ? QueryAppGroup.builder().namePath(text.trim()).build() : null);


        activeTeam = new VComboBox<AppTeamShort>()
                .withClearButtonVisible(true)
                .withItemLabelGenerator(AppTeamShort::getName)
                .withFullWidth();

        activeTeam.setDataProvider(SpringDataProviderBuilder.<AppTeamShort, QueryAppTeam>forFunctions((page, q) -> (Page) teamApi.find(q, page).toPage(),
                (q) -> teamApi.find(q, PageRequest.of(0, 1)).getTotalElements()).build(), text -> StringUtils.hasText(text) ? QueryAppTeam.builder().name(text.trim()).personal(false).build() : QueryAppTeam.builder().personal(false).build());

        keyValues = new VTextField().withFullWidth();

        enabled = new VCheckBox(getTranslation("user.enabled")).withFullWidth();
        locked = new VCheckBox(getTranslation("user.locked")).withFullWidth();

        setReadOnly(false);
    }

    @Override
    protected void setPresentationValue(AppUserGeneral general) {
        if (general == null) {
            id.clear();
            systemRefId.clear();
            username.clear();
            email.clear();
            capabilities.clear();
            groups.clear();
            activeTeam.clear();
            keyValues.clear();
            enabled.clear();
            locked.clear();
            return;
        }
        id.setValue(general.getId());
        systemRefId.setValue(general.getSystemRefId());
        username.setValue(general.getUsername());
        email.setValue(general.getEmail());
        capabilities.setValue(general.getCapabilities() != null ? new HashSet<>(capabilityApi.find(QueryAppCapability.builder().ids(general.getCapabilities().stream().map(AppCapabilityShort::getId).collect(Collectors.toSet())).build(), PageRequest.of(0, 100)).getContent()) : null);
        groups.setValue(general.getCapabilities() != null ? new HashSet<>(groupApi.find(QueryAppGroup.builder().ids(general.getCapabilities().stream().map(AppCapabilityShort::getId).collect(Collectors.toSet())).build(), PageRequest.of(0, 100)).getContent()) : null);
        activeTeam.setValue(general.getActiveTeam() != null ? teamApi.findById(general.getActiveTeam().getId()).orElse(null) : null);
        keyValues.setValue("");
        enabled.setValue(general.isEnabled());
        locked.setValue(general.isLocked());
    }

    @Override
    protected FormLayout initContent() {
        return new VFormLayout()
                .withFullWidth()
                .withResponsiveStepsTwoCols(FormLayout.ResponsiveStep.LabelsPosition.TOP, "21em")
                .withFormItem(id, getTranslation("user.id"), 1)
                .withFormItem(systemRefId, getTranslation("user.systemRefId"), 1)
                .withFormItem(username, getTranslation("user.username"), 1)
                .withFormItem(email, getTranslation("user.email"), 1)
                .withFormItem(capabilities, getTranslation("user.capabilities"), 2)
                .withFormItem(groups, getTranslation("user.groups"), 2)
                .withFormItem(activeTeam, getTranslation("user.activeTeam"), 2)
                .withFormItem(keyValues, getTranslation("user.keyValues"), 2)
                .withFormItem(enabled, getTranslation("user.enabled"), 1)
                .withFormItem(locked, getTranslation("user.locked"), 1);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        id.setReadOnly(true);
        systemRefId.setReadOnly(true);
        username.setReadOnly(true);
        email.setReadOnly(true);
        capabilities.setReadOnly(readOnly);
        groups.setReadOnly(readOnly);
        activeTeam.setReadOnly(readOnly);
        keyValues.setReadOnly(readOnly);
        enabled.setReadOnly(readOnly);
        locked.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return capabilities.isReadOnly();
    }
}
