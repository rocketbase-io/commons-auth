import { createRequestorFactory, PageableRequest, Requestor } from "../../util";
import type {
  AppCapabilityRead,
  AppCapabilityWrite,
  PageableResult,
  QueryAppCapability,
} from "../../api-types";
import { AxiosRequestConfig } from "axios";

export interface CapabilityQuery extends PageableRequest, QueryAppCapability {}

export interface CapabilityUpdate {
  id: number;
  write: AppCapabilityWrite;
}

/**
 * backend/admin api to interact with capability entities
 */
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

  const find: CapabilityApi["find"] = createRequestor({
    url: "",
    query: (query) => query,
  });

  const findById: CapabilityApi["findById"] = createRequestor({
    url: (id) => `/${id}`,
  });

  const create: CapabilityApi["create"] = createRequestor({
    method: "post",
    url: "/",
    body: (write) => write,
  });

  const update: CapabilityApi["update"] = createRequestor({
    method: "put",
    url: ({ id }) => `/${id}`,
    body: ({ write }) => write,
  });

  const remove: CapabilityApi["remove"] = createRequestor({
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
