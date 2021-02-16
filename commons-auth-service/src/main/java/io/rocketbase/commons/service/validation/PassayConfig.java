package io.rocketbase.commons.service.validation;

import org.passay.CharacterData;

public interface PassayConfig {

    int EMAIL_MAX_LENGTH = 255;

    CharacterData SPECIAL = new CharacterData() {
        @Override
        public String getErrorCode() {
            return "INSUFFICIENT_SPECIAL";
        }

        @Override
        public String getCharacters() {
            return "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
        }
    };
}
