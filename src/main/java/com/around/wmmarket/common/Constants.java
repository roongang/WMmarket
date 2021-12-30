package com.around.wmmarket.common;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Constants {
    public static final Path dealPostImagePath=Paths
            .get("src","main","resources","images","dealPostImages")
            .toAbsolutePath().normalize();
    public static final Path userImagePath=Paths
            .get("src","main","resources","images","user")
            .toAbsolutePath().normalize();
}
