import { createRequestorFactory, Requestor } from "../util";
import type {
  AppUserRead,
  ExpirationInfo,
  JwtTokenBundle,
  RegistrationRequest,
} from "../api-types";
import { AxiosRequestConfig } from "axios";

export interface RegistrationApi {
  register: Requestor<RegistrationRequest, ExpirationInfo<AppUserRead>>;
  verify: Requestor<string, JwtTokenBundle>;
}

export function createRegistrationApi(cf?: AxiosRequestConfig): RegistrationApi {
  const createRequestor = createRequestorFactory(cf, {
    baseURL: `${cf?.baseURL ?? ""}/auth/`,
  });

  const register: RegistrationApi["register"] = createRequestor({
    method: "post",
    url: "/register",
    body: (request) => request,
  });

  const verify: RegistrationApi["verify"] = createRequestor({
    url: "/verify",
    query: (v) => {
      return { verification: v };
    },
  });

  return {
    register,
    verify,
  };
}
