import { createRequestorFactory, PageableRequest, Requestor } from "../util";
import type {
  AppInviteRead,
  AppUserCreate,
  AppUserRead,
  AppUserResetPassword,
  AppUserUpdate,
  InviteRequest,
  PageableResult,
  QueryAppUser,
} from "../api-types";
import { AxiosRequestConfig } from "axios";

export interface UserQuery extends PageableRequest, QueryAppUser {}

export interface UserUpdate {
  id: number;
  update: AppUserUpdate;
}
export interface ResetPassword {
  id: number;
  reset: AppUserResetPassword;
}

export interface UserApi {
  find: Requestor<UserQuery, PageableResult<AppUserRead>>;
  findById: Requestor<string, AppUserRead>;
  create: Requestor<AppUserCreate, AppUserRead>;
  resetPassword: Requestor<ResetPassword, AppUserRead>;
  update: Requestor<UserUpdate, AppUserRead>;
  patch: Requestor<UserUpdate, AppUserRead>;
  remove: Requestor<string, void>;
  invite: Requestor<InviteRequest, AppInviteRead>;
}

export function createUserApi(cf?: AxiosRequestConfig): UserApi {
  const createRequestor = createRequestorFactory(cf, {
    baseURL: `${cf?.baseURL ?? ""}/api/user`,
  });

  const find: UserApi["find"] = createRequestor({
    url: "",
    query: (query) => query,
  });

  const findById: UserApi["findById"] = createRequestor({
    url: (id) => `/${id}`,
  });

  const create: UserApi["create"] = createRequestor({
    method: "post",
    url: "",
    body: (create) => create,
  });

  const resetPassword: UserApi["resetPassword"] = createRequestor({
    method: "put",
    url: ({ id }) => `/${id}/password`,
    body: ({ reset }) => reset,
  });

  const update: UserApi["update"] = createRequestor({
    method: "put",
    url: ({ id }) => `/${id}`,
    body: ({ update }) => update,
  });

  const patch: UserApi["patch"] = createRequestor({
    method: "patch",
    url: ({ id }) => `/${id}`,
    body: ({ update }) => update,
  });

  const remove: UserApi["remove"] = createRequestor({
    method: "delete",
    url: (id) => `/${id}`,
  });

  const invite: UserApi["invite"] = createRequestor({
    method: "post",
    url: "/invite",
    body: (invite) => invite,
  });

  return {
    find,
    findById,
    create,
    resetPassword,
    update,
    patch,
    remove,
    invite,
  };
}
