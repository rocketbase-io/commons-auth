package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;

public interface ImpersonateApi {

    JwtTokenBundle impersonate(String userIdOrUsername);

}
