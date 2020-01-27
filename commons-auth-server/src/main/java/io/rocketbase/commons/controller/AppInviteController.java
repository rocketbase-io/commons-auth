package io.rocketbase.commons.controller;

import io.rocketbase.commons.convert.QueryAppInviteConverter;
import io.rocketbase.commons.converter.AppInviteConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.service.AppInvitePersistenceService;
import io.rocketbase.commons.service.invite.InviteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@ConditionalOnExpression(value = "${auth.invite.enabled:true}")
@RequestMapping("${auth.prefix:}")
public class AppInviteController implements BaseController {

    @Resource
    private AppInvitePersistenceService appInvitePersistenceService;

    @Resource
    private InviteUserService inviteUserService;

    @Resource
    private AppInviteConverter appInviteConverter;

    private QueryAppInviteConverter queryAppInviteConverter = new QueryAppInviteConverter();


    @RequestMapping(method = RequestMethod.GET, path = "/api/invite")
    @ResponseBody
    public PageableResult<AppInviteRead> find(@RequestParam(required = false) MultiValueMap<String, String> params) {
        Page<AppInviteEntity> entities = appInvitePersistenceService.findAll(queryAppInviteConverter.fromParams(params),
                parsePageRequest(params, Sort.by("email")));

        return PageableResult.contentPage(appInviteConverter.fromEntities(entities.getContent()), entities);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/auth/invite", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<AppInviteRead> invite(HttpServletRequest request, @RequestBody @NotNull @Validated InviteRequest inviteRequest) {
        AppInviteEntity entity = inviteUserService.createInvite(inviteRequest, getBaseUrl(request));
        return ResponseEntity.ok(appInviteConverter.fromEntity(entity));
    }

}
