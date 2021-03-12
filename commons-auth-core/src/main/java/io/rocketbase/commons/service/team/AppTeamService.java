package io.rocketbase.commons.service.team;

import io.rocketbase.commons.dto.appteam.AppTeamRead;
import io.rocketbase.commons.dto.appteam.AppUserMembership;
import io.rocketbase.commons.dto.appteam.QueryAppTeam;
import io.rocketbase.commons.model.AppTeamEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AppTeamService {

    Page<AppTeamRead> findAll(QueryAppTeam query, Pageable pageable);

    AppTeamEntity save(AppTeamEntity entity);

    Optional<AppTeamEntity> findById(Long id);

    List<AppTeamEntity> findByIds(Collection<Long> ids);

    /**
     * will delete also their children-tree
     */
    void delete(Long id);

    Set<AppTeamRead> lookupIds(Collection<Long> ids);

    AppUserMembership lookupMembership(Long teamId, String userId);
}
