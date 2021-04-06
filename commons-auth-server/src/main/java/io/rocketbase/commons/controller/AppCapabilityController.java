package io.rocketbase.commons.controller;

import io.rocketbase.commons.convert.QueryAppCapabilityConverter;
import io.rocketbase.commons.converter.AppCapabilityConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appcapability.AppCapabilityWrite;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppCapabilityEntity;
import io.rocketbase.commons.service.capability.AppCapabilityService;
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
public class AppCapabilityController implements BaseController {

    @Resource
    private AppCapabilityService service;

    @Resource
    private AppCapabilityConverter converter;

    private final QueryAppCapabilityConverter queryConverter = new QueryAppCapabilityConverter();

    @RequestMapping(method = RequestMethod.GET, path = "/api/capability")
    @ResponseBody
    public PageableResult<AppCapabilityRead> find(@RequestParam(required = false) MultiValueMap<String, String> params) {
        Page<AppCapabilityEntity> entities = service.findAll(queryConverter.fromParams(params),
                parsePageRequest(params, Sort.by("id")));

        return PageableResult.contentPage(converter.fromEntities(entities.getContent()), entities);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/api/capability/{id}")
    @ResponseBody
    public AppCapabilityRead findById(@PathVariable("id") Long id) {
        return converter.fromEntity(service.findById(id).orElseThrow(NotFoundException::new));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/api/capability/{parentId}")
    @ResponseBody
    public AppCapabilityRead create(@PathVariable("parentId") Long parentId, @RequestBody @NotNull @Validated AppCapabilityWrite write) {
        AppCapabilityEntity entity = service.create(write, parentId);
        return converter.fromEntity(entity);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/api/capability/{id}")
    @ResponseBody
    public AppCapabilityRead update(@PathVariable("id") Long id, @RequestBody @NotNull @Validated AppCapabilityWrite write) {
        AppCapabilityEntity entity = service.update(id, write);
        return converter.fromEntity(entity);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/api/capability/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }

}
