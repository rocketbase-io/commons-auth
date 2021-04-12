package io.rocketbase.commons.service.user;

import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.dto.appuser.AppUserUpdate;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.exception.EmailValidationException;
import io.rocketbase.commons.exception.PasswordValidationException;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.exception.UsernameValidationException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;
import java.util.Optional;

/**
 * persistence layer for AppUserEntity<br>
 * on security level AppUserToken is been used that combines capabilities and keyValues from group and team
 */
public interface AppUserService {

    /**
     * @param username
     * @return null in case not found
     */
    AppUserEntity getByUsername(String username);

    /**
     * @param email find by mail-address
     * @return
     */
    Optional<AppUserEntity> findByEmail(String email);

    /**
     * @param id find by it's unique id
     * @return
     */
    Optional<AppUserEntity> findById(String id);

    default Optional<AppUserEntity> findByIdOrUsername(String idOrUsername) {
        Optional<AppUserEntity> optional = findById(idOrUsername);
        if (!optional.isPresent()) {
            optional = Optional.ofNullable(getByUsername(idOrUsername));
        }
        return optional;
    }

    /**
     * updates AppUsers property lastLogin
     *
     * @param usernameOrId unique username
     * @return
     */
    AppUserEntity updateLastLogin(String usernameOrId);

    /**
     * validate password and perform update - invalidates already generated tokens
     *
     * @param usernameOrId
     * @param passwordChangeRequest current password get checked will + new will get validated validated via validationService
     * @return
     */
    AppUserEntity performUpdatePassword(String usernameOrId, PasswordChangeRequest passwordChangeRequest) throws PasswordValidationException;

    /**
     * update users password and invalidates already generated tokens
     *
     * @param usernameOrId
     * @param newPassword  password is not been checked by validations (this needs to be done separately)
     * @return
     */
    AppUserEntity updatePasswordUnchecked(String usernameOrId, String newPassword);


    /**
     * allow to update values
     *
     * @param usernameOrId entity to lookup
     * @param update       update entity with valzes
     * @return
     */
    AppUserEntity save(String usernameOrId, AppUserUpdate update);

    /**
     * allow to path values
     *
     * @param usernameOrId entity to lookup
     * @param update       only not null/empty values will get updated
     * @return
     */
    AppUserEntity patch(String usernameOrId, AppUserUpdate update);

    /**
     * update keyValues and persist settings
     *
     * @param usernameOrId
     * @param keyValues
     * @return
     */
    AppUserEntity updateKeyValues(String usernameOrId, Map<String, String> keyValues);

    /**
     * invalidate user's cache
     *
     * @param appUser entity that implements AppUserReference
     */
    void invalidateCache(AppUserReference appUser);

    /**
     * @param userCreate <ul>
     *                   <li>username will get verified</li>
     *                   <li>password will not get verified</li>
     *                   <li>email will get verified</li>
     *                   </ul>
     * @return initialized user
     */
    AppUserEntity initializeUser(AppUserCreate userCreate) throws UsernameNotFoundException, EmailValidationException;

    /**
     * perform a registration of user
     *
     * @param registration
     * @return
     */
    AppUserEntity registerUser(RegistrationRequest registration) throws RegistrationException;

    /**
     * handle registration verification
     *
     * @param username
     */
    void processRegistrationVerification(String username);

    AppUserEntity changeUsername(String usernameOrId, String newUsername) throws UsernameValidationException;

    AppUserEntity changeEmail(String usernameOrId, String newEmail) throws EmailValidationException;

    /**
     * delete a user from database
     *
     * @param usernameOrId
     */
    void delete(String usernameOrId);

    /**
     * delegates query to persistence service<br>
     * so that for all main function {@link AppUserService} is the main service - by dealing with users
     */
    Page<AppUserEntity> findAll(QueryAppUser query, Pageable pageable);

}
