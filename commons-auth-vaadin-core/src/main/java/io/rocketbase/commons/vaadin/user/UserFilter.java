package io.rocketbase.commons.vaadin.user;

import com.vaadin.componentfactory.multiselect.MultiComboBox;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import io.rocketbase.commons.api.AppCapabilityApi;
import io.rocketbase.commons.api.AppGroupApi;
import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appgroup.AppGroupShort;
import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CssImport("./io/rocketbase/custom-filter-field.css")
public class UserFilter extends CustomField<QueryAppUser> {

    private final AppGroupApi groupApi;
    private final AppCapabilityApi capabilityApi;

    private final Select<PropertyFilterWrapper> searchProperty;
    private final TextField search;
    private final Checkbox enabled;

    private final MultiComboBox<AppGroupRead> groups;
    private final MultiComboBox<AppCapabilityRead> capabilities;

    protected static final List<PropertyFilterWrapper> EXTRA_FILTERS = Arrays.asList(new PropertyFilterWrapper("freetext"),
            new PropertyFilterWrapper("username"),
            new PropertyFilterWrapper("firstName"),
            new PropertyFilterWrapper("lastName"),
            new PropertyFilterWrapper("email"),
            new PropertyFilterWrapper("systemRefId"));


    public UserFilter(AppGroupApi groupApi, AppCapabilityApi capabilityApi) {
        super(new QueryAppUser());
        this.groupApi = groupApi;
        this.capabilityApi = capabilityApi;

        searchProperty = new Select<>();
        searchProperty.setWidth("150px");
        searchProperty.setItemLabelGenerator(v -> getTranslation("user.filter." + v.getKey()));
        searchProperty.setItems(EXTRA_FILTERS);
        searchProperty.setValue(EXTRA_FILTERS.get(0));

        search = new TextField();
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.setSuffixComponent(searchProperty);
        search.setPlaceholder(getTranslation("user.typeToSearch"));
        search.setValueChangeMode(ValueChangeMode.LAZY);
        search.setValueChangeTimeout(200);
        search.addValueChangeListener(e -> updateValue());
        search.setWidth("40vw");

        enabled = new Checkbox(getTranslation("user.enabled"), true);
        enabled.setVisible(false);

        groups = new MultiComboBox<>();
        groups.setItemLabelGenerator(AppGroupRead::getNamePath);
        groups.setClearButtonVisible(true);
        groups.setVisible(false);

        capabilities = new MultiComboBox<>();
        capabilities.setItemLabelGenerator(AppCapabilityRead::getKeyPath);
        capabilities.setClearButtonVisible(true);
        capabilities.setVisible(false);

        Div wrapper = new Div(search,
                buildExtraFilter(),
                enabled,
                groups,
                capabilities);
        wrapper.addClassName("custom-filter__wrapper");
        add(wrapper);
    }

    protected Component buildExtraFilter() {
        Button filter = new Button(VaadinIcon.FILTER.create());
        filter.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        ContextMenu contextMenu = new ContextMenu(filter);
        contextMenu.setOpenOnClick(true);

        MenuItem menuEnabled = contextMenu.addItem(getTranslation("user.filter.enabled"), e -> {
            this.enabled.setVisible(e.getSource().isChecked());
            updateValue();
        });
        menuEnabled.setCheckable(true);
        menuEnabled.setChecked(false);

        MenuItem menuGroups = contextMenu.addItem(getTranslation("user.filter.groups"), e -> {
            this.groups.clear();
            this.groups.setVisible(e.getSource().isChecked());
            if (e.getSource().isChecked()) {
                this.groups.setDataProvider(SpringDataProviderBuilder.<AppGroupRead, QueryAppGroup>forFunctions((page, q) -> groupApi.find(q, page).toPage(),
                        (q) -> groupApi.find(q, PageRequest.of(0, 1)).getTotalElements()).build(), text -> StringUtils.hasText(text) ? QueryAppGroup.builder().namePath(text.trim()).build() : null);
            }
        });
        menuGroups.setCheckable(true);
        menuGroups.setChecked(false);

        MenuItem menuCapabilities = contextMenu.addItem(getTranslation("user.filter.capabilities"), e -> {
            this.capabilities.clear();
            this.capabilities.setVisible(e.getSource().isChecked());
            if (e.getSource().isChecked()) {
                this.capabilities.setDataProvider(SpringDataProviderBuilder.<AppCapabilityRead, QueryAppCapability>forFunctions((page, q) -> capabilityApi.find(q, page).toPage(),
                        (q) -> capabilityApi.find(q, PageRequest.of(0, 1)).getTotalElements()).build(), text -> StringUtils.hasText(text) ? QueryAppCapability.builder().keyPath(text.trim()).build() : null);
            }
        });
        menuCapabilities.setCheckable(true);
        menuCapabilities.setChecked(false);

        return filter;
    }

    @Override
    protected QueryAppUser generateModelValue() {
        QueryAppUser query = new QueryAppUser();
        if (StringUtils.hasText(search.getValue())) {
            String filterValue = search.getValue().trim();
            switch (EXTRA_FILTERS.indexOf(searchProperty.getValue())) {
                case 0:
                    query.setFreetext(filterValue);
                    break;
                case 1:
                    query.setUsername(filterValue);
                    break;
                case 2:
                    query.setFirstName(filterValue);
                    break;
                case 3:
                    query.setLastName(filterValue);
                    break;
                case 4:
                    query.setEmail(filterValue);
                    break;
                case 5:
                    query.setSystemRefId(filterValue);
                    break;
            }
        }
        if (enabled.isVisible()) {
            query.setEnabled(enabled.getValue());
        }
        if (groups.isVisible() && groups.getValue() != null && !groups.getValue().isEmpty()) {
            query.setGroupIds(groups.getValue().stream().map(AppGroupShort::getId).collect(Collectors.toSet()));
        }

        return query;
    }

    @Override
    protected void setPresentationValue(QueryAppUser queryAppUser) {
        searchProperty.setValue(EXTRA_FILTERS.get(0));
        enabled.setVisible(false);

        if (queryAppUser != null) {
            search.setValue(queryAppUser.getFreetext());
            if (queryAppUser.getEnabled() != null) {
                enabled.setVisible(true);
                enabled.setValue(queryAppUser.getEnabled());
            }
            if (queryAppUser.getGroupIds() != null) {
                groups.setVisible(true);
                enabled.setValue(queryAppUser.getEnabled());
            }
        }
    }

    @Data
    @RequiredArgsConstructor
    private static class PropertyFilterWrapper {
        private final String key;
    }
}
