package io.rocketbase.commons.service.impersonate;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.AppUserToken;

public interface ImpersonateService {

    JwtTokenBundle getImpersonateBundle(AppUserToken requestedBy, AppUserToken impersonateAs);

}
