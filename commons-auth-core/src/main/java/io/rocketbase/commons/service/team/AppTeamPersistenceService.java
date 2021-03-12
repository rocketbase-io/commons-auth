package io.rocketbase.commons.service.team;

import io.rocketbase.commons.dto.appteam.QueryAppTeam;
import io.rocketbase.commons.model.AppTeamEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AppTeamPersistenceService<E extends AppTeamEntity> {

    Optional<E> findById(Long id);

    List<E> findAllById(Iterable<Long> ids);

    Page<E> findAll(QueryAppTeam query, Pageable pageable);

    E save(E entity);

    void delete(Long id);

    E initNewInstance();
}
