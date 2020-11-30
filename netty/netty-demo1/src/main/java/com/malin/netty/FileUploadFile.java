package com.malin.netty;

import lombok.Data;

import java.io.File;

@Data
public class FileUploadFile {
    private File file;
    private String fileMd5;
    private int startPos;
    private byte[] bytes;
    private int endPos;
}
