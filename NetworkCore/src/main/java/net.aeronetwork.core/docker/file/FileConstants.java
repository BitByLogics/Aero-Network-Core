package net.aeronetwork.core.docker.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

@AllArgsConstructor
@Getter
public enum FileConstants {

    PACKAGES_DIRECTORY(File.separatorChar + "packages", FileType.DIRECTORY),
    RESOURCES_DIRECTORY(File.separatorChar + "resources", FileType.DIRECTORY);

    private String relativePath;
    private FileType fileType;

    public enum FileType {
        DIRECTORY,
        FILE
    }
}
