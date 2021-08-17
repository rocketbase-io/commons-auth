import { createRequestorFactory, PageableRequest, Requestor } from "../../util";
import type { AppTeamRead, AppTeamWrite, PageableResult, QueryAppTeam } from "../../api-types";
import { AxiosRequestConfig } from "axios";

export interface TeamQuery extends PageableRequest, QueryAppTeam {}

export interface TeamUpdate {
  id: number;
  write: AppTeamWrite;
}

/**
 * backend/admin api to interact with team entities
 */
export interface TeamApi {
  find: Requestor<TeamQuery, PageableResult<AppTeamRead>>;
  findById: Requestor<number, AppTeamRead>;
  create: Requestor<AppTeamWrite, AppTeamRead>;
  update: Requestor<TeamUpdate, AppTeamRead>;
  remove: Requestor<number, void>;
}

export function createTeamApi(cf?: AxiosRequestConfig): TeamApi {
  const createRequestor = createRequestorFactory(cf, {
    baseURL: `${cf?.baseURL ?? ""}/api/team`,
  });

  const find: TeamApi["find"] = createRequestor({
    url: "",
    query: (query) => query,
  });

  const findById: TeamApi["findById"] = createRequestor({
    url: (id) => `/${id}`,
  });

  const create: TeamApi["create"] = createRequestor({
    method: "post",
    url: "",
    body: (create) => create,
  });

  const update: TeamApi["update"] = createRequestor({
    method: "put",
    url: ({ id }) => `/${id}`,
    body: ({ write }) => write,
  });

  const remove: TeamApi["remove"] = createRequestor({
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
