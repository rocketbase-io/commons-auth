import * as rest from "./commons-rest-api";

export interface ExpirationInfo<T> {
    expires?: string;
    detail?: T;
    expired: boolean;
    /**
     * duration in seconds after verification-link will expire
     */
    expiresAfter?: number;
}

export interface AppCapabilityRead extends AppCapabilityShort {
    key: string;
    systemRefId?: string;
    /**
     * is capability parent for any other capability in the database
     */
    withChildren: boolean;
    description: string;
    parentId: number;
    created: string;
    modifiedBy: string;
    modified: string;
}

/**
 * short version of AppCapability in oder to link it in response of user + invites
 */
export interface AppCapabilityShort {
    id: number;
    /**
     * calculated path
     * 
     * concat each parent0.parent1.key split by .
     */
    keyPath: string;
}

export interface AppCapabilityWrite {
    key: string;
    description?: string;
    systemRefId?: string;
}

/**
 * query object to find {@link AppCapabilityRead}
 * string properties mean search like ignore cases
 */
export interface QueryAppCapability {
    ids?: number[];
    keyPath?: string;
    parentIds?: number[];
    key?: string;
    description?: string;
    systemRefId?: string;
}

export interface AppClientRead {
    id: number;
    systemRefId?: string;
    name: string;
    description: string;
    capabilities: AppCapabilityShort[];
    redirectUrls: string[];
    created: string;
    modifiedBy: string;
    modified: string;
}

export interface AppClientWrite {
    name: string;
    systemRefId?: string;
    description: string;
    capabilityIds: number[];
    redirectUrls: string[];
}

/**
 * query object to find {@link AppClientRead}
 * string properties mean search like ignore cases
 */
export interface QueryAppClient {
    ids?: number[];
    name?: string;
    systemRefId?: string;
    /**
     * client has capability - exact search
     */
    capabilityIds?: number[];
    redirectUrl?: string;
    description?: string;
}

export interface AppGroupRead extends AppGroupShort, rest.HasKeyValue {
    systemRefId?: string;
    name: string;
    description: string;
    /**
     * is group parent for any other group in the database
     */
    withChildren: boolean;
    parentId: number;
    capabilities: AppCapabilityShort[];
    keyValues?: Record<string, string>;
    created: string;
    modifiedBy: string;
    modified: string;
}

/**
 * short version of AppGroup in oder to link it in response of user + invites
 */
export interface AppGroupShort {
    id: number;
    /**
     * calculated tree of parent's names to get also parent names
     * 
     * example: /buyers/department/manager
     */
    namePath: string;
}

export interface AppGroupWrite extends rest.HasKeyValue {
    name: string;
    systemRefId?: string;
    description: string;
    capabilityIds: number[];
    keyValues?: Record<string, string>;
}

/**
 * query object to find {@link AppGroupRead}
 * string properties mean search like ignore cases
 */
export interface QueryAppGroup {
    ids?: number[];
    namePath?: string;
    systemRefId?: string;
    name?: string;
    parentIds?: number[];
    description?: string;
    /**
     * search for given key and value with exact match
     */
    keyValues?: Record<string, string>;
    /**
     * group has capability exact search
     */
    capabilityIds?: number[];
}

export interface AppInviteRead extends rest.HasKeyValue, rest.HasFirstAndLastName {
    id: number;
    systemRefId?: string;
    invitor: string;
    message: string;
    firstName?: string;
    lastName?: string;
    email: string;
    capabilities: AppCapabilityShort[];
    keyValues?: Record<string, string>;
    teamInvite?: AppTeamInvite;
    groups?: AppGroupShort[];
    created: string;
    modifiedBy: string;
    modified: string;
    expiration: string;
}

export interface ConfirmInviteRequest extends rest.HasFirstAndLastName {
    inviteId: number;
    username: string;
    firstName?: string;
    lastName?: string;
    email: string;
    password: string;
}

export interface InviteRequest extends rest.HasFirstAndLastName, rest.HasKeyValue {
    systemRefId?: string;
    /**
     * name of invitor that will get displayed within email + form
     */
    invitor: string;
    /**
     * optional message to add to invited person
     */
    message?: string;
    firstName?: string;
    lastName?: string;
    email: string;
    capabilityIds: number[];
    groupIds?: number[];
    keyValues?: Record<string, string>;
    /**
     * optional parameter to overwrite system default
     * 
     * full qualified url to a custom UI that proceed the invite
     * 
     * ?inviteId=VALUE will get append
     */
    inviteUrl?: string;
    teamInvite?: AppTeamInvite;
}

/**
 * query object to find invites
 * string properties mean search like ignore cases
 */
export interface QueryAppInvite extends rest.HasKeyValue {
    invitor?: string;
    email?: string;
    expired?: boolean;
    /**
     * search for given key and value with exact match ignore cases
     */
    keyValues?: Record<string, string>;
    teamId?: number;
    systemRefId?: string;
}

/**
 * used in case of user/invite creation
 */
export interface AppTeamInvite {
    teamId: number;
    role: AppTeamRole;
}

export interface AppTeamMember {
    userId: string;
    role: AppTeamRole;
}

export interface AppTeamRead extends AppTeamShort {
    systemRefId: string;
    description?: string;
    personal: boolean;
    created: string;
    modifiedBy: string;
    modified: string;
    keyValues?: Record<string, string>;
}

export interface AppTeamShort {
    id: number;
    name: string;
}

export interface AppTeamWrite extends rest.HasKeyValue {
    systemRefId?: string;
    name: string;
    description?: string;
    personal: boolean;
    keyValues?: Record<string, string>;
}

export interface AppUserMembership {
    team: AppTeamShort;
    role: AppTeamRole;
}

/**
 * query object to find {@link AppCapabilityRead}
 * string properties mean search like ignore cases
 */
export interface QueryAppTeam {
    ids?: number[];
    name?: string;
    description?: string;
    personal?: boolean;
    systemRefId?: string;
}

export interface AppUserCreate extends rest.HasFirstAndLastName, rest.HasKeyValue {
    username: string;
    password: string;
    systemRefId?: string;
    firstName?: string;
    lastName?: string;
    email: string;
    avatar?: string;
    keyValues?: Record<string, string>;
    enabled: boolean;
    capabilityIds?: number[];
    groupIds?: number[];
}

export interface AppUserRead extends AppUserReference, rest.HasKeyValue {
    capabilities: AppCapabilityShort[];
    groups?: AppGroupShort[];
    activeTeam?: AppUserMembership;
    keyValues?: Record<string, string>;
    enabled: boolean;
    locked: boolean;
    created: string;
    modifiedBy: string;
    modified: string;
    lastLogin?: string;
    setting?: UserSetting;
    identityProvider?: string;
}

export interface AppUserResetPassword {
    resetPassword: string;
}

/**
 * null properties mean let value as it is
 */
export interface AppUserUpdate extends rest.HasKeyValue {
    capabilityIds?: number[];
    groupIds?: number[];
    profile?: UserProfile;
    setting?: UserSetting;
    /**
     * will removed key that have value of null
     * 
     * will only add/replace new/existing key values
     * 
     * not mentioned key will still stay the same
     */
    keyValues?: Record<string, string>;
    enabled?: boolean;
    locked?: boolean;
    activeTeamId?: number;
}

/**
 * query object to find user
 * string properties mean search like ignore cases
 */
export interface QueryAppUser extends rest.HasKeyValue, rest.HasFirstAndLastName {
    username?: string;
    firstName?: string;
    lastName?: string;
    email?: string;
    systemRefId?: string;
    /**
     * search for given key and value with exact match
     */
    keyValues?: Record<string, string>;
    /**
     * searches for all properties containing text
     */
    freetext?: string;
    /**
     * exact capability search
     */
    capabilityIds?: number[];
    /**
     * user is member of group
     */
    groupIds?: number[];
    enabled?: boolean;
}

export interface AuthAuditRead {
    id: number;
    timestamp: string;
    userId: string;
    /**
     * for example crud / interaction
     */
    eventType: string;
    /**
     * crud: create/read/update/delete
     * 
     * interaction: login/refresh_token...
     */
    eventDetail: string;
    /**
     * crud: database-entity-name
     * 
     * interaction: service-name/api-endpoint
     */
    source: string;
    /**
     * crud: id of entity
     * 
     * interaction: null
     */
    identifier: string;
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
    user: AppUserToken;
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

export interface UsernameChangeRequest {
    newUsername: string;
}

export interface ForgotPasswordRequest {
    username?: string;
    email?: string;
    /**
     * optional parameter to overwrite system default
     * 
     * full qualified url to a custom UI that proceed the password reset
     * 
     * * ?verification=VALUE will get append
     */
    resetPasswordUrl?: string;
    /**
     * please use resetPasswordUrl will get removed in future
     * @deprecated
     */
    verificationUrl?: string;
}

export interface PerformPasswordResetRequest {
    verification: string;
    password: string;
}

export interface ConnectedAuthorization {
    /**
     * a mobile app for example an a desktop app should use different clientIds to support separated auth flows/states
     * 
     * an AppUserEntity could hold a set of unique clientId values
     */
    clientId: string;
    /**
     * timestamp of expiration of the refreshToken to know if it's could still be valid to use it...
     */
    refreshExpires: string;
    refreshToken: string;
}

export interface IdentityProvider {
    key: string;
    name: string;
    description: string;
    logo: string;
    endpoints: Endpoints;
    clientId: string;
    clientSecret: string;
    scope: string;
    defaultProvider: boolean;
    /**
     * JSON Web Key Sets
     */
    jwksUri: string;
}

export interface Endpoints {
    issuer: string;
    authorization: string;
    token: string;
    userinfo: string;
    revocation: string;
    endSession: string;
}

export interface RegistrationRequest extends rest.HasKeyValue, rest.HasFirstAndLastName {
    username: string;
    firstName?: string;
    lastName?: string;
    email: string;
    password: string;
    keyValues?: Record<string, string>;
    /**
     * optional parameter to overwrite system default
     * 
     * full qualified url to a custom UI that proceed the verification
     * 
     * * ?verification=VALUE will get append
     */
    verificationUrl?: string;
}

export interface ModelConstraint {
    model: string;
    constraints: Record<string, string>;
}

export interface ValidationConstraint {
    type: string;
    message: string;
    attributes: Record<string, string>;
}

export interface ValidationResponse<T> {
    "@type": "io.rocketbase.commons.dto.validation.ValidationResponse";
    valid: boolean;
    errorCodes: Record<string, string>;
}

/**
 * a short representation of a user
 * 
 * will be used for user-search results for example
 * 
 * important: will not keep secrets, audit oder security information like capabilities, passwords or groups
 */
export interface AppUserReference {
    email: string;
    id: string;
    username: string;
    systemRefId?: string;
    profile?: UserProfile;
}

/**
 * extended {@link AppUserReference} with roles, keyValues and groups and activeTeam
 * 
 * all it's information will get stored within the jwt-token for example
 */
export interface AppUserToken extends AppUserReference, rest.HasKeyValue {
    groups?: AppGroupShort[];
    capabilities: string[];
    activeTeam?: AppUserMembership;
    setting?: UserSetting;
    identityProvider?: string;
    keyValues?: Record<string, string>;
}

export interface EntityWithKeyValue<T> extends rest.HasKeyValue {
    keyValues?: Record<string, string>;
}

/**
 * simplified AppUserEntity without keyValues, password, audit etc...
 * 
 * used to store a simple representation as a copy of AppUserEntity in mongo or elsewhere
 */
export interface SimpleAppUserReference extends AppUserReference {
}

export interface SimpleAppUserToken extends SimpleAppUserReference, AppUserToken {
}

export interface OnlineProfile {
    /**
     * for example: website, linkedIn, pinterest, github, microsoftTeams, slack
     */
    type: string;
    value: string;
}

export interface PhoneNumber {
    /**
     * for example: phone, cellphone, fax, personal, business...
     */
    type: string;
    number: string;
}

export interface SimpleUserProfile extends UserProfile {
}

export interface SimpleUserSetting extends UserSetting {
}

export interface UserProfile extends rest.HasFirstAndLastName {
    about?: string;
    phoneNumbers?: PhoneNumber[];
    onlineProfiles?: OnlineProfile[];
    jobTitle?: string;
    country?: string;
    gender?: rest.Gender;
    salutation?: string;
    firstName?: string;
    lastName?: string;
    title?: string;
    location?: string;
    organization?: string;
    avatar?: string;
}

export interface UserSetting {
    /**
     * converts locale value or use LocaleContextHolder when not set
     * @return always a locale
     */
    currentLocale: Locale;
    currentTimeZone?: string;
    dateFormat?: string;
    timeFormat?: string;
    dateTimeFormat?: string;
    locale?: string;
}

export interface Locale extends Cloneable {
}

export interface Cloneable {
}

export type AppTeamRole = "owner" | "member";

export type EmailErrorCodes = "alreadyTaken" | "invalid" | "tooLong";

export type PasswordErrorCodes = "tooShort" | "tooLong" | "insufficientLowercase" | "insufficientUppercase" | "insufficientDigit" | "insufficientSpecial" | "invalidCurrentPassword";

export type TokenErrorCodes = "expired" | "invalid";

export type UsernameErrorCodes = "alreadyTaken" | "tooShort" | "tooLong" | "notAllowedChar";

export type ValidationResponseUnion<T> = UsernameErrorCodes | PasswordErrorCodes | EmailErrorCodes | TokenErrorCodes;
