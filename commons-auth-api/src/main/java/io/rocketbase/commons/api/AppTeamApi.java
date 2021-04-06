package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appteam.AppTeamRead;
import io.rocketbase.commons.dto.appteam.AppTeamWrite;
import io.rocketbase.commons.dto.appteam.QueryAppTeam;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AppTeamApi {

    default PageableResult<AppTeamRead> find(Pageable pageable) {
        return find(null, pageable);
    }

    PageableResult<AppTeamRead> find(QueryAppTeam query, Pageable pageable);

    Optional<AppTeamRead> findById(Long id);

    AppTeamRead create(AppTeamWrite write);

    AppTeamRead update(Long id, AppTeamWrite write);

    void delete(Long id);
}
