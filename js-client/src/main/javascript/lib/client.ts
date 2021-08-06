import {ApiClientFactoryConfig, buildQuery, createRequestor, PageableRequest, Requestor} from "../util";
import {AppClientRead, AppClientWrite, QueryAppClient} from "../api-types/commons-auth-api";
import {PageableResult} from "../api-types/commons-rest-api";

export interface ClientQuery extends PageableRequest, QueryAppClient {
}

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

export function createClientApi(cf: ApiClientFactoryConfig): ClientApi {
    const path = '/api/client';
    const headers = {"content-type": "application/json"};

    const find = createRequestor<ClientQuery, PageableResult<AppClientRead>>({
        url: (query) => buildQuery(path, query)
    }, cf);

    const findById = createRequestor<number, AppClientRead>({
        url: (id) => `${path}/${id}`
    }, cf);

    const create = createRequestor<AppClientWrite, AppClientRead>({
        method: "post",
        url: path,
        headers,
        body: (write) => write,
    }, cf);

    const update = createRequestor<ClientUpdate, AppClientRead>({
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