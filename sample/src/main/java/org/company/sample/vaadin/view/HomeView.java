package org.company.sample.vaadin.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.company.sample.vaadin.MainLayout;
import org.vaadin.firitin.components.html.VLabel;

@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
public class HomeView extends VerticalLayout {

    public HomeView() {
        add(new VLabel("welcome"));
    }
}
