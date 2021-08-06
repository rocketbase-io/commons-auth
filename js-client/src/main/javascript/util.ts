import axios, {AxiosInstance, AxiosRequestConfig, AxiosResponse, Method} from "axios";
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

export interface ApiClientFactoryConfig {
    baseUrl?: string
}

export function buildQuery(path: string, params: any): string {
    if (!params) return path;
    return path + qs.stringify(params, { arrayFormat: 'repeat' });
}

const deep = ["headers", "auth", "proxy", "params"];
export function mergeRequestConfig(...configs: AxiosRequestConfig[]): AxiosRequestConfig {
    const result: AxiosRequestConfig = {};
    for (const config of configs.map((it = {}) => it))
        for (const key of Object.keys(config))
            if (config[key] == null)
            else if (!deep.includes(key)) result[key] = config[key];
            else result[key] = { ...(result[key] ?? {}), ...(config[key] ?? {}) };
    return result;
}

export type Requestor<Options, Result> = (options: Options & { overrides?: AxiosRequestConfig }) => Promise<AxiosResponse<Result>>;

function applyIfNecessary<Options, Result>(element: ((options: Options) => Result) | Result, options: Options): Result {
    if (typeof element === "function") return (element as (options: Options) => Result)(options);
    return element;
}

const baseConfig: AxiosRequestConfig = {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    baseURL: window.env.NX_CUSTOM_API_URL,
};

export function createRequestor<Options, Result, Error = unknown>(
    config: RequestorConfig<Options, Result, Error>,
    cf: ApiClientFactoryConfig
): Requestor<Options, Result> {
    const { client = axios, method = "get", url, headers, query, body, options: clientOptions } = config;
    return function ({ overrides, ...options }) {
        const config = mergeRequestConfig(
            baseConfig,
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
