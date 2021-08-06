/**
 * wrapped response in case of errors
 */
export interface ErrorResponse {
    /**
     * http status code
     */
    status: number;
    /**
     * user readable error explanation
     */
    message: string;
    /**
     * in case of form validations details related to properties. key is the filed value list of related errors
     */
    fields?: Record<string, string[]>;
}

/**
 * wrapping object for paged result lists
 */
export interface PageableResult<E> {
    /**
     * total count of values in database
     */
    totalElements: number;
    /**
     * count of pages in total with given pageSize
     */
    totalPages: number;
    /**
     * current page (starts by 0)
     */
    page: number;
    /**
     * maximum size of content list
     */
    pageSize: number;
    /**
     * content of current page. count of elements is less or equals pageSize (depends on totalElements and page/pageSize)
     */
    content: E[];
}

/**
 * simple address object
 */
export interface AddressDto {
    /**
     * first address line
     */
    addressLineOne?: string;
    /**
     * second address line (optional)
     */
    addressLineTwo?: string;
    city?: string;
    state?: string;
    postalCode?: string;
    /**
     * iso country code 2 letters - ISO 3166-1 alpha-2
     */
    countryCode?: string;
}

export interface ContactDto extends HasFirstAndLastName {
    gender?: Gender;
    salutation?: string;
    title?: string;
    email?: string;
    landline?: string;
    cellphone?: string;
}

/**
 * Object representation of validation constraints defined on a model domain object
 */
export interface ModelConstraint {
    /**
     * Model class name. Example: io.rocketbase.commons.model.User
     */
    model: string;
    /**
     * Map of all validation constraints on each model property
     */
    constraints: Record<string, ValidationConstraint[]>;
}

/**
 * Object representation of validation constraints.
 */
export interface ValidationConstraint {
    /**
     * Constraint type. Example: NotNull
     */
    type: string;
    /**
     * Associated constraint message. Example: may not be null
     */
    message: string;
}

export interface EntityWithKeyValue<T> extends HasKeyValue {
}

/**
 * entity/dto has firstName + lastName capability
 */
export interface HasFirstAndLastName {
    firstName?: string;
    lastName?: string;
}

/**
 * entity/dto has key value capability
 */
export interface HasKeyValue {
    keyValues?: Record<string, string>;
}

export type Gender = "female" | "male" | "diverse";