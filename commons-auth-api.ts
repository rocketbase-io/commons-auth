// Generated using typescript-generator version 2.27.744 on 2020-11-20 13:23:53.

import * as rest from "./commons-rest-api.d.ts";

export interface AppInviteRead extends rest.HasKeyValue, rest.HasFirstAndLastName {
    id: string;
    invitor: string;
    message: string;
    email: string;
    roles: string[];
    created: string;
    expiration: string;
}

export interface ConfirmInviteRequest extends rest.HasFirstAndLastName {
    inviteId: string;
    username: string;
    email: string;
    password: string;
}

export interface InviteRequest extends rest.HasFirstAndLastName, rest.HasKeyValue {
    invitor: string;
    message?: string;
    email: string;
    roles: string[];
    inviteUrl?: string;
}

/**
 * query object to find invites
 * string properties mean search like ignore cases
 */
export interface QueryAppInvite extends rest.HasKeyValue {
    invitor?: string;
    email?: string;
    expired?: boolean;
}

export interface AppUserCreate extends rest.HasFirstAndLastName, rest.HasKeyValue {
    username: string;
    password: string;
    email: string;
    avatar?: string;
    admin?: boolean;
    roles?: string[];
    enabled: boolean;
}

export interface AppUserRead extends AppUserToken {
    enabled: boolean;
    created: string;
    lastLogin?: string;
}

export interface AppUserResetPassword {
    resetPassword: string;
}

/**
 * null properties mean let value as it is
 */
export interface AppUserUpdate extends rest.HasKeyValue, rest.HasFirstAndLastName {
    password?: string;
    avatar?: string;
    roles?: string[];
    enabled?: boolean;
}

/**
 * query object to find user
 * string properties mean search like ignore cases
 */
export interface QueryAppUser extends rest.HasKeyValue, rest.HasFirstAndLastName {
    username?: string;
    email?: string;
    freetext?: string;
    hasRole?: string;
    enabled?: boolean;
    empty: boolean;
}

export interface EmailChangeRequest {
    newEmail: string;
}

export interface JwtTokenBundle {
    token: string;
    refreshToken: string;
}

export interface LoginRequest {
    username: string;
    password: string;
}

export interface LoginResponse {
    jwtTokenBundle: JwtTokenBundle;
    user: AppUserRead;
}

export interface OAuthLoginResponse {
    scope: string;
    token_type: string;
    expires_in: number;
    access_token: string;
    refresh_expires_in: number;
    refresh_token: string;
}

export interface PasswordChangeRequest {
    currentPassword: string;
    newPassword: string;
}

export interface UpdateProfileRequest extends rest.HasKeyValue, rest.HasFirstAndLastName {
    avatar?: string;
}

export interface UsernameChangeRequest {
    newUsername: string;
}

export interface ForgotPasswordRequest {
    username?: string;
    email?: string;
    resetPasswordUrl?: string;
    verificationUrl?: string;
}

export interface PerformPasswordResetRequest {
    verification: string;
    password: string;
}

export interface RegistrationRequest extends rest.HasKeyValue, rest.HasFirstAndLastName {
    username: string;
    email: string;
    password: string;
    verificationUrl?: string;
}

export interface ValidationResponse<T> {
    valid: boolean;
    errorCodes: Record<ValidationResponseUnion<T>, string>;
}

export interface AppUserReference extends rest.HasFirstAndLastName {
    email: string;
    id: string;
    avatar?: string;
    username: string;
}

export interface AppUserToken extends AppUserReference, rest.HasKeyValue {
    roles: string[];
}

export type EmailErrorCodes = "alreadyTaken" | "invalid" | "tooLong";

export type PasswordErrorCodes = "tooShort" | "tooLong" | "insufficientLowercase" | "insufficientUppercase" | "insufficientDigit" | "insufficientSpecial" | "invalidCurrentPassword";

export type TokenErrorCodes = "expired" | "invalid";

export type UsernameErrorCodes = "alreadyTaken" | "tooShort" | "tooLong" | "notAllowedChar";

export type ValidationResponseUnion<T> = UsernameErrorCodes | PasswordErrorCodes | EmailErrorCodes | TokenErrorCodes;
