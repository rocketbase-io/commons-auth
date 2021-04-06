package io.rocketbase.commons.service.team;

import com.google.common.collect.Sets;
import io.rocketbase.commons.converter.AppTeamConverter;
import io.rocketbase.commons.dto.appteam.AppTeamRead;
import io.rocketbase.commons.dto.appteam.AppTeamRole;
import io.rocketbase.commons.dto.appteam.AppTeamWrite;
import io.rocketbase.commons.dto.appteam.QueryAppTeam;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppTeamEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class DefaultAppTeamService implements AppTeamService {

    private final AppTeamPersistenceService<AppTeamEntity> teamPersistenceService;
    private final AppTeamConverter appTeamConverter;

    @Override
    public Page<AppTeamEntity> findAll(QueryAppTeam query, Pageable pageable) {
        return teamPersistenceService.findAll(query, pageable);
    }

    @Override
    public AppTeamEntity create(AppTeamWrite write) {
        AppTeamEntity instance = teamPersistenceService.initNewInstance();
        return applyAndSave(write, instance);
    }
    @Override
    public AppTeamEntity update(Long id, AppTeamWrite write) {
        AppTeamEntity instance = teamPersistenceService.findById(id).orElseThrow(NotFoundException::new);
        return applyAndSave(write, instance);
    }

    protected AppTeamEntity applyAndSave(AppTeamWrite write, AppTeamEntity instance) {
        instance.setName(write.getName());
        instance.setDescription(write.getDescription());
        instance.setPersonal(write.isPersonal());
        instance.setKeyValues(write.getKeyValues());
        instance.setSystemRefId(write.getSystemRefId());
        return teamPersistenceService.save(instance);
    }

    @Override
    public Optional<AppTeamEntity> findById(Long id) {
        return teamPersistenceService.findById(id);
    }

    @Override
    public List<AppTeamEntity> findByIds(Collection<Long> ids) {
        return teamPersistenceService.findAllById(ids);
    }

    @Override
    public void delete(Long id) {
        teamPersistenceService.delete(id);
    }

    @Override
    public Set<AppTeamRead> lookupIds(Collection<Long> ids) {
        List<AppTeamEntity> entities = findByIds(ids);
        return entities != null ? Sets.newHashSet(appTeamConverter.fromEntities(entities)) : null;
    }

    @Override
    public Pair<AppTeamEntity, AppTeamRole> lookupMembership(Long teamId, String userId) {
        if (teamId == null || userId == null) {
            return null;
        }
        Optional<AppTeamEntity> optional = findById(teamId);
        if (optional.isPresent()) {
            AppTeamEntity team = optional.get();
            AppTeamRole role = team.getMembers().getOrDefault(userId, null);
            if (role != null) {
                return Pair.of(team, role);
            }
        }
        return null;
    }
}
