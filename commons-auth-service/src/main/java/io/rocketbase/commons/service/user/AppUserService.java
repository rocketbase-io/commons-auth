package io.rocketbase.commons.service.user;

import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.dto.appuser.AppUserUpdate;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.authentication.UpdateProfileRequest;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.exception.EmailValidationException;
import io.rocketbase.commons.exception.PasswordValidationException;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.commons.service.ValidationUserLookupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AppUserService extends UserDetailsService, ValidationUserLookupService {

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
     * @param update       only not null/empty values will get updated
     * @return
     */
    AppUserEntity patch(String usernameOrId, AppUserUpdate update);

    /**
     * update user profile settings
     *
     * @param usernameOrId
     * @param updateProfile
     */
    AppUserEntity updateProfile(String usernameOrId, UpdateProfileRequest updateProfile);

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
     * same as initializeUser but checks before if user already not exists
     *
     * @param username
     * @param password
     * @param email
     * @param admin
     * @return
     */
    AppUserEntity initializeUserIfNotExists(String username, String password, String email, boolean admin);

    /**
     * @param username will get verified
     * @param password will not get verified
     * @param email    will get verified
     * @param admin    should user get created as admin or normal user
     * @return initialized user
     */
    AppUserEntity initializeUser(String username, String password, String email, boolean admin) throws UsernameNotFoundException, EmailValidationException;

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
     * allows to enable/disable an user
     */
    AppUserEntity updateEnabled(String username, boolean enabled);

    /**
     * replace user's roles by given list<br>
     * already created tokens get invalidated
     *
     * @param usernameOrId
     * @param roles
     * @return
     */
    AppUserEntity updateRoles(String usernameOrId, List<String> roles);

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

    /**
     * delete a user from database
     *
     * @param user
     */
    void delete(AppUserEntity user);

    /**
     * delegates query to persistence service<br>
     * so that for all main function {@link AppUserService} is the main service - by dealing with users
     */
    Page<AppUserEntity> findAll(QueryAppUser query, Pageable pageable);

    default boolean shouldPatch(String value) {
        return value != null && !value.trim().isEmpty();
    }

}
