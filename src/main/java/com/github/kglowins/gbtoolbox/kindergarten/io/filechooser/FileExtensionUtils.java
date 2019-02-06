package com.github.kglowins.gbtoolbox.kindergarten.io.filechooser;

import static com.google.common.io.Files.getFileExtension;

public class FileExtensionUtils {

    public static String decorateWithExtension(String originalPath, String expectedExtension) {
        final String currentExtension = getFileExtension(originalPath);
        return currentExtension.equals(expectedExtension) ? originalPath
            : String.format("%s.%s", originalPath, expectedExtension);
    }
}
