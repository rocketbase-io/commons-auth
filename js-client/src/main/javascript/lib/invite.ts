import { createRequestorFactory, Requestor } from "../util";
import type { AppInviteRead, AppUserRead, ConfirmInviteRequest } from "../api-types";
import { AxiosRequestConfig } from "axios";

export interface InviteApi {
  verify: Requestor<number, AppInviteRead>;
  transformToUser: Requestor<ConfirmInviteRequest, AppUserRead>;
}

export function createInviteApi(cf?: AxiosRequestConfig): InviteApi {
  const createRequestor = createRequestorFactory(cf, {
    baseURL: `${cf?.baseURL ?? ""}/auth/invite`,
  });

  const verify: InviteApi["verify"] = createRequestor({
    url: "",
    query: (id) => {
      return { inviteId: id };
    },
  });

  const transformToUser: InviteApi["transformToUser"] = createRequestor({
    method: "post",
    url: "/",
    body: (request) => request,
  });

  return {
    verify,
    transformToUser,
  };
}
