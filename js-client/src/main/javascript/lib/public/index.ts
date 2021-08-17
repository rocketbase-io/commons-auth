import { createForgotPasswordApi, ForgotPasswordApi } from "./forgotPassword";
import { createInviteApi, InviteApi } from "./invite";
import { createRegistrationApi, RegistrationApi } from "./registration";
import { createValidationApi, ValidationApi } from "./validation";
import { AxiosRequestConfig } from "axios";

export * from "./forgotPassword";
export * from "./invite";
export * from "./registration";
export * from "./validation";

/**
 * public api bundle
 */
export interface CommonsAuthPublicApi {
  forgotPassword: ForgotPasswordApi;
  invite: InviteApi;
  registration: RegistrationApi;
  validation: ValidationApi;
}

export function createCommonsAuthPublicApi(cf?: AxiosRequestConfig): CommonsAuthPublicApi {
  const forgotPassword = createForgotPasswordApi(cf);
  const invite = createInviteApi(cf);
  const registration = createRegistrationApi(cf);
  const validation = createValidationApi(cf);

  return {
    forgotPassword,
    invite,
    registration,
    validation,
  };
}
