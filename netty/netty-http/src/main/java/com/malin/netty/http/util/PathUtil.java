package com.malin.netty.http.util;


import java.io.File;

public class PathUtil {
    private static final ClassLoader classLoader = PathUtil.class.getClassLoader();

    public static String getFileBasePath() {
        String os = System.getProperty("os.name");
        String basePath;
        if (os.toLowerCase().startsWith("win")) {
            basePath = "D:/warehouse/";
        } else {
            basePath = "/root/upload_source";
        }
        basePath = basePath.replace("/", File.separator);
        return basePath;
    }

    public static String getSourcePath(String name) {
        return classLoader.getResource(name).getPath();
    }
}
