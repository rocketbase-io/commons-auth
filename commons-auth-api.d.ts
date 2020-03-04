export interface AppInviteRead extends HasKeyValue {
    id: string;
    invitor: string;
    message: string;
    firstName: string;
    lastName: string;
    email: string;
    roles: string[];
    created: string;
    expiration: string;
}

export interface ConfirmInviteRequest {
    inviteId: string;
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
}

export interface InviteRequest extends HasFirstAndLastName, HasKeyValue {
    invitor: string;
    message: string;
    email: string;
    roles: string[];
    inviteUrl: string;
}

export interface QueryAppInvite extends HasKeyValue {
    invitor: string;
    email: string;
    expired: boolean;
}

export interface AppUserCreate extends HasKeyValue {
    username: string;
    password: string;
    firstName: string;
    lastName: string;
    email: string;
    avatar: string;
    admin: boolean;
    enabled: boolean;
}

export interface AppUserRead extends AppUserToken {
    enabled: boolean;
    created: string;
    lastLogin: string;
}

export interface AppUserUpdate extends HasKeyValue {
    password: string;
    firstName: string;
    lastName: string;
    avatar: string;
    roles: string[];
    enabled: boolean;
}

export interface QueryAppUser extends HasKeyValue {
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    freetext: string;
    hasRole: string;
    enabled: boolean;
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

export interface UpdateProfileRequest extends HasKeyValue {
    firstName: string;
    lastName: string;
    avatar: string;
}

export interface ForgotPasswordRequest {
    username: string;
    email: string;
    resetPasswordUrl: string;
}

export interface PerformPasswordResetRequest {
    verification: string;
    password: string;
}

export interface RegistrationRequest {
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    keyValues: Record<string, string>;
    verificationUrl: string;
}

export interface ValidationResponse<T> {
    valid: boolean;
    errorCodes: Record<string, string>;
}

export interface HasFirstAndLastName {
    firstName: string;
    lastName: string;
}

export interface AppUserToken extends AppUserReference, HasKeyValue {
    roles: string[];
}

export interface AppUserReference extends HasFirstAndLastName {
    id: string;
    email: string;
    avatar: string;
    username: string;
}

export interface HasKeyValue {
    keyValues: Record<string, string>;
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

export type ValidationResponseUnion = UsernameErrorCodes | PasswordErrorCodes | EmailErrorCodes | TokenErrorCodes;
