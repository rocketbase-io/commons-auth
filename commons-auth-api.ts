// Generated using typescript-generator version 2.20.583 on 2020-03-05 21:14:45.

export interface AppInviteRead {
    id: string;
    invitor: string;
    message: string;
    firstName: string;
    lastName: string;
    email: string;
    roles: string[];
    keyValues: Record<string, string>;
    created: string;
    expiration: string;
}

export interface ConfirmInviteRequest {
    inviteId: string;
    username: string;
    firstName?: string;
    lastName?: string;
    email: string;
    password: string;
}

export interface InviteRequest extends HasFirstAndLastName {
    invitor: string;
    message?: string;
    email: string;
    roles: string[];
    keyValues?: Record<string, string>;
    inviteUrl?: string;
}

/**
 * query object to find invites
 * string properties mean search like ignore cases
 */
export interface QueryAppInvite {
    invitor?: string;
    email?: string;
    expired?: boolean;
    keyValues?: Record<string, string>;
}

export interface AppUserCreate {
    username: string;
    password: string;
    firstName?: string;
    lastName?: string;
    email: string;
    avatar?: string;
    keyValues?: Record<string, string>;
    admin: boolean;
    enabled: boolean;
}

export interface AppUserRead extends AppUserToken, HasKeyValue {
    enabled: boolean;
    created: string;
    lastLogin?: string;
}

/**
 * null properties mean let value as it is
 */
export interface AppUserUpdate {
    password?: string;
    firstName?: string;
    lastName?: string;
    avatar?: string;
    roles?: string[];
    keyValues?: Record<string, string>;
    enabled?: boolean;
}

/**
 * query object to find user
 * string properties mean search like ignore cases
 */
export interface QueryAppUser {
    username?: string;
    firstName?: string;
    lastName?: string;
    email?: string;
    keyValues?: Record<string, string>;
    freetext?: string;
    hasRole?: string;
    enabled?: boolean;
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

export interface UpdateProfileRequest {
    firstName?: string;
    lastName?: string;
    avatar?: string;
    keyValues?: Record<string, string>;
}

export interface ForgotPasswordRequest {
    username?: string;
    email?: string;
    resetPasswordUrl?: string;
}

export interface PerformPasswordResetRequest {
    verification: string;
    password: string;
}

export interface RegistrationRequest {
    username: string;
    firstName?: string;
    lastName?: string;
    email: string;
    password: string;
    keyValues?: Record<string, string>;
    verificationUrl?: string;
}

export interface ValidationResponse {
    valid: boolean;
    errorCodes: Record<string, string>;
}

export interface AppUserReference extends HasFirstAndLastName {
    id: string;
    avatar?: string;
    email: string;
    username: string;
}

export interface AppUserToken extends AppUserReference, HasKeyValue {
    roles: string[];
}

export interface HasFirstAndLastName {
    firstName?: string;
    lastName?: string;
}

export interface HasKeyValue {
    /**
     * @return an immutable map so that changes should only be done by add/remove KeyValue
     */
    keyValues?: Record<string, string>;
}

export type EmailErrorCodes = "alreadyTaken" | "invalid" | "tooLong";

export type PasswordErrorCodes =
    "tooShort"
    | "tooLong"
    | "insufficientLowercase"
    | "insufficientUppercase"
    | "insufficientDigit"
    | "insufficientSpecial"
    | "invalidCurrentPassword";

export type TokenErrorCodes = "expired" | "invalid";

export type UsernameErrorCodes = "alreadyTaken" | "tooShort" | "tooLong" | "notAllowedChar";

export type ValidationResponseUnion<T> = UsernameErrorCodes | PasswordErrorCodes | EmailErrorCodes | TokenErrorCodes;
