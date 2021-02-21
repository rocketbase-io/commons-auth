package io.rocketbase.commons.controller;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.security.CommonsPrincipal;
import io.rocketbase.commons.service.impersonate.ImpersonateService;
import io.rocketbase.commons.service.user.ActiveUserStore;
import io.rocketbase.commons.service.user.AppUserTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@ConditionalOnExpression(value = "${auth.impersonate.enabled:true}")
@RequestMapping("${auth.prefix:}")
public class ImpersonateController implements BaseController {

    @Resource
    private AppUserTokenService appUserTokenService;

    @Resource
    private ImpersonateService impersonateService;

    @Resource
    private ActiveUserStore activeUserStore;

    @RequestMapping(method = RequestMethod.GET, path = "/api/impersonate/{userIdOrUsername}")
    @ResponseBody
    public ResponseEntity<JwtTokenBundle> impersonate(@PathVariable("userIdOrUsername") String userIdOrUsername) {
        AppUserToken impersonateToken = appUserTokenService.findByIdOrUsername(userIdOrUsername).orElseThrow(NotFoundException::new);
        activeUserStore.addUser(impersonateToken);

        return ResponseEntity.ok(impersonateService.getImpersonateBundle(CommonsPrincipal.getCurrent(), impersonateToken));
    }

}
