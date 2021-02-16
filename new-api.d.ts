import * as rest from "./commons-rest-api.d.ts";

export interface UserProfile extends rest.HasFirstAndLastName {
    gender?: rest.Gender;
    // max 255 chars
    firstName?: string;
    // max 255 chars
    lastName?: string;
    // max 10 chars
    title?: string;
    // url to avatar max 2000 chars
    avatar?: string;
    // max 255 chars
    location?: string;
    phoneNumbers?: PhoneNumber[];
    // max 500 chars
    about?: string;
    onlineProfiles?: OnlineProfile[];
    /**
     * Alpha-2 code - ISO 3166
     * max 2 chars for example: de, gb, us
     */
    country?: string;
    // max 100 chars
    jobTitle?: string;
    // max 100 chars
    organization?: string;
}

export interface UserSetting {
    /**
     * Java {@link Locale} in format (language-region-variant)
     * valid examples:  zh-Hant-TW, DE, en_US
     *
     * regex: ^[A-Za-z]{2,4}([_-][0-9A-Za-z]{2,8})?([_-][0-9A-Za-z]{2,8})?$
     */
    locale?: string;
    /**
     * please use valid formats for {@link java.time.ZoneId}
     * format examples: Europe/Berlin, America/Indiana/Petersburg
     *
     * regex: ^[A-Za-z][A-Za-z0-9~/._+-]+$
     */
    currentTimeZone?: string;
    /**
     * format: yyyy-MM-dd
     * max 15 chars
     */
    dateFormat?: string;
    /**
     * format: HH:mm:ss
     * max 10 chars
     */
    timeFormat?: string;
    /**
     * format: yyyy-MM-dd HH:mm
     * max 25 chars
     */
    dateTimeFormat?: string;
}

export interface OnlineProfile {
    /**
     * please stick to some like: website, linkedIn, pinterest, github, microsoftTeams, slack
     * max 15 chars
     */
    type: string;
    // max 255 chars
    value: string;
}

export interface PhoneNumber {
    /**
     * please stick to some like: phone, cellphone, fax, personal, business...
     * max 15 chars
     */
    type: string;
    // max 20 chars
    number: string;
}