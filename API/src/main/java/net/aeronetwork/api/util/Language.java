package net.aeronetwork.api.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Language {

    ENGLISH_US("en_US"),
    ENGLISH_UK("en_UK");

    private String languageCode;

    public static Language match(String name) {
        return Arrays.stream(values())
                .filter(language -> language.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
