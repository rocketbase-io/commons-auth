import { createRequestorFactory, PageableRequest, Requestor } from "../util";
import type { AppClientRead, AppClientWrite, PageableResult, QueryAppClient } from "../api-types";
import { AxiosRequestConfig } from "axios";

export interface ClientQuery extends PageableRequest, QueryAppClient {}

export interface ClientUpdate {
  id: number;
  write: AppClientWrite;
}

export interface ClientApi {
  find: Requestor<ClientQuery, PageableResult<AppClientRead>>;
  findById: Requestor<number, AppClientRead>;
  create: Requestor<AppClientWrite, AppClientRead>;
  update: Requestor<ClientUpdate, AppClientRead>;
  remove: Requestor<number, void>;
}

export function createClientApi(cf?: AxiosRequestConfig): ClientApi {
  const createRequestor = createRequestorFactory(cf, {
    baseURL: `${cf?.baseURL ?? ""}/api/client`,
  });

  const find = createRequestor<ClientQuery, PageableResult<AppClientRead>>({
    url: "/",
    query: (query) => query,
  });

  const findById = createRequestor<number, AppClientRead>({
    url: (id) => `/${id}`,
  });

  const create = createRequestor<AppClientWrite, AppClientRead>({
    method: "post",
    url: "/",
    body: (write) => write,
  });

  const update = createRequestor<ClientUpdate, AppClientRead>({
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
