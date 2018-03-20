package io.rocketbase.commons.service;

import io.rocketbase.commons.config.GravatarConfiguration;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;

@Service
public class GravatarService {

    protected GravatarConfiguration gravatarConfiguration;

    @Autowired
    public GravatarService(GravatarConfiguration gravatarConfiguration) {
        this.gravatarConfiguration = gravatarConfiguration;
    }

    public String getAvatar(String email) {
        if (email == null) {
            return null;
        }
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("https://www.gravatar.com/avatar/");
        uriBuilder.path(md5(email.toLowerCase()));
        uriBuilder.path(".jpg");
        uriBuilder.queryParam("s", gravatarConfiguration.getSize());
        uriBuilder.queryParam("d", gravatarConfiguration.getDefaultImage().getUrlParam());
        if (gravatarConfiguration.getRating() != null) {
            uriBuilder.queryParam("r", gravatarConfiguration.getRating().getUrlParam());
        }
        return uriBuilder.toUriString();
    }

    @SneakyThrows
    private String md5(String text) {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((text == null ? "" : text).getBytes());
        return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
    }

}
