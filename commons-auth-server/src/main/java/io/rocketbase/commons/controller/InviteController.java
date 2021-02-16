package io.rocketbase.commons.controller;

import io.rocketbase.commons.converter.AppInviteConverter;
import io.rocketbase.commons.converter.KeyValuesConverter;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.ConfirmInviteRequest;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.service.invite.AppInviteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@ConditionalOnExpression(value = "${auth.invite.enabled:true}")
@RequestMapping("${auth.prefix:}")
public class InviteController implements BaseController {

    @Resource
    private AppInviteService appInviteService;

    @Resource
    private AppInviteConverter appInviteConverter;

    @Resource
    private KeyValuesConverter keyValuesConverter;

    @RequestMapping(method = RequestMethod.GET, path = "/auth/invite")
    @ResponseBody
    public ResponseEntity<AppInviteRead> verify(@RequestParam("inviteId") Long inviteId) {
        AppInviteEntity entity = appInviteService.verifyInvite(inviteId);
        return ResponseEntity.ok(appInviteConverter.fromEntity(entity));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/auth/invite", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<AppUserRead> transformToUser(@RequestBody @NotNull @Validated ConfirmInviteRequest confirmInviteRequest) {
        AppUserEntity entity = appInviteService.confirmInvite(confirmInviteRequest);
        return ResponseEntity.ok(keyValuesConverter.fromEntity(entity));
    }

}
