package com.malin.netty.demo.client;

import com.malin.netty.demo.constant.NettyConstant;
import com.malin.netty.demo.model.UploadFile;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;
import java.io.RandomAccessFile;

public class Client {
    public static void main(String[] args) throws Exception {
        UploadFile uploadFile = new UploadFile();
        // File file = new File("D:/images/CentOS-7-x86_64-DVD-2009.iso");
        // File file = new File("D:/迅雷下载/VMware-workstation-full-16.1.0-17198959.exe");
        File file = new File("D:/mariadb-10.5.6-winx64.msi");
        String fileMd5 = file.getName();
        uploadFile.setFile(file);
        uploadFile.setFileName(fileMd5);
        uploadFile.setStartPos(0);

        new Client().connect("127.0.0.1", 8080, uploadFile);
    }

    public void connect(String host, int port, final UploadFile uploadFile) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        protected void initChannel(Channel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new ObjectEncoder());
                            p.addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                            p.addLast(new FileUploadClientHandler(uploadFile));
                        }
                    });

            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
            System.out.println("Client connect()结束");
        } finally {
            group.shutdownGracefully();
        }
    }

    class FileUploadClientHandler extends ChannelInboundHandlerAdapter {
        private int byteRead;
        private volatile int start = 0;
        private volatile int lastLength = 0;
        private UploadFile uploadFile;

        private RandomAccessFile raf;

        public FileUploadClientHandler(UploadFile uf) {
            if (uf.getFile().exists()) {
                if (!uf.getFile().isFile()) {
                    System.out.println("Not a file :" + uf.getFile());
                    return;
                }
            }
            this.uploadFile = uf;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("正在执行channelActive()");
            raf = new RandomAccessFile(uploadFile.getFile(), "r");
            raf.seek(uploadFile.getStartPos());

            lastLength = NettyConstant.netty_transport_chunked;
            byte[] bytes = new byte[lastLength];
            if ((byteRead = raf.read(bytes)) != -1) {
                uploadFile.setEndPos(byteRead);
                uploadFile.setBytes(bytes);
                // TODO 1. 发送消息到服务端
                ctx.writeAndFlush(uploadFile);
            } else {
            }
            System.out.println("channelActive()执行完毕");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // TODO 4. 读取服务端发送消息
            if (msg instanceof Integer) {
                start = (Integer) msg;
                if (start != -1) {
                    raf = new RandomAccessFile(uploadFile.getFile(), "r");
                    raf.seek(start);
                    int remaining = (int) (uploadFile.getFile().length() - start);
                    if (remaining < lastLength) {
                        lastLength = remaining;
                    }
                    byte[] bytes = new byte[lastLength];

                    System.out.println(String.format("文件长度:%s, 已完成:%s, 剩余:%s",
                            uploadFile.getFile().length(), start, remaining));

                    if ((byteRead = raf.read(bytes)) != -1 && (raf.length() - start) > 0) {
                        uploadFile.setEndPos(byteRead);
                        uploadFile.setBytes(bytes);

                        ctx.writeAndFlush(uploadFile);
                    } else {
                        raf.close();
                        ctx.close();
                        System.out.println("文件已经读完");
                    }
                }
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            // TODO Auto-generated method stub
            super.channelInactive(ctx);
            System.out.println("客户端结束传递文件channelInactive()");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

}
