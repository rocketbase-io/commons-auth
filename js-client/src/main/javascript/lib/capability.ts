import {ApiClientFactoryConfig, buildQuery, createRequestor, PageableRequest, Requestor} from "../util";
import {AppCapabilityRead, AppCapabilityWrite, QueryAppCapability} from "../api-types/commons-auth-api";
import {PageableResult} from "../api-types/commons-rest-api";

export interface CapabilityQuery extends PageableRequest, QueryAppCapability {
}

export interface CapabilityUpdate {
    id: number;
    write: AppCapabilityWrite;
}

export interface CapabilityApi {
    find: Requestor<CapabilityQuery, PageableResult<AppCapabilityRead>>;
    findById: Requestor<number, AppCapabilityRead>;
    create: Requestor<AppCapabilityWrite, AppCapabilityRead>;
    update: Requestor<CapabilityUpdate, AppCapabilityRead>;
    remove: Requestor<number, void>;
}

export function createCapabilityApi(cf: ApiClientFactoryConfig): CapabilityApi {
    const path = '/api/capability';
    const headers = {"content-type": "application/json"};

    const find = createRequestor<CapabilityQuery, PageableResult<AppCapabilityRead>>({
        url: (query) => buildQuery(path, query)
    }, cf);

    const findById = createRequestor<number, AppCapabilityRead>({
        url: (id) => `${path}/${id}`
    }, cf);

    const create = createRequestor<AppCapabilityWrite, AppCapabilityRead>({
        method: "post",
        url: path,
        headers,
        body: (write) => write,
    }, cf);

    const update = createRequestor<CapabilityUpdate, AppCapabilityRead>({
        method: "put",
        url: ({id}) => `${path}/${id}`,
        headers,
        body: ({write}) => write,
    }, cf);

    const remove = createRequestor<number, void>({
        method: "delete",
        url: (id) => `${path}/${id}`
    }, cf);

    return {
        find,
        findById,
        create,
        update,
        remove
    }
}