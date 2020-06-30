package io.rocketbase.commons.api;

import io.rocketbase.commons.util.Nulls;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

public interface BaseServiceApi {

    default String getBaseUrl() {
        try {
            ServletUriComponentsBuilder uriComponentsBuilder = ServletUriComponentsBuilder.fromCurrentContextPath();
            // in some cases uriComponentsBuilder will ignore ssl (checks for X-Forwarded-Ssl: on) ignore this behaviour...
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String scheme = Nulls.notNull(request.getHeader("x-forwarded-proto"), request.getHeader("x-scheme"));
            if ("https".equalsIgnoreCase(scheme)) {
                uriComponentsBuilder.scheme(scheme);
            }
            return uriComponentsBuilder.toUriString();
        } catch (Exception e) {
        }
        return null;
    }

}
