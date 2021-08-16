import { AxiosRequestConfig } from "axios";
import {
  CapabilityApi,
  ClientApi,
  createCapabilityApi,
  createClientApi,
  createGroupApi,
  GroupApi,
} from "@/main/javascript";

export * from "./client";
export * from "./capability";
export * from "./group";

export interface CommonsAuthApi {
  capability: CapabilityApi;
  client: ClientApi;
  group: GroupApi;
}

export function createCommonsAuthApi(cf?: AxiosRequestConfig): CommonsAuthApi {
  const capability = createCapabilityApi(cf);
  const client = createClientApi(cf);
  const group = createGroupApi(cf);
  return {
    capability,
    client,
    group,
  };
}
