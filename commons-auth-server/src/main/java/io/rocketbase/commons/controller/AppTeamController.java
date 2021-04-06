package io.rocketbase.commons.controller;

import io.rocketbase.commons.convert.QueryAppTeamConverter;
import io.rocketbase.commons.converter.AppTeamConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appteam.AppTeamRead;
import io.rocketbase.commons.dto.appteam.AppTeamWrite;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppTeamEntity;
import io.rocketbase.commons.service.team.AppTeamService;
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
public class AppTeamController implements BaseController {

    @Resource
    private AppTeamService service;

    @Resource
    private AppTeamConverter converter;

    private final QueryAppTeamConverter queryConverter = new QueryAppTeamConverter();

    @RequestMapping(method = RequestMethod.GET, path = "/api/team")
    @ResponseBody
    public PageableResult<AppTeamRead> find(@RequestParam(required = false) MultiValueMap<String, String> params) {
        Page<AppTeamEntity> entities = service.findAll(queryConverter.fromParams(params),
                parsePageRequest(params, Sort.by("id")));

        return PageableResult.contentPage(converter.fromEntities(entities.getContent()), entities);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/api/team/{id}")
    @ResponseBody
    public AppTeamRead findById(@PathVariable("id") Long id) {
        return converter.fromEntity(service.findById(id).orElseThrow(NotFoundException::new));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/api/team")
    @ResponseBody
    public AppTeamRead create(@RequestBody @NotNull @Validated AppTeamWrite write) {
        AppTeamEntity entity = service.create(write);
        return converter.fromEntity(entity);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/api/team/{id}")
    @ResponseBody
    public AppTeamRead update(@PathVariable("id") Long id, @RequestBody @NotNull @Validated AppTeamWrite write) {
        AppTeamEntity entity = service.update(id, write);
        return converter.fromEntity(entity);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/api/team/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }

}
