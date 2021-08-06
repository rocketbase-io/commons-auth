package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserReference;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserSearchApi {

    PageableResult<AppUserReference> search(QueryAppUser query, Pageable pageable);

    Optional<AppUserReference> findByUsernameOrId(String usernameOrId);
}
