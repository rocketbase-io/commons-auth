import { createRequestorFactory, Requestor } from "../util";
import type { LoginRequest, LoginResponse } from "../api-types";
import { AxiosRequestConfig } from "axios";

export interface NewAccessTokenRequest {
  refreshToken: string;
}

export interface LoginApi {
  login: Requestor<LoginRequest, LoginResponse>;
  newAccessToken: Requestor<NewAccessTokenRequest, string>;
}

export function createLoginApi(cf?: AxiosRequestConfig): LoginApi {
  const createRequestor = createRequestorFactory(cf, {
    baseURL: `${cf?.baseURL ?? ""}/auth/`,
  });

  const login: LoginApi["login"] = createRequestor({
    method: "post",
    url: "/login",
    body: (request) => request,
  });

  const newAccessToken: LoginApi["newAccessToken"] = createRequestor({
    url: "/refresh",
    headers: ({ refreshToken }) => {
      return { authorization: `Bearer ${refreshToken}` };
    },
  });

  return {
    login,
    newAccessToken,
  };
}
