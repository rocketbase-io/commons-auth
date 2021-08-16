import { createRequestorFactory, PageableRequest, Requestor } from "../util";
import type { AppUserRead, PageableResult, QueryAppUser } from "../api-types";
import { AxiosRequestConfig } from "axios";

export interface UserSearchQuery extends PageableRequest, QueryAppUser {}

export interface UserSearchApi {
  search: Requestor<UserSearchQuery, PageableResult<AppUserRead>>;
  findByUsernameOrId: Requestor<string, AppUserRead>;
}

export function createUserSearchApi(cf?: AxiosRequestConfig): UserSearchApi {
  const createRequestor = createRequestorFactory(cf, {
    baseURL: `${cf?.baseURL ?? ""}/api/user-search`,
  });

  const search: UserSearchApi["search"] = createRequestor({
    url: "",
    query: (query) => query,
  });

  const findByUsernameOrId: UserSearchApi["findByUsernameOrId"] = createRequestor({
    url: (usernameOrId) => `/${usernameOrId}`,
  });

  return {
    search,
    findByUsernameOrId,
  };
}
