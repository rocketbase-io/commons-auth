import { createRequestorFactory, Requestor } from "../../util";
import type {
  EmailErrorCodes,
  PasswordErrorCodes,
  TokenErrorCodes,
  UsernameErrorCodes,
  ValidationResponse,
} from "../../api-types";
import { AxiosRequestConfig } from "axios";

/**
 * public endpoints for validations
 */
export interface ValidationApi {
  validatePassword: Requestor<string, ValidationResponse<PasswordErrorCodes>>;
  validateUsername: Requestor<string, ValidationResponse<UsernameErrorCodes>>;
  validateEmail: Requestor<string, ValidationResponse<EmailErrorCodes>>;
  validateToken: Requestor<string, ValidationResponse<TokenErrorCodes>>;
}

export function createValidationApi(cf?: AxiosRequestConfig): ValidationApi {
  const createRequestor = createRequestorFactory(cf, {
    baseURL: `${cf?.baseURL ?? ""}/auth/validate`,
  });

  const validatePassword: ValidationApi["validatePassword"] = createRequestor({
    method: "post",
    url: "/password",
    body: (password) => password,
  });

  const validateUsername: ValidationApi["validateUsername"] = createRequestor({
    method: "post",
    url: "/username",
    body: (username) => username,
  });

  const validateEmail: ValidationApi["validateEmail"] = createRequestor({
    method: "post",
    url: "/email",
    body: (email) => email,
  });

  const validateToken: ValidationApi["validateToken"] = createRequestor({
    method: "post",
    url: "/token",
    body: (token) => token,
  });

  return {
    validatePassword,
    validateUsername,
    validateEmail,
    validateToken,
  };
}
