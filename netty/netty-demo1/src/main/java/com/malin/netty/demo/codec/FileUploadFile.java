package com.malin.netty.demo.codec;

import lombok.Data;

import java.io.File;

@Data
public class FileUploadFile {
    private File file;
    private String file_md5;
    private int startPos;
    private byte[] bytes;
    private int endPos;
}
