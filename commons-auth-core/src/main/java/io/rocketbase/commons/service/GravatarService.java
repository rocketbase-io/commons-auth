package io.rocketbase.commons.service;

import io.rocketbase.commons.config.GravatarProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;

@RequiredArgsConstructor
public class GravatarService {

    final GravatarProperties gravatarProperties;

    public String getAvatar(String email) {
        if (email == null) {
            return null;
        }
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("https://www.gravatar.com/avatar/");
        uriBuilder.path(md5(email.toLowerCase()));
        uriBuilder.path(".jpg");
        uriBuilder.queryParam("s", gravatarProperties.getSize());
        uriBuilder.queryParam("d", gravatarProperties.getImage().getUrlParam());
        if (gravatarProperties.getRating() != null) {
            uriBuilder.queryParam("r", gravatarProperties.getRating().getUrlParam());
        }
        return uriBuilder.toUriString();
    }

    @SneakyThrows
    private String md5(String text) {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((text == null ? "" : text).getBytes());
        return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
    }

    public boolean isEnabled() {
        return gravatarProperties.isEnabled();
    }
}
