# Netty 文件上传

- https://segmentfault.com/a/1190000020087277
- https://blog.csdn.net/a953713428/article/details/72792909
- https://github.com/zhangji-hhu/BigFileTransfer

# Netty "零拷贝"

- Netty的接收和发送ByteBuffer采用DIRECT BUFFERS，使用堆外直接内存进行Socket读写，不需要进行字节缓冲区和二次拷贝。如果使用传统的堆内存(HEAP BUFFERS)进行Socket读写，JVM会将堆内存Buffer拷贝一份到直接内存中，然后才写入Socket中。相比于堆外直接内存，消息在发送过程中多了一次缓冲区的内存拷贝。
- Netty提供了组合Buffer对象，可以聚合多个ByteBuffer对象，用户可以向操作一个Buffer那样方便的堆组合Buffer进行操作，避免了传统通过内存拷贝的方式将几个小Buffer合并成一个大Buffer。
- Netty的文件传输采用了transferTo方法，它可以直接将文件缓冲区的数据发送到目标Channel，避免了传统通过循环write方式导致的内存拷贝问题。



Netty作为高性能的服务端异步IO框架必然也离不开文件读写供功能，我们可以使用netty模拟http的形式通过网页上传文件写入服务器，当然要使用http的形式那你也用不着netty！大材小用。netty4中如果想使用http形式上传文件你还得借助第三方jar包：okhttp。使用该jar完成http请求的发送。但是在netty5中已经为我们写好了，我们可以直接调用netty5的API就可以实现。所以netty4和5的差别还是挺大的，至于使用哪个，那就看选择哪一个了！



# 服务端文件上传

## `pom.xml`

```xml
<dependency>
	<groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.5.Final</version>
</dependency>
```



## server



```java
public class FileUploadServer {
    public void bind(int port) throws Exception {
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap b = new ServerBoostrap();
            b.group(bossGroup, wokerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingCOncurrentResolver(null)));	// 最大长度
                        ch.pipeline().addLast(new FileUploadServerHandler());
                    }
                });
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) throws Exception {
        int port = 8080;
        new FileUploadServer().bind(port);
    }
}
```



### `FileUploadServerHandler`

```java
public class FileUploadServerHandler extends ChannelInboundHandlerAdapter {
    private int byteRead;
    private volatile int start = 0;
    private String file_dir = "D:";
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FileUploadFile) {
            FileUploadFile ef = (FileUploadFile) msg;
            byte[] bytes = ef.getBytes();
            byteRead = ef.getEndPos();
            String md5 = ef.getFile_md5(); // 文件名
            String path = file_dir + File.separator + md5;
            File file = new File(path);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(start);
            randomAccessFile.write(bytes);
            start = start + byteRead;
            if (byteRead > 0) {
                ctx.writeAndFlush(start);
            } else {
                randomAccessFile.close();
                ctx.close();
            }
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
```



## client

```java
public class FileUploadClient {
	public void connect(int port, String host, final FileUploadFile fileUploadFile) throws Exception {
    	EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new ObjectDecode(ClassResolvers.weakCachingConcurrentResolver(null)));
                        ch.pipeline().addLast(new FileUploadClientHandler(fileUploadFile));
                    }
                });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
	} 
    
    public static void main(String[] args) throws Exception {
        int port = 8080;
        FileUploadFile uploadFile = new FileUploadFile();
        File file = new File("D:/test.txt");
        String filelMd5 = file.getName();	// 文件名
        uploadFile.setFile(file);
        uploadFile.setFile_md5(fileMd5);
        uploadFile.setStartPos(0);	// 文件开始位置
        new FileUploadClient().connect(port, "127.0.0.1", uploadFile);
    }
}
```



### `FileUploadClientHandler`

```java
public class FileUploadClientHandler extends ChannelInboudHandlerAdapter {
    private int byteRead;
    private volatile int start = 0;
    private volatile int lastLength = 0;
    public RandomAccessFile randomAccessFile;
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
    
    public void channelActive(ChannelHandlerContext ctx) {
        try {
            randomAccessFile = new RandomAccessFile(fileUploadFile.getFile(), "r");
            randomAccessFile.seek(start);
            System.out.println("块长度：" + (randomAccessFile.length()/10));
            System.out.println("长度：" + (randomAccessFile.length() - start));
            int a = (int) (randomAccessFile.length() - start);
            int b = (int) (randomAccessFile.length() / 10);
            if (a < b) {
                lastLength = a;
            }
            byte[] bytes = new byte[lastLength];
            System.out.println("-------------" + bytes.length);
            if ((byteRead = randomAccessFile.read(bytes) != -1 
                 && (randomAccessFile.length() - start) > 0)) {
                System.out.println("byte长度：" + bytes.length);
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
    
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
```

## `FileUploadFile`

统计件上传进度

```java
@Data
public class FileUploadFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private File file;	// 文件
    private String file_md5;	// 文件名
    private int startPos;	// 开始位置
    private byte[] bytes;	// 文件字节数组
    private int endPos;	// 结尾位置
}
```

