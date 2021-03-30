package io.rocketbase.commons.service.team;

import io.rocketbase.commons.dto.appteam.AppTeamRead;
import io.rocketbase.commons.dto.appteam.AppTeamRole;
import io.rocketbase.commons.dto.appteam.AppTeamWrite;
import io.rocketbase.commons.dto.appteam.QueryAppTeam;
import io.rocketbase.commons.model.AppTeamEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AppTeamService {

    Page<AppTeamEntity> findAll(QueryAppTeam query, Pageable pageable);

    AppTeamEntity create(AppTeamWrite write);

    AppTeamEntity update(Long id, AppTeamWrite write);

    Optional<AppTeamEntity> findById(Long id);

    List<AppTeamEntity> findByIds(Collection<Long> ids);

    /**
     * will delete also their children-tree
     */
    void delete(Long id);

    Set<AppTeamRead> lookupIds(Collection<Long> ids);

    Pair<AppTeamEntity, AppTeamRole> lookupMembership(Long teamId, String userId);
}
