import { createRequestorFactory, Requestor } from "../util";
import type {
  AppUserRead,
  EmailChangeRequest,
  ExpirationInfo,
  PasswordChangeRequest,
  UsernameChangeRequest,
  UserProfile,
  UserSetting,
} from "../api-types";
import { AxiosRequestConfig } from "axios";

export interface AuthenticationApi {
  me: Requestor<void, AppUserRead>;
  changePassword: Requestor<PasswordChangeRequest, void>;
  changeUsername: Requestor<UsernameChangeRequest, AppUserRead>;
  changeEmail: Requestor<EmailChangeRequest, ExpirationInfo<AppUserRead>>;
  verifyEmail: Requestor<string, AppUserRead>;
  updateProfile: Requestor<UserProfile, AppUserRead>;
  updateSetting: Requestor<UserSetting, AppUserRead>;
}

export function createAuthenticationApi(cf?: AxiosRequestConfig): AuthenticationApi {
  const createRequestor = createRequestorFactory(cf, {
    baseURL: `${cf?.baseURL ?? ""}/auth`,
  });

  const me: AuthenticationApi["me"] = createRequestor({
    url: "/me",
  });

  const changePassword: AuthenticationApi["changePassword"] = createRequestor({
    method: "put",
    url: "/change-password",
    body: (request) => request,
  });

  const changeUsername: AuthenticationApi["changeUsername"] = createRequestor({
    method: "put",
    url: "/change-username",
    body: (request) => request,
  });

  const changeEmail: AuthenticationApi["changeEmail"] = createRequestor({
    method: "put",
    url: "/change-email",
    body: (request) => request,
  });

  const verifyEmail: AuthenticationApi["verifyEmail"] = createRequestor({
    url: "/verify-email",
    query: (v) => {
      return { verification: v };
    },
  });

  const updateProfile: AuthenticationApi["updateProfile"] = createRequestor({
    method: "put",
    url: "/update-profile",
    body: (request) => request,
  });

  const updateSetting: AuthenticationApi["updateSetting"] = createRequestor({
    method: "put",
    url: "/update-setting",
    body: (request) => request,
  });

  return {
    me,
    changePassword,
    changeUsername,
    changeEmail,
    verifyEmail,
    updateProfile,
    updateSetting,
  };
}
