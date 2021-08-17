import { createRequestorFactory, PageableRequest, Requestor } from "../../util";
import type { AppGroupRead, AppGroupWrite, PageableResult, QueryAppGroup } from "../../api-types";
import { AxiosRequestConfig } from "axios";

export interface GroupQuery extends PageableRequest, QueryAppGroup {}

export interface GroupCreate {
  parentId: number;
  write: AppGroupWrite;
}

export interface GroupUpdate {
  id: number;
  write: AppGroupWrite;
}

/**
 * backend/admin api to interact with group entities
 */
export interface GroupApi {
  find: Requestor<GroupQuery, PageableResult<AppGroupRead>>;
  findById: Requestor<number, AppGroupRead>;
  create: Requestor<GroupCreate, AppGroupRead>;
  update: Requestor<GroupUpdate, AppGroupRead>;
  remove: Requestor<number, void>;
}

export function createGroupApi(cf?: AxiosRequestConfig): GroupApi {
  const createRequestor = createRequestorFactory(cf, {
    baseURL: `${cf?.baseURL ?? ""}/api/group`,
  });

  const find: GroupApi["find"] = createRequestor({
    url: "",
    query: (query) => query,
  });

  const findById: GroupApi["findById"] = createRequestor({
    url: (id) => `/${id}`,
  });

  const create: GroupApi["create"] = createRequestor({
    method: "post",
    url: ({ parentId }) => `/${parentId}`,
    body: ({ write }) => write,
  });

  const update: GroupApi["update"] = createRequestor({
    method: "put",
    url: ({ id }) => `/${id}`,
    body: ({ write }) => write,
  });

  const remove: GroupApi["remove"] = createRequestor({
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
