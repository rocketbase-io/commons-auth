package io.rocketbase.commons.controller;

import io.rocketbase.commons.convert.QueryAppUserConverter;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.appuser.AppUserUpdate;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.AppUserPersistenceService;
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

    @Resource
    private AppUserPersistenceService appUserPersistenceService;

    @Resource
    private AppUserService appUserService;

    @Resource
    private AppUserConverter appUserConverter;

    @Resource
    private ValidationService validationService;

    private QueryAppUserConverter queryConverter = new QueryAppUserConverter();

    @RequestMapping(method = RequestMethod.GET, path = "/api/user")
    @ResponseBody
    public PageableResult<AppUserRead> find(@RequestParam(required = false) MultiValueMap<String, String> params) {
        Page<AppUserEntity> entities = appUserPersistenceService.findAll(queryConverter.fromParams(params),
                parsePageRequest(params, Sort.by("username")));

        return PageableResult.contentPage(appUserConverter.fromEntities(entities.getContent()), entities);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/api/user", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppUserRead create(@RequestBody @NotNull @Validated AppUserCreate create) {
        validationService.registrationIsValid(create.getUsername(), create.getPassword(), create.getEmail());

        AppUserEntity entity = appUserService.initializeUser(create.getUsername(), create.getPassword(), create.getEmail(), create.getAdmin());
        if (create.getFirstName() != null || create.getLastName() != null || create.getAvatar() != null || create.getKeyValues() != null) {
            String avatar = create.getAvatar() != null ? create.getAvatar() : entity.getAvatar();
            appUserService.updateProfile(entity.getUsername(), create.getFirstName(), create.getLastName(), avatar, create.getKeyValues());
        }
        return appUserConverter.fromEntity(entity);
    }

    @RequestMapping(method = RequestMethod.PATCH, path = "/api/user/{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppUserRead patch(@PathVariable("id") String id, @RequestBody @NotNull @Validated AppUserUpdate update) {
        AppUserEntity entity = getById(id);
        if (shouldPatch(update.getFirstName())) {
            entity.setFirstName(update.getFirstName());
        }
        if (shouldPatch(update.getLastName())) {
            entity.setLastName(update.getLastName());
        }
        if (update.getRoles() != null && !update.getRoles().isEmpty()) {
            entity.setRoles(update.getRoles());
        }
        if (update.getEnabled() != null) {
            entity.setEnabled(update.getEnabled());
        }
        appUserService.handleKeyValues(entity, update.getKeyValues());

        appUserPersistenceService.save(entity);

        if (shouldPatch(update.getPassword())) {
            validationService.passwordIsValid(update.getPassword());
            appUserService.updatePassword(entity.getUsername(), update.getPassword());
        }

        appUserService.refreshUsername(entity.getUsername());

        return appUserConverter.fromEntity(entity);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/api/user/{id}")
    public void delete(@PathVariable("id") String id) {
        AppUserEntity entity = getById(id);
        appUserPersistenceService.delete(entity);
        appUserService.refreshUsername(entity.getUsername());
    }


    private AppUserEntity getById(String id) {
        Optional<AppUserEntity> optionalAppUser = appUserPersistenceService.findById(id);
        if (!optionalAppUser.isPresent()) {
            throw new NotFoundException();
        }
        return optionalAppUser.get();
    }

    private boolean shouldPatch(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
