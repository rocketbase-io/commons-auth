import { AxiosRequestConfig } from "axios";
import {
  CapabilityApi,
  ClientApi,
  createAuthenticationApi,
  createCapabilityApi,
  createClientApi,
  createForgotPasswordApi,
  createGroupApi,
  createImpersonateApi,
  createInviteApi,
  createLoginApi,
  createRegistrationApi,
  createTeamApi,
  createUserApi,
  createUserSearchApi,
  createValidationApi,
  GroupApi,
} from "@/main/javascript";
import { AuthenticationApi } from "@/main/javascript/lib/authentication";
import { ForgotPasswordApi } from "@/main/javascript/lib/forgotPassword";
import { ImpersonateApi } from "@/main/javascript/lib/impersonate";
import { InviteApi } from "@/main/javascript/lib/invite";
import { LoginApi } from "@/main/javascript/lib/login";
import { RegistrationApi } from "@/main/javascript/lib/registration";
import { TeamApi } from "@/main/javascript/lib/team";
import { UserApi } from "@/main/javascript/lib/user";
import { UserSearchApi } from "@/main/javascript/lib/userSearch";
import { ValidationApi } from "@/main/javascript/lib/validation";

export * from "./authentication";
export * from "./capability";
export * from "./client";
export * from "./forgotPassword";
export * from "./group";
export * from "./impersonate";
export * from "./invite";
export * from "./login";
export * from "./registration";
export * from "./team";
export * from "./user";
export * from "./userSearch";
export * from "./validation";

export interface CommonsAuthApi {
  authentication: AuthenticationApi;
  capability: CapabilityApi;
  client: ClientApi;
  forgotPassword: ForgotPasswordApi;
  group: GroupApi;
  impersonate: ImpersonateApi;
  invite: InviteApi;
  login: LoginApi;
  registration: RegistrationApi;
  team: TeamApi;
  user: UserApi;
  userSearch: UserSearchApi;
  validation: ValidationApi;
}

export function createCommonsAuthApi(cf?: AxiosRequestConfig): CommonsAuthApi {
  const authentication = createAuthenticationApi(cf);
  const capability = createCapabilityApi(cf);
  const client = createClientApi(cf);
  const forgotPassword = createForgotPasswordApi(cf);
  const group = createGroupApi(cf);
  const impersonate = createImpersonateApi(cf);
  const invite = createInviteApi(cf);
  const login = createLoginApi(cf);
  const registration = createRegistrationApi(cf);
  const team = createTeamApi(cf);
  const user = createUserApi(cf);
  const userSearch = createUserSearchApi(cf);
  const validation = createValidationApi(cf);

  return {
    authentication,
    capability,
    client,
    forgotPassword,
    group,
    impersonate,
    invite,
    login,
    registration,
    team,
    user,
    userSearch,
    validation,
  };
}
