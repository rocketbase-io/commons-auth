package io.rocketbase.commons.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@Getter
public class UnknownUserException extends RuntimeException {

    private final boolean email;
    private final boolean username;
}
