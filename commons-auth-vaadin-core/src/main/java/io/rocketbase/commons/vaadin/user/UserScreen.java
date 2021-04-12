package io.rocketbase.commons.vaadin.user;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.rocketbase.commons.api.AppCapabilityApi;
import io.rocketbase.commons.api.AppGroupApi;
import io.rocketbase.commons.api.AppTeamApi;
import io.rocketbase.commons.api.AppUserApi;
import io.rocketbase.commons.vaadin.component.FilterBar;
import lombok.Getter;

@Getter
public class UserScreen extends Composite<VerticalLayout> {

    protected final UserFilter filter;
    protected final UserGrid grid;

    public UserScreen(AppUserApi appUserApi, AppGroupApi appGroupApi, AppCapabilityApi appCapabilityApi, AppTeamApi appTeamApi) {
        filter = new UserFilter(appGroupApi, appCapabilityApi);
        grid = new UserGrid(appUserApi, () -> filter.getValue(), new UserDialog(update -> appUserApi.patch(update.getFirst(), update.getSecond()), deleteId -> appUserApi.delete(deleteId),
                appGroupApi, appCapabilityApi, appTeamApi));
        filter.addValueChangeListener(e -> grid.reload());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        grid.reload();
    }

    @Override
    protected VerticalLayout initContent() {
        VerticalLayout layout = new VerticalLayout();

        Button addButton = new Button(getTranslation("user.buttonAdd"), VaadinIcon.PLUS.create(), e -> {
            Notification.show("added item...");
        });
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        layout.add(new FilterBar(filter)
                .withButton(addButton));
        layout.add(grid);
        layout.setSizeFull();
        return layout;
    }
}
