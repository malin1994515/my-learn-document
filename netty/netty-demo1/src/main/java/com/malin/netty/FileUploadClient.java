package com.malin.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUploadClient {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String host = "127.0.0.1";

        File file = new File("D:/mariadb-10.5.6-winx64.msi");
        FileUploadFile uploadFile = new FileUploadFile();
        uploadFile.setFile(file);
        uploadFile.setFileMd5(file.getName());
        uploadFile.setStartPos(0);

        new FileUploadClient().connect(port, host, uploadFile);
    }

    public void connect(int port, String host, final FileUploadFile fileUploadFile) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                            ch.pipeline().addLast(new FileUploadClientHandler(fileUploadFile));
                        }
                    });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    class FileUploadClientHandler extends ChannelInboundHandlerAdapter {
        private int byteRead;
        private volatile int start = 0;
        private volatile int lastLength = 0;
        private RandomAccessFile randomAccessFile;
        private FileUploadFile fileUploadFile;

        public FileUploadClientHandler(FileUploadFile ef) throws FileNotFoundException {
            if (ef.getFile().exists()) {
                if (!ef.getFile().isFile()) {
                    System.out.println("Not a file :" + ef.getFile());
                    return;
                }
            }
            this.fileUploadFile = ef;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            try {
                randomAccessFile = new RandomAccessFile(fileUploadFile.getFile(), "r");
                randomAccessFile.seek(fileUploadFile.getStartPos());
                lastLength = (int) randomAccessFile.length() / 10;
                byte[] bytes = new byte[lastLength];
                if ((byteRead = randomAccessFile.read(bytes)) != -1) {
                    fileUploadFile.setEndPos(byteRead);
                    fileUploadFile.setBytes(bytes);
                    ctx.writeAndFlush(fileUploadFile);
                } else {
                    System.out.println("文件已经读完");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof Integer) {
                start = (Integer) msg;
                if (start != -1) {
                    randomAccessFile = new RandomAccessFile(fileUploadFile.getFile(), "r");
                    randomAccessFile.seek(start);
                    System.out.println("块儿长度：" + (randomAccessFile.length() / 10));
                    System.out.println("长度：" + (randomAccessFile.length() - start));
                    int a = (int) (randomAccessFile.length() - start);
                    int b = (int) (randomAccessFile.length() / 10);
                    if (a < b) {
                        lastLength = a;
                    }
                    byte[] bytes = new byte[lastLength];
                    System.out.println("-----------------------------" + bytes.length);
                    if ((byteRead = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - start) > 0) {
                        System.out.println("byte 长度：" + bytes.length);
                        fileUploadFile.setEndPos(byteRead);
                        fileUploadFile.setBytes(bytes);
                        try {
                            ctx.writeAndFlush(fileUploadFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        randomAccessFile.close();
                        ctx.close();
                        System.out.println("文件已经读完--------" + byteRead);
                    }
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }


}
