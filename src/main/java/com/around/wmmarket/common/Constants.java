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
    // Pagination
    public static final int DEFAULT_PAGE_INDEX=0;
    public static final int DEFAULT_PAGE_SIZE=5;
    // WM email
    public static final String WM_EMAIL="iam.wmmarket@gmail.com";
}
