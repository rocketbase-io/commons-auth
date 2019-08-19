package io.rocketbase.commons.handler;

import io.rocketbase.commons.filter.LoginCookieFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LogoutCookieHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (WebUtils.getCookie(request, LoginSuccessCookieHandler.AUTH_REMEMBER) != null) {
            LoginCookieFilter.removeAuthCookie(response);
            log.debug("removed authRemeber cookie after logout");
        }
    }

}
