import { createRequestorFactory, Requestor } from "../../util";
import type {
  AppUserRead,
  ExpirationInfo,
  ForgotPasswordRequest,
  PerformPasswordResetRequest,
} from "../../api-types";
import { AxiosRequestConfig } from "axios";

/**
 * public interactions for password forget flow
 */
export interface ForgotPasswordApi {
  forgotPassword: Requestor<ForgotPasswordRequest, ExpirationInfo<void>>;
  resetPassword: Requestor<PerformPasswordResetRequest, AppUserRead>;
}

export function createForgotPasswordApi(cf?: AxiosRequestConfig): ForgotPasswordApi {
  const createRequestor = createRequestorFactory(cf, {
    baseURL: `${cf?.baseURL ?? ""}/auth`,
  });

  const forgotPassword: ForgotPasswordApi["forgotPassword"] = createRequestor({
    method: "put",
    url: "/forgot-password",
    body: (request) => request,
  });

  const resetPassword: ForgotPasswordApi["resetPassword"] = createRequestor({
    method: "put",
    url: "/reset-password",
    body: (request) => request,
  });

  return {
    forgotPassword,
    resetPassword,
  };
}
