package io.rocketbase.commons.test;

import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.service.AppInvitePersistenceService;
import io.rocketbase.commons.test.model.AppInviteTestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;

public class AppInvitePersistenceTestService implements AppInvitePersistenceService<AppInviteTestEntity> {

    private Map<String, AppInviteTestEntity> inviteMap = new HashMap<>();

    private AppInviteTestEntity buildAppInvite(String email, String role) {
        return AppInviteTestEntity.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .roles(Arrays.asList(role))
                .created(Instant.now())
                .expiration(Instant.now().plusSeconds(60 * 60))
                .build();
    }

    @PostConstruct
    public void init() {
        resetData();
    }

    public void resetData() {
        inviteMap.clear();

        AppInviteTestEntity sampleInvite = buildAppInvite("sample@rocketbase.io", "USER");
        inviteMap.put(sampleInvite.getId(), sampleInvite);
    }

    @Override
    public Page<AppInviteTestEntity> findAll(QueryAppInvite query, Pageable pageable) {
        return new PageImpl(new ArrayList(inviteMap.values()), pageable, inviteMap.size());
    }

    @Override
    public AppInviteTestEntity save(AppInviteTestEntity entity) {
        inviteMap.put(entity.getId(), entity);
        return inviteMap.get(entity.getId());
    }

    @Override
    public Optional<AppInviteTestEntity> findById(String id) {
        for (AppInviteTestEntity invite : inviteMap.values()) {
            if (id.equals(invite.getId())) {
                return Optional.of(invite.clone());
            }
        }
        return Optional.empty();
    }

    @Override
    public long count() {
        return inviteMap.size();
    }

    @Override
    public void delete(AppInviteTestEntity entity) {
        inviteMap.remove(entity.getId());
    }

    @Override
    public void deleteAll() {
    }

    @Override
    public AppInviteTestEntity initNewInstance() {
        return AppInviteTestEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .roles(new ArrayList<>())
                .build();
    }

    @Override
    public long deleteExpired() {
        return 0;
    }
}
