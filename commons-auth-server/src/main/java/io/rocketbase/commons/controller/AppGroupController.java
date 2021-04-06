package io.rocketbase.commons.controller;

import io.rocketbase.commons.convert.QueryAppGroupConverter;
import io.rocketbase.commons.converter.AppGroupConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appgroup.AppGroupWrite;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppGroupEntity;
import io.rocketbase.commons.service.group.AppGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("${auth.prefix:}")
public class AppGroupController implements BaseController {

    @Resource
    private AppGroupService service;

    @Resource
    private AppGroupConverter converter;

    private final QueryAppGroupConverter queryConverter = new QueryAppGroupConverter();

    @RequestMapping(method = RequestMethod.GET, path = "/api/group")
    @ResponseBody
    public PageableResult<AppGroupRead> find(@RequestParam(required = false) MultiValueMap<String, String> params) {
        Page<AppGroupEntity> entities = service.findAll(queryConverter.fromParams(params),
                parsePageRequest(params, Sort.by("id")));

        return PageableResult.contentPage(converter.fromEntities(entities.getContent()), entities);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/api/group/{id}")
    @ResponseBody
    public AppGroupRead findById(@PathVariable("id") Long id) {
        return converter.fromEntity(service.findById(id).orElseThrow(NotFoundException::new));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/api/group/{parentId}")
    @ResponseBody
    public AppGroupRead create(@PathVariable("parentId") Long parentId, @RequestBody @NotNull @Validated AppGroupWrite write) {
        AppGroupEntity entity = service.create(write, parentId);
        return converter.fromEntity(entity);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/api/group/{id}")
    @ResponseBody
    public AppGroupRead update(@PathVariable("id") Long id, @RequestBody @NotNull @Validated AppGroupWrite write) {
        AppGroupEntity entity = service.update(id, write);
        return converter.fromEntity(entity);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/api/group/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }

}
