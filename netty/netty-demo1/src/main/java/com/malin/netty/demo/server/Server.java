package com.malin.netty.demo.server;

import com.malin.netty.demo.constant.NettyConstant;
import com.malin.netty.demo.model.UploadFile;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;
import java.io.RandomAccessFile;

public class Server {
    public static void main(String[] args) throws Exception {
        new Server().bind(8080);
    }

    public void bind(int port) throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<Channel>() {
                        protected void initChannel(Channel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new ObjectEncoder());
                            p.addLast(new ObjectDecoder(Integer.MAX_VALUE,
                                    ClassResolvers.weakCachingConcurrentResolver(null)));
                            p.addLast(new FileUploadServerHandler());
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            System.out.println("file server 等待连接");
            f.channel().closeFuture().sync();
            System.out.println("file server 结束");
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    class FileUploadServerHandler extends ChannelInboundHandlerAdapter {

        private volatile int start = 0;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            System.out.println("服务端：channelActive()");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // TODO 2. 接收客户端的传输的数据
            System.out.println("收到客户端发来的文件,正在处理");
            if (msg instanceof UploadFile) {
                UploadFile uf = (UploadFile) msg;
                String path = NettyConstant.server_store_path + File.separator + uf.getFileName();

                File file = new File(path);
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                // raf.seek(start.get());
                raf.seek(start);
                raf.write(uf.getBytes());

                int byteRead = uf.getEndPos();
                // start.addAndGet(byteRead);
                start = start + byteRead;

                if (byteRead > 0) {
                    ctx.writeAndFlush(start);
                    raf.close();
                    if (byteRead != NettyConstant.netty_transport_chunked) {
                        Thread.sleep(1000);
                        channelInactive(ctx);
                    }
                } else {
                    ctx.close();
                }
                System.out.println("处理完毕,文件路径:" + path + "," + byteRead);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            System.out.println("服务端：channelInactive()");
            ctx.flush();
            ctx.close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
            System.out.println("FileUploadServerHandler--exceptionCaught()");
        }
    }
}
