package io.rocketbase.commons.dto;

public interface CommonDtoSettings {

    String PASSWORD_MESSAGE = "at least one digit, one lower+upper case letter, one special character and with a minimum length of 8 characters";
    String PASSWORD_REGEX = "^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\\d]){1,})(?=(.*[\\W]){1,})(?!.*\\s).{8,}$";
}
