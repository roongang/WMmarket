package com.around.wmmarket.service.common;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Constants {
    public static final Path dealPostImagePath=Paths
            .get("src","main","resources","images","dealPostImages")
            .toAbsolutePath().normalize();
}
