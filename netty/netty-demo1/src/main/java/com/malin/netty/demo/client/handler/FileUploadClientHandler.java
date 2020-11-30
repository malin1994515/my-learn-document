package com.malin.netty.demo.client.handler;

import com.malin.netty.demo.codec.FileUploadFile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.RandomAccessFile;

public class FileUploadClientHandler extends ChannelInboundHandlerAdapter {
    private int byteRead;
    private volatile int start = 0;
    private volatile int lastLength = 0;
    private RandomAccessFile randomAccessFile;
    private FileUploadFile fileUploadFile;

    public FileUploadClientHandler(FileUploadFile ef) {
        if (ef.getFile().exists()) {
            if (!ef.getFile().isFile()) {
                System.out.println("Not a file :" + ef.getFile());
                return;
            }
        }
        this.fileUploadFile = ef;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        randomAccessFile = new RandomAccessFile(fileUploadFile.getFile(), "r");
        randomAccessFile.seek(fileUploadFile.getStartPos());
        lastLength = (int) (randomAccessFile.length() / 10);
        byte[] bytes = new byte[lastLength];
        if ((byteRead = randomAccessFile.read(bytes)) != -1) {
            fileUploadFile.setEndPos(byteRead);
            fileUploadFile.setBytes(bytes);
            ctx.writeAndFlush(fileUploadFile);
        } else {
            System.out.println("文件已经读完");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        
    }
}
