# Netty 文件上传

- https://segmentfault.com/a/1190000020087277
- https://blog.csdn.net/a953713428/article/details/72792909
- https://github.com/zhangji-hhu/BigFileTransfer
- https://github.com/haoxiaoyong1014/netty-file
- https://recomm.cnblogs.com/blogpost/4314319?page=1
  - https://www.cnblogs.com/learningspace/p/4314319.html
- https://github.com/LWHTarena/netty
- **https://github.com/netty/netty**
  - 官网提供的Demo

# Netty "零拷贝"

- Netty的接收和发送ByteBuffer采用DIRECT BUFFERS，使用堆外直接内存进行Socket读写，不需要进行字节缓冲区和二次拷贝。如果使用传统的堆内存(HEAP BUFFERS)进行Socket读写，JVM会将堆内存Buffer拷贝一份到直接内存中，然后才写入Socket中。相比于堆外直接内存，消息在发送过程中多了一次缓冲区的内存拷贝。
- Netty提供了组合Buffer对象，可以聚合多个ByteBuffer对象，用户可以向操作一个Buffer那样方便的堆组合Buffer进行操作，避免了传统通过内存拷贝的方式将几个小Buffer合并成一个大Buffer。
- Netty的文件传输采用了transferTo方法，它可以直接将文件缓冲区的数据发送到目标Channel，避免了传统通过循环write方式导致的内存拷贝问题。



Netty作为高性能的服务端异步IO框架必然也离不开文件读写供功能，我们可以使用netty模拟http的形式通过网页上传文件写入服务器，当然要使用http的形式那你也用不着netty！大材小用。netty4中如果想使用http形式上传文件你还得借助第三方jar包：okhttp。使用该jar完成http请求的发送。但是在netty5中已经为我们写好了，我们可以直接调用netty5的API就可以实现。所以netty4和5的差别还是挺大的，至于使用哪个，那就看选择哪一个了！



# Netty 4 Socket Transport File



> 使用Netty的好处
>
> - 用NIO代替BIO，实现了分批次发送**字节流**。
>   - 因为使用 `RandomAccessFile` 只有有**计数器**就能完成**断点续传**(这个demo为了简单就没有写)
> - 使用**Socket**协议代替**HTTP**协议大大降低创建请求的开销。以及增强稳定性。
> - Netty 提供的API全面强大。相比于手写Socket少了很多坑。



## `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.malin.netty</groupId>
    <artifactId>netty-demo1</artifactId>
    <version>1.0-SNAPSHOT</version>
    <description>Netty服务端传输文件Demo</description>

    <dependencies>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.1.42.Final</version>
        </dependency>
    </dependencies>
</project>
```



## `Server` 服务端堆外暴露服务

- `FileUploadServerHandler` 继承 `ChannelInboundHandlerAdapter` 
- Server 端使用的是 `ServerBootstreap` 



```java
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
                    if (byteRead != NettyConstant.netty_transport) {
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
```



## `Client` 客户端连接服务端进行通信

- `FileUploadClientHandler` 继承 `ChannelInboundHandlerAdapter` 
- Client 端使用的是 `Bootstrap` 



```java
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

            lastLength = NettyConstant.netty_transport;
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
                            uploadFile.ge tFile().length(), start, remaining));

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
```





## `UploadFile` 用于记录文件传输进度



```java
package com.malin.netty.demo.model;

import java.io.File;
import java.io.Serializable;

public class UploadFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private File file;
    private String fileName;
    private int startPos;
    private byte[] bytes;
    private int endPos;

    // 篇幅问题 省略 get/set 
}
```



## `NettyConstant` 本次用到的常量

```java
package com.malin.netty.demo.constant;

public class NettyConstant {
    // 服务端存储目录
    public static final String server_store_path = ".";
    // 传输的块大小
    public static final Integer netty_transport_chunked = 1024 * 10;
}
```



# Netty HTTP Transport File

> 如果需要在弄





# FAQ



- Netty 4 传输超过4G的文件会出现  传输不完整的情况。(Win10 环境 硬盘NTFS文件格式)
  - 没有找到相关的解释说明