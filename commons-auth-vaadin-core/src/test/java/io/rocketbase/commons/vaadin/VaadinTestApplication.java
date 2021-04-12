package io.rocketbase.commons.vaadin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class VaadinTestApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(VaadinTestApplication.class, args);
    }

}
