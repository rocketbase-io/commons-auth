package io.rocketbase.commons.controller;

import io.rocketbase.commons.convert.QueryAppUserConverter;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.appuser.AppUserResetPassword;
import io.rocketbase.commons.dto.appuser.AppUserUpdate;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("${auth.prefix:}")
public class AppUserController implements BaseController {

    private final QueryAppUserConverter queryConverter = new QueryAppUserConverter();

    @Resource
    private AppUserService appUserService;

    @Resource
    private AppUserConverter appUserConverter;

    @Resource
    private ValidationService validationService;

    @RequestMapping(method = RequestMethod.GET, path = "/api/user")
    @ResponseBody
    public PageableResult<AppUserRead> find(@RequestParam(required = false) MultiValueMap<String, String> params) {
        Page<AppUserEntity> entities = appUserService.findAll(queryConverter.fromParams(params),
                parsePageRequest(params, Sort.by("username")));

        return PageableResult.contentPage(appUserConverter.toRead(entities.getContent()), entities);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/api/user", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppUserRead create(@RequestBody @NotNull @Validated AppUserCreate create) {
        validationService.registrationIsValid(create.getUsername(), create.getPassword(), create.getEmail());

        AppUserEntity entity = appUserService.initializeUser(create);
        return appUserConverter.toRead(entity);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/api/user/{id}/password", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppUserRead restPassword(@PathVariable("id") String id,  @RequestBody @NotNull @Validated AppUserResetPassword reset) {
        validationService.passwordIsValid("resetPassword", reset.getResetPassword());

        AppUserEntity entity = appUserService.updatePasswordUnchecked(id, reset.getResetPassword());
        return appUserConverter.toRead(entity);
    }

    @RequestMapping(method = RequestMethod.PATCH, path = "/api/user/{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppUserRead patch(@PathVariable("id") String id, @RequestBody @NotNull @Validated AppUserUpdate update) {
        AppUserEntity entity = appUserService.patch(id, update);
        return appUserConverter.toRead(entity);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/api/user/{id}")
    public void delete(@PathVariable("id") String id) {
        appUserService.delete(id);
    }


    private AppUserEntity getById(String id) {
        Optional<AppUserEntity> optionalAppUser = appUserService.findById(id);
        if (!optionalAppUser.isPresent()) {
            throw new NotFoundException();
        }
        return optionalAppUser.get();
    }

}
