package io.rocketbase.commons.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegistrationException extends RuntimeException {

    private final boolean username;
    private final boolean email;

}
