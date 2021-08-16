import { createRequestorFactory, Requestor } from "../util";
import type { JwtTokenBundle } from "../api-types";
import { AxiosRequestConfig } from "axios";

export interface ImpersonateApi {
  impersonate: Requestor<string, JwtTokenBundle>;
}

export function createImpersonateApi(cf?: AxiosRequestConfig): ImpersonateApi {
  const createRequestor = createRequestorFactory(cf, {
    baseURL: `${cf?.baseURL ?? ""}/api/impersonate`,
  });

  const impersonate: ImpersonateApi["impersonate"] = createRequestor({
    url: (userIdOrUsername) => `/${userIdOrUsername}`,
  });

  return {
    impersonate,
  };
}
