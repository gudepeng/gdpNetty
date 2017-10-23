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
    public static void main(String[] args) throws InterruptedException {
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
                            .addLast(new SimpleChannelInboundHandler<String>() {
                                @Override
                                public void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
                                    // 收到消息直接打印输出
                                    System.out.println("服务端消息 : " + s);
                                    //ctx.close();
                                }
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("发起服务端链接");
                                    super.channelActive(ctx);
                                }
                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("客户端关闭");
                                    super.channelInactive(ctx);
                                }
                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    System.out.println("发生错误");
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });
                }
            });
            // 连接服务端
            Channel ch = b.connect("127.0.0.1", 8099).sync().channel();
            // 控制台输入
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            for (;;) {
                String line = in.readLine();
                if (line == null) {
                    continue;
                }
                /*
                 * 向服务端发送在控制台输入的文本 并用"\r\n"结尾 之所以用\r\n结尾 是因为我们在handler中添加了
                 * DelimiterBasedFrameDecoder 帧解码。
                 * 这个解码器是一个根据\n符号位分隔符的解码器。所以每条消息的最后必须加上\n否则无法识别和解码
                 */
                ch.writeAndFlush(line + "\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
