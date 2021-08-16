package io.rocketbase.commons.service.token;

import io.rocketbase.commons.model.AuthorizationCodeJpaEntity;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.Optional;

@Transactional
public class AuthorizationCodeJpaService implements AuthorizationCodeService {

    private final EntityManager em;
    private final SimpleJpaRepository<AuthorizationCodeJpaEntity, String> repository;

    public AuthorizationCodeJpaService(EntityManager entityManager) {
        this.em = entityManager;
        this.repository = new SimpleJpaRepository<>(AuthorizationCodeJpaEntity.class, entityManager);
    }

    @Override
    public Optional<AuthorizationCode> findByCode(String code) {
        Optional<AuthorizationCodeJpaEntity> entityOptional = repository.findById(code);
        return entityOptional.isPresent() ? Optional.of(entityOptional.get().toDto()) : Optional.empty();
    }

    @Override
    public AuthorizationCode save(AuthorizationCode code) {
        AuthorizationCodeJpaEntity entity = repository.save(new AuthorizationCodeJpaEntity(code));
        return entity.toDto();
    }

    @Override
    public void delete(String code) {
        repository.deleteById(code);
    }

    @Override
    public void deleteByUserId(String userId) {
        em.createNativeQuery("delete from co_authcode where user_id = ?").setParameter(1, userId).executeUpdate();
    }

    @Override
    public void deleteInvalid() {
        em.createNativeQuery("delete from co_authcode where invalid < ?").setParameter(1, Instant.now()).executeUpdate();
    }
}
