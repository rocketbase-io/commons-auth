import { AxiosRequestConfig } from "axios";
import { CommonsAuthBackendApi, createCommonsAuthBackendApi } from "./backend";
import { CommonsAuthPublicApi, createCommonsAuthPublicApi } from "./public";
import { AuthenticationApi, createAuthenticationApi } from "./authentication";
import { createLoginApi, LoginApi } from "./login";
import { createUserSearchApi, UserSearchApi } from "./userSearch";

export * from "./backend";
export * from "./public";
export * from "./authentication";
export * from "./login";
export * from "./userSearch";

/**
 * full api bundle
 */
export interface CommonsAuthApi {
  backend: CommonsAuthBackendApi;
  public: CommonsAuthPublicApi;
  authentication: AuthenticationApi;
  login: LoginApi;
  userSearch: UserSearchApi;
}

export function createCommonsAuthApi(cf?: AxiosRequestConfig): CommonsAuthApi {
  const backend = createCommonsAuthBackendApi(cf);
  const public_ = createCommonsAuthPublicApi(cf);
  const authentication = createAuthenticationApi(cf);
  const login = createLoginApi(cf);
  const userSearch = createUserSearchApi(cf);

  return {
    backend,
    public: public_,
    authentication,
    login,
    userSearch,
  };
}
