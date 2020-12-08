package com.malin.netty.http.handler;


import com.malin.netty.http.util.PathUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.net.URI;

public class HttpUploadServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final String uploadUrl = "/up";
    private static final String fromFileUrl = "/post_multipart";
    private static final HttpDataFactory factory =
            new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed

    private HttpRequest request;
    private HttpPostRequestDecoder decoder;
    private String filename;

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file
        DiskFileUpload.baseDirectory = null; // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on
        DiskAttribute.baseDirectory = null; // system temp directory
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        System.out.println("channelRead0");
        if (msg instanceof HttpRequest) {
            this.request = (HttpRequest) msg;
            URI uri = new URI(request.uri());
            System.out.println(uri);
            urlRoute(ctx, uri.getPath());
        }

        if (decoder != null && msg instanceof HttpContent) {
            // 接收一个新的请求体
            decoder.offer((HttpContent) msg);
            // 将内存中的数据序列化本地
            while (decoder.hasNext()) {
                InterfaceHttpData data = decoder.next();
                if (data != null && data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                    FileUpload fileUpload = (FileUpload) data;
                    if (fileUpload.isCompleted()) {
                        if (filename == null) filename = fileUpload.getFilename();
                        // or on File
                        fileUpload.renameTo(new File(PathUtil.getFileBasePath() + fileUpload.getFilename())); // enable to move into another
                        // File dest
                        decoder.removeHttpDataFromClean(fileUpload); //remove
                    }
                }
            }
        }

        if (decoder != null && msg instanceof LastHttpContent) {
            System.out.println("LastHttpContent");
            reset();
            writeResponse(ctx, "上传成功");

            // TODO fastdfs
            // TODO postgresql
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("channelInactive()");
        if (decoder != null) {
            decoder.cleanFiles();
        }
    }

    // url路由
    private void urlRoute(ChannelHandlerContext ctx, String uri) {
        StringBuilder urlResponse = new StringBuilder();
        // 访问文件上传页面
        if (uri.startsWith(uploadUrl)) {
            urlResponse.append("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Title</title></head><body><form action=\"http://127.0.0.1:8080/post_multipart\"enctype=\"multipart/form-data\"method=\"POST\"><input type=\"file\"name=\"YOU_KEY\"><input type=\"submit\"name=\"send\"></form></body></html>");
        } else if (uri.startsWith(fromFileUrl)) {
            decoder = new HttpPostRequestDecoder(factory, request);
            return;
        } else {
            urlResponse.append("<h1> welcome home </h1>");
        }
        writeResponse(ctx, urlResponse.toString());
    }

    private void writeResponse(ChannelHandlerContext ctx, String context) {
        ByteBuf buf = Unpooled.copiedBuffer(context, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=utf-8");
        //设置短连接 addListener 写完马上关闭连接
        ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

    private void reset() {
        request = null;
        // destroy the decoder to release all resources
        decoder.destroy();
        decoder = null;
    }

}

