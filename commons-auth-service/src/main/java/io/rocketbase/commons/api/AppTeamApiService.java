package io.rocketbase.commons.api;

import io.rocketbase.commons.converter.AppTeamConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appteam.AppTeamRead;
import io.rocketbase.commons.dto.appteam.AppTeamWrite;
import io.rocketbase.commons.dto.appteam.QueryAppTeam;
import io.rocketbase.commons.model.AppTeamEntity;
import io.rocketbase.commons.service.team.AppTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@RequiredArgsConstructor
public class AppTeamApiService implements AppTeamApi, BaseApiService {

    private final AppTeamService service;
    private final AppTeamConverter converter;

    @Override
    public PageableResult<AppTeamRead> find(QueryAppTeam query, Pageable pageable) {
        Page<AppTeamEntity> page = service.findAll(query, pageable);
        return PageableResult.contentPage(converter.fromEntities(page.getContent()), page);
    }

    @Override
    public Optional<AppTeamRead> findById(Long id) {
        Optional<AppTeamEntity> optional = service.findById(id);
        return optional.isPresent() ? Optional.of(converter.fromEntity(optional.get())) : Optional.empty();
    }

    @Override
    public AppTeamRead create(AppTeamWrite write) {
        AppTeamEntity entity = service.create(write);
        return converter.fromEntity(entity);
    }

    @Override
    public AppTeamRead update(Long id, AppTeamWrite write) {
        AppTeamEntity entity = service.update(id, write);
        return converter.fromEntity(entity);
    }

    @Override
    public void delete(Long id) {
        service.delete(id);
    }
}
