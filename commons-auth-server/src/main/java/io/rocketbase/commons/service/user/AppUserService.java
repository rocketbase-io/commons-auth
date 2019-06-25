package io.rocketbase.commons.service.user;

import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.exception.EmailValidationException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.ValidationUserLookupService;
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

    /**
     * updates AppUsers property lastLogin
     *
     * @param username unique username
     * @return
     */
    AppUserEntity updateLastLogin(String username);

    /**
     * update users password and invalidates already generated tokens
     *
     * @param username
     * @param newPassword password is not been checked by validations (this needs to be done separately)
     * @return
     */
    AppUserEntity updatePassword(String username, String newPassword);

    /**
     * update user profile settings
     *
     * @param username
     * @param firstName
     * @param lastName
     * @param avatar
     * @param keyValues
     */
    AppUserEntity updateProfile(String username, String firstName, String lastName, String avatar, Map<String, String> keyValues);

    /**
     * update keyValues and persist settings
     *
     * @param username
     * @param keyValues
     * @return
     */
    AppUserEntity updateKeyValues(String username, Map<String, String> keyValues);

    /**
     * refresh user's cache
     *
     * @param username
     * @return current live version
     */
    AppUserEntity refreshUsername(String username);

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
     * @param username will get verified
     * @param password will not get verified
     * @param email    will get verified
     * @param roles    individually specify user's roles
     * @return initialized user
     */
    AppUserEntity initializeUser(String username, String password, String email, List<String> roles) throws UsernameNotFoundException, EmailValidationException;

    /**
     * replace user's roles by given list<br>
     * already created tokens get invalidated
     *
     * @param username
     * @param roles
     * @return
     */
    AppUserEntity updateRoles(String username, List<String> roles);

    /**
     * perform a registration of user
     *
     * @param registration
     * @return
     */
    AppUserEntity registerUser(RegistrationRequest registration);

    /**
     * only update's the keyValues add a users<br>
     * doesn't persits changes!
     *
     * @param user
     * @param keyValues
     */
    void handleKeyValues(AppUserEntity user, Map<String, String> keyValues);

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

}
