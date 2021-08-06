package io.rocketbase.commons.vaadin.user;


import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import io.rocketbase.commons.api.AppUserApi;
import io.rocketbase.commons.dto.appcapability.AppCapabilityShort;
import io.rocketbase.commons.dto.appgroup.AppGroupShort;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.user.OnlineProfile;
import io.rocketbase.commons.model.user.PhoneNumber;
import io.rocketbase.commons.model.user.UserProfile;
import io.rocketbase.commons.model.user.UserSetting;
import io.rocketbase.commons.util.Nulls;
import io.rocketbase.commons.vaadin.renderer.IconRenderer;
import io.rocketbase.commons.vaadin.renderer.InstantRenderer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.spring.dataprovider.PageableDataProvider;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class UserGrid extends Grid<AppUserRead> {

    private final AppUserApi appUserApi;
    private final Supplier<QueryAppUser> filterSupplier;
    private final UserDialog dialog;

    public UserGrid(AppUserApi appUserApi, Supplier<QueryAppUser> filterSupplier, UserDialog dialog) {
        this.appUserApi = appUserApi;
        this.filterSupplier = filterSupplier;
        this.dialog = dialog;
        initColumns();
        //initDetails();
        addSelectionListener(e -> {
            if (e.getFirstSelectedItem().isPresent()) {
                dialog.showUser(e.getFirstSelectedItem().get());
            }
        });
    }

    protected void initColumns() {
        Column<AppUserRead> avatar = addColumn(TemplateRenderer.<AppUserRead>of("<img src$='[[item.avatar]]' alt='avatar' style='max-height:35px; width: auto'>")
                .withProperty("avatar", v -> Nulls.notEmpty(v, AppUserRead::getAvatar, "/img/avatar-placeholder.png")))
                .setHeader(getTranslation("user.avatar"))
                .setFlexGrow(0)
                .setWidth("80px")
                .setAutoWidth(false);

        Column<AppUserRead> username = addColumn(AppUserRead::getUsername)
                .setHeader(getTranslation("user.username"))
                .setSortProperty("username")
                .setFlexGrow(0)
                .setWidth("200px")
                .setAutoWidth(false);

        Column<AppUserRead> firstName = addColumn(AppUserRead::getFirstName)
                .setHeader(getTranslation("user.firstName"))
                .setSortProperty("firstName")
                .setAutoWidth(true);

        Column<AppUserRead> lastName = addColumn(AppUserRead::getLastName)
                .setHeader(getTranslation("user.lastName"))
                .setSortProperty("lastName")
                .setAutoWidth(true);

        Column<AppUserRead> email = addColumn(AppUserRead::getEmail)
                .setHeader(getTranslation("user.email"))
                .setSortProperty("email")
                .setAutoWidth(true);

        Column<AppUserRead> created = addColumn(new InstantRenderer<>(AppUserRead::getCreated))
                .setHeader(getTranslation("user.created"))
                .setSortProperty("created")
                .setWidth("150px")
                .setFlexGrow(0)
                .setAutoWidth(false);

        Column<AppUserRead> lastLogin = addColumn(new InstantRenderer<>(AppUserRead::getLastLogin))
                .setHeader(getTranslation("user.lastLogin"))
                .setSortProperty("lastLogin")
                .setWidth("150px")
                .setFlexGrow(0)
                .setAutoWidth(false);

        addColumn(IconRenderer.createBoolean(AppUserRead::isEnabled))
                .setHeader(getTranslation("user.enabled"))
                .setSortProperty("enabled")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setWidth("100px")
                .setFlexGrow(0)
                .setAutoWidth(false);

        sort(GridSortOrder.asc(username).build());
    }

    private void initDetails() {
        setItemDetailsRenderer(TemplateRenderer.<AppUserRead>of("<div style='padding: 10px; width: 100%; box-sizing: border-box;'><div class='user-grid-detail__wrapper' style=''>" +
                "<div class='profile'>" +
                "<img src$='[[item.avatar]]' alt='avatar' class='avatar'>" +
                "<p class='name'>[[item.salutation]] [[item.title]] [[item.fullName]]</p>" +
                "<p class='geo'><span>country: </span><b>[[item.country]]</b> location: <b>[[item.location]]</b></p>" +
                "<p class='geo'><span>jobTitle: </span><b>[[item.jobTitle]]</b> organization: <b>[[item.organization]]</b></p>" +
                "<p class='phone-numbers'><span>phoneNumbers: </span><b>[[item.phoneNumbers]]</b></p>" +
                "<p class='online-profiles'><span>onlineProfiles: </span><b>[[item.onlineProfiles]]</b></p>" +
                "<p class='about'>[[item.about]]</b></p>" +
                "</div>" +
                "<div class='setting'>" +
                "<p class='locale'><span>locale: </span><b>[[item.locale]]</b></p>" +
                "<p class='date-format'><span>dateFormat: </span><b>[[item.dateFormat]]</b></p>" +
                "<p class='date-time-format'><span>dateTimeFormat: </span><b>[[item.dateTimeFormat]]</b></p>" +
                "<p class='current-time-zone'><span>currentTimeZone: </span><b>[[item.currentTimeZone]]</b></p>" +
                "</div>" +
                "<div class='user'>" +
                "<p class='capabilities'><span>capabilities: </span><b>[[item.capabilities]]</b></p>" +
                "<p class='groups'><span>groups: </span><b>[[item.groups]]</b></p>" +
                "<p class='keyValues'><span>keyValues: </span><b>[[item.keyValues]]</b></p>" +
                "</div>" +
                "</div></div>")
                .withProperty("avatar", v -> Nulls.notNull(v.getProfile(), UserProfile::getAvatar, "/img/avatar-placeholder.png"))
                .withProperty("salutation", v -> Nulls.notNull(v.getProfile(), UserProfile::getSalutation))
                .withProperty("title", v -> Nulls.notNull(v.getProfile(), UserProfile::getTitle))
                .withProperty("fullName", v -> Nulls.notNull(v.getProfile(), UserProfile::getFullName))
                .withProperty("country", v -> Nulls.notNull(v.getProfile(), UserProfile::getCountry))
                .withProperty("location", v -> Nulls.notNull(v.getProfile(), UserProfile::getLocation))
                .withProperty("phoneNumbers", v -> Nulls.notNull(v.getProfile(), UserProfile::getPhoneNumbers, Collections.<PhoneNumber>emptySet()).stream().map(e -> String.format("'%s': '%s'", e.getType(), e.getNumber())).collect(Collectors.toList()))
                .withProperty("onlineProfiles", v -> Nulls.notNull(v.getProfile(), UserProfile::getOnlineProfiles, Collections.<OnlineProfile>emptySet()).stream().map(e -> String.format("'%s': '%s'", e.getType(), e.getValue())).collect(Collectors.toList()))
                .withProperty("about", v -> Nulls.notNull(v.getProfile(), UserProfile::getAbout))
                .withProperty("jobTitle", v -> Nulls.notNull(v.getProfile(), UserProfile::getJobTitle))
                .withProperty("organization", v -> Nulls.notNull(v.getProfile(), UserProfile::getOrganization))
                .withProperty("locale", v -> Nulls.notNull(v.getSetting(), UserSetting::getLocale))
                .withProperty("dateFormat", v -> Nulls.notNull(v.getSetting(), UserSetting::getDateFormat))
                .withProperty("timeFormat", v -> Nulls.notNull(v.getSetting(), UserSetting::getTimeFormat))
                .withProperty("dateTimeFormat", v -> Nulls.notNull(v.getSetting(), UserSetting::getDateTimeFormat))
                .withProperty("currentTimeZone", v -> Nulls.notNull(v.getSetting(), UserSetting::getCurrentTimeZone))
                .withProperty("capabilities", v -> Nulls.notNull(v.getCapabilities()).stream().map(AppCapabilityShort::getKeyPath).collect(Collectors.joining(", ")))
                .withProperty("groups", v -> Nulls.notNull(v.getGroups()).stream().map(AppGroupShort::getNamePath).collect(Collectors.joining(", ")))
                .withProperty("keyValues", v -> Nulls.notNull(v.getKeyValues()).entrySet().stream().map(e -> String.format("'%s': '%s'", e.getKey(), e.getValue())).collect(Collectors.joining(", ")))
        );
    }

    public void reload() {
        scrollToStart();
        setDataProvider(new PageableDataProvider<AppUserRead, Object>() {
            @Override
            protected int sizeInBackEnd(Query<AppUserRead, Object> query) {
                return (int) appUserApi.find(filterSupplier != null ? filterSupplier.get() : null, PageRequest.of(0, 1)).getTotalElements();
            }

            @Override
            protected Page<AppUserRead> fetchFromBackEnd(Query<AppUserRead, Object> query, Pageable pageable) {
                return appUserApi.find(filterSupplier != null ? filterSupplier.get() : null, pageable).toPage();
            }

            @Override
            protected List<QuerySortOrder> getDefaultSortOrders() {
                return QuerySortOrder.asc("username").build();
            }
        });
    }
}
