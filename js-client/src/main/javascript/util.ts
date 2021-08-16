import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, Method } from "axios";
import qs from "qs";

export interface RequestorConfig<Options, Result, Error = unknown> {
  client?: AxiosInstance;
  method?: Method | ((options: Options) => Method);
  url: string | ((options: Options) => string);
  headers?: unknown | ((options: Options) => unknown);
  query?: (keyof Options)[] | ((options: Options) => unknown);
  body?: (keyof Options)[] | ((options: Options) => unknown);
  options?: AxiosRequestConfig | ((options: Options) => AxiosRequestConfig);
}

export interface PageableRequest {
  page?: number;
  pageSize?: number;
  sort?: string[];
}

const deep = ["headers", "auth", "proxy", "params"];

export function mergeRequestConfig(
  ...configs: (AxiosRequestConfig | undefined)[]
): AxiosRequestConfig {
  const result: AxiosRequestConfig = {};
  for (const config of configs.map((it = {}) => it))
    for (const key of Object.keys(config) as (keyof typeof config)[])
      if (config[key] == null)
      else if (!deep.includes(key)) result[key] = config[key];
      else result[key] = { ...(result[key] ?? {}), ...(config[key] ?? {}) };

  return result;
}

export type Requestor<Options, Result> = (
  options: Options & { overrides?: AxiosRequestConfig }
) => Promise<AxiosResponse<Result>>;

function applyIfNecessary<Options, Result>(
  element: ((options: Options) => Result) | Result,
  options: Options
): Result {
  if (typeof element === "function") return (element as (options: Options) => Result)(options);
  return element;
}

export function createRequestor<Options, Result, Error = unknown>(
  config: RequestorConfig<Options, Result, Error>,
  cf?: AxiosRequestConfig
): Requestor<Options, Result> {
  const {
    client = axios,
    method = "get",
    url,
    headers,
    query,
    body,
    options: clientOptions,
  } = config;
  return function ({ overrides, ...options }) {
    const config = mergeRequestConfig(
      cf,
      {
        method: applyIfNecessary(method, options as Options),
        url: applyIfNecessary(url, options as Options),
        headers: applyIfNecessary(headers, options as Options),
        params: applyIfNecessary(query, options as Options),
        data: applyIfNecessary(body, options as Options),
      },
      applyIfNecessary(clientOptions, options as Options),
      overrides
    );
    return client.request(config);
  };
}

export function createRequestorFactory(
  ...configs: (AxiosRequestConfig | undefined)[]
): typeof createRequestor {
  const cf = mergeRequestConfig(
    {
      headers: { "content-type": "application/json" },
      paramsSerializer: (params) => qs.stringify(params, { arrayFormat: "repeat" }),
    },
    ...configs
  );
  return (config) => createRequestor(config, cf);
}
