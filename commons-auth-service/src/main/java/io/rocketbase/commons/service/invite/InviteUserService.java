package io.rocketbase.commons.service.invite;

import io.rocketbase.commons.dto.appinvite.ConfirmInviteRequest;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.exception.EmailValidationException;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.FeedbackActionService;

public interface InviteUserService extends FeedbackActionService {

    /**
     * creates an invite and sent him a invite email. during confirm process invitor will fill required fields like username, password to a new user.<br>
     * time for verification could be configure AuthProperties.inviteExpiration
     *
     * @throws EmailValidationException
     * @throws BadRequestException
     */
    AppInviteEntity createInvite(InviteRequest request, String baseUrl) throws EmailValidationException, BadRequestException;

    AppInviteEntity verifyInvite(String inviteId) throws VerificationException;

    /**
     * checks verification-token, set password and possible update firstName, lastName
     *
     * @throws RegistrationException
     * @throws VerificationException
     */
    AppUserEntity confirmInvite(String inviteId, ConfirmInviteRequest request) throws RegistrationException, VerificationException;
}
