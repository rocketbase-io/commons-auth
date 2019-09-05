package io.rocketbase.commons.controller;

import io.rocketbase.commons.convert.QueryAppUserConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.commons.service.AppUserPersistenceService;
import io.rocketbase.commons.service.user.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.stream.Collectors;

@Slf4j
@RestController
@ConditionalOnExpression(value = "${auth.search.enabled:true}")
@RequestMapping("${auth.prefix:}")
public class UserSearchController implements BaseController {

    @Resource
    private AppUserPersistenceService<AppUserEntity> appUserPersistenceService;

    @Resource
    private AppUserService appUserService;

    private QueryAppUserConverter queryConverter = new QueryAppUserConverter();

    @RequestMapping(method = RequestMethod.GET, path = "/api/user-search")
    @ResponseBody
    public PageableResult<AppUserReference> search(@RequestParam(required = false) MultiValueMap<String, String> params) {
        Page<AppUserEntity> entities = appUserPersistenceService.findAll(queryConverter.fromParams(params),
                parsePageRequest(params, parseSort(params, "sort", Sort.by("username"))));

        return PageableResult.contentPage(entities.stream().map(AppUserEntity::toReference).collect(Collectors.toList()), entities);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/api/user-search/{usernameOrId}")
    @ResponseBody
    public AppUserReference findByUsernameOrId(@PathVariable("usernameOrId") String usernameOrId) throws Throwable {
        AppUserEntity byUsername = appUserService.getByUsername(usernameOrId);
        if (byUsername != null) {
            return byUsername.toReference();
        }

        return appUserPersistenceService.findById(usernameOrId).orElseThrow(() -> new NotFoundException()).toReference();
    }
}
