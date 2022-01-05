package com.around.wmmarket.common;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Constants {
    // Path
    public static final Path dealPostImagePath=Paths
            .get("src","main","resources","images","dealPostImages")
            .toAbsolutePath().normalize();
    public static final Path userImagePath=Paths
            .get("src","main","resources","images","user")
            .toAbsolutePath().normalize();

    // API
    public static final String API_PATH="/api/v1";
}
