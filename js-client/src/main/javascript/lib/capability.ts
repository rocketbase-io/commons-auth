import { createRequestorFactory, PageableRequest, Requestor } from "../util";
import type {
  AppCapabilityRead,
  AppCapabilityWrite,
  PageableResult,
  QueryAppCapability,
} from "../api-types";
import { AxiosRequestConfig } from "axios";

export interface CapabilityQuery extends PageableRequest, QueryAppCapability {}

export interface CapabilityUpdate {
  id: number;
  write: AppCapabilityWrite;
}

export interface CapabilityApi {
  find: Requestor<CapabilityQuery, PageableResult<AppCapabilityRead>>;
  findById: Requestor<number, AppCapabilityRead>;
  create: Requestor<AppCapabilityWrite, AppCapabilityRead>;
  update: Requestor<CapabilityUpdate, AppCapabilityRead>;
  remove: Requestor<number, void>;
}

export function createCapabilityApi(cf?: AxiosRequestConfig): CapabilityApi {
  const createRequestor = createRequestorFactory(cf, {
    baseURL: `${cf?.baseURL ?? ""}/api/capability`,
  });

  const find = createRequestor<CapabilityQuery, PageableResult<AppCapabilityRead>>({
    url: "",
    query: (query) => query,
  });

  const findById = createRequestor<number, AppCapabilityRead>({
    url: (id) => `/${id}`,
  });

  const create = createRequestor<AppCapabilityWrite, AppCapabilityRead>({
    method: "post",
    url: "/",
    body: (write) => write,
  });

  const update = createRequestor<CapabilityUpdate, AppCapabilityRead>({
    method: "put",
    url: ({ id }) => `/${id}`,
    body: ({ write }) => write,
  });

  const remove = createRequestor<number, void>({
    method: "delete",
    url: (id) => `/${id}`,
  });

  return {
    find,
    findById,
    create,
    update,
    remove,
  };
}
