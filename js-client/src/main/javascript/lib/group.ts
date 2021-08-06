import {ApiClientFactoryConfig, buildQuery, createRequestor, PageableRequest, Requestor} from "../util";
import {AppGroupRead, AppGroupWrite, QueryAppGroup} from "../api-types/commons-auth-api";
import {PageableResult} from "../api-types/commons-rest-api";

export interface GroupQuery extends PageableRequest, QueryAppGroup {
}

export interface GroupUpdate {
    id: number;
    write: AppGroupWrite;
}

export interface GroupApi {
    find: Requestor<GroupQuery, PageableResult<AppGroupRead>>;
    findById: Requestor<number, AppGroupRead>;
    create: Requestor<AppGroupWrite, AppGroupRead>;
    update: Requestor<GroupUpdate, AppGroupRead>;
    remove: Requestor<number, void>;
}

export function createGroupApi(cf: ApiClientFactoryConfig): GroupApi {
    const path = '/api/group';
    const headers = {"content-type": "application/json"};

    const find = createRequestor<GroupQuery, PageableResult<AppGroupRead>>({
        url: (query) => buildQuery(path, query)
    }, cf);

    const findById = createRequestor<number, AppGroupRead>({
        url: (id) => `${path}/${id}`
    }, cf);

    const create = createRequestor<AppGroupWrite, AppGroupRead>({
        method: "post",
        url: path,
        headers,
        body: (write) => write,
    }, cf);

    const update = createRequestor<GroupUpdate, AppGroupRead>({
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