package io.rocketbase.commons.controller;

import io.rocketbase.commons.convert.QueryAppClientConverter;
import io.rocketbase.commons.converter.AppClientConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appclient.AppClientRead;
import io.rocketbase.commons.dto.appclient.AppClientWrite;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppClientEntity;
import io.rocketbase.commons.service.client.AppClientService;
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
public class AppClientController implements BaseController {

    @Resource
    private AppClientService service;

    @Resource
    private AppClientConverter converter;

    private final QueryAppClientConverter queryConverter = new QueryAppClientConverter();

    @RequestMapping(method = RequestMethod.GET, path = "/api/client")
    @ResponseBody
    public PageableResult<AppClientRead> find(@RequestParam(required = false) MultiValueMap<String, String> params) {
        Page<AppClientEntity> entities = service.findAll(queryConverter.fromParams(params),
                parsePageRequest(params, Sort.by("id")));

        return PageableResult.contentPage(converter.fromEntities(entities.getContent()), entities);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/api/client/{id}")
    @ResponseBody
    public AppClientRead findById(@PathVariable("id") Long id) {
        return converter.fromEntity(service.findById(id).orElseThrow(NotFoundException::new));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/api/client")
    @ResponseBody
    public AppClientRead create(@RequestBody @NotNull @Validated AppClientWrite write) {
        AppClientEntity entity = service.create(write);
        return converter.fromEntity(entity);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/api/client/{id}")
    @ResponseBody
    public AppClientRead update(@PathVariable("id") Long id, @RequestBody @NotNull @Validated AppClientWrite write) {
        AppClientEntity entity = service.update(id, write);
        return converter.fromEntity(entity);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/api/client/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }

}
