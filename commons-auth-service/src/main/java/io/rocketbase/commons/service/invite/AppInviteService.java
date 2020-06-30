package io.rocketbase.commons.service.invite;

import io.rocketbase.commons.dto.appinvite.ConfirmInviteRequest;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.FeedbackActionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppInviteService extends FeedbackActionService {

    /**
     * creates an invite and sent him a invite email. during confirm process invitor will fill required fields like username, password to a new user.<br>
     * time for verification could be configure AuthProperties.inviteExpiration
     *
     * @throws BadRequestException
     */
    AppInviteEntity createInvite(InviteRequest request, String baseUrl) throws BadRequestException;

    AppInviteEntity verifyInvite(String inviteId) throws VerificationException, NotFoundException;

    /**
     * checks verification-token, set password and possible update firstName, lastName
     *
     * @throws RegistrationException
     * @throws VerificationException
     */
    AppUserEntity confirmInvite(ConfirmInviteRequest request) throws RegistrationException, VerificationException;

    /**
     * delegates query to persistence service<br>
     * so that for all main function {@link AppInviteService} is the main service - by dealing with invites
     */
    Page<AppInviteEntity> findAll(QueryAppInvite query, Pageable pageable);

    void deleteInvite(String inviteId);
}
