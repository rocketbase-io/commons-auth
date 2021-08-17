package org.company.sample.vaadin.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import io.rocketbase.commons.api.AppCapabilityApi;
import io.rocketbase.commons.api.AppGroupApi;
import io.rocketbase.commons.api.AppTeamApi;
import io.rocketbase.commons.api.AppUserApi;
import io.rocketbase.commons.vaadin.user.UserScreen;
import org.company.sample.vaadin.MainLayout;

@PageTitle("User")
@Route(value = "user", layout = MainLayout.class)
public class UserView extends UserScreen {

    public UserView(AppUserApi appUserApi, AppGroupApi appGroupApi, AppCapabilityApi appCapabilityApi, AppTeamApi appTeamApi) {
        super(appUserApi, appGroupApi, appCapabilityApi, appTeamApi);
    }
}
