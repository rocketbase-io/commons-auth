package io.rocketbase.commons.service.token;

import java.util.Optional;

public interface AuthorizationCodeService {

    Optional<AuthorizationCode> findByCode(String code);

    AuthorizationCode save(AuthorizationCode code);

    void delete(String code);

    void deleteByUserId(String userId);

    void deleteInvalid();
}
