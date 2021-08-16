package io.rocketbase.commons.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OAuthException extends RuntimeException {

    public enum ErrorType {
        INVALID_CLIENT,
        INVALID_GRANT,
        INVALID_REQUEST,
        INVALID_SCOPE,
        UNSUPPORTED_GRANT_TYPE
    }

    private final ErrorType type;

    public OAuthException(ErrorType type, String message) {
        super(message);
        this.type = type;
    }
}
