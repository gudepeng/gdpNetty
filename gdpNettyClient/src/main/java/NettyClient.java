import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by 我是金角大王 on 2017-10-22.
 */
public class NettyClient {


    public String getServerMessage(String message){
        final ServerChannelInboundHandler serverchannelinboundhandler = new ServerChannelInboundHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.TCP_NODELAY, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline()
                            // 以("\n")为结尾分割的 解码器
                            .addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                            .addLast(new StringDecoder())
                            .addLast(new StringEncoder())
                            .addLast(serverchannelinboundhandler);
                }
            });
            // 连接服务端
            ChannelFuture ch = b.connect("127.0.0.1", 8099).sync();
            ch.channel().writeAndFlush(message+"\n").sync();
            ch.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return serverchannelinboundhandler.getResponse();
    }
}
