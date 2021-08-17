import { ClientApi, createClientApi } from "./client";
import { createGroupApi, GroupApi } from "./group";
import { createTeamApi, TeamApi } from "./team";
import { createUserApi, UserApi } from "./user";
import { CapabilityApi, createCapabilityApi } from "./capability";
import { createImpersonateApi, ImpersonateApi } from "./impersonate";
import { AxiosRequestConfig } from "axios";

export * from "./client";
export * from "./group";
export * from "./team";
export * from "./user";
export * from "./capability";
export * from "./impersonate";

/**
 * backend api bundle
 */
export interface CommonsAuthBackendApi {
  capability: CapabilityApi;
  client: ClientApi;
  group: GroupApi;
  team: TeamApi;
  user: UserApi;
  impersonate: ImpersonateApi;
}

export function createCommonsAuthBackendApi(cf?: AxiosRequestConfig): CommonsAuthBackendApi {
  const capability = createCapabilityApi(cf);
  const client = createClientApi(cf);
  const group = createGroupApi(cf);
  const team = createTeamApi(cf);
  const user = createUserApi(cf);
  const impersonate = createImpersonateApi(cf);

  return {
    capability,
    client,
    group,
    team,
    user,
    impersonate,
  };
}
