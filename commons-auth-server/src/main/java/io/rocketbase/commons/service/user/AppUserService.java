package io.rocketbase.commons.service.user;

import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.exception.EmailValidationException;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.service.ValidationUserLookupService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;
import java.util.Optional;

public interface AppUserService extends UserDetailsService, ValidationUserLookupService {

    AppUser getByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findById(String id);

    AppUser updateLastLogin(String username);

    void updatePassword(String username, String newPassword);

    void updateProfile(String username, String firstName, String lastName, String avatar, Map<String, String> keyValues);

    void updateKeyValues(String username, Map<String, String> keyValues);

    void refreshUsername(String username);

    AppUser initializeUserIfNotExists(String username, String password, String email, boolean admin);

    AppUser initializeUser(String username, String password, String email, boolean admin) throws UsernameNotFoundException, EmailValidationException;

    AppUser registerUser(RegistrationRequest registration);

    void handleKeyValues(AppUser user, Map<String, String> keyValues);

    void processRegistrationVerification(String username);

    void delete(AppUser user);

}
