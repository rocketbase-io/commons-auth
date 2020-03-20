package io.rocketbase.commons.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public class VerificationException extends RuntimeException {

    private final String field;
}
