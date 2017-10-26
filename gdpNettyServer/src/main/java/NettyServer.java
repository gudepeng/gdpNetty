import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by 我是金角大王 on 2017-10-22.
 */
public class NettyServer {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline()
                            .addLast(new RpcDecoder(RpcRequest.class)) // 解码 RPC 请求
                            .addLast(new RpcEncoder(RpcResponse.class)) // 编码 RPC 响应
                            .addLast(new SimpleChannelInboundHandler<RpcRequest>() {
                                @Override
                                public void channelRead0(ChannelHandlerContext ctx, RpcRequest s) throws Exception {
                                    // 收到消息直接打印输出
                                    System.out.println(ctx.channel().remoteAddress() + "客戶端消息 :"+s.getInterfaceName());
                                    // 返回客户端消息 - 我已经接收到了你的消息
                                    RpcResponse response = new RpcResponse();
                                    response.setResult("服务端执行完成");
                                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                                    ;
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println(ctx.channel().remoteAddress() + "客户端发来链接");
                                    super.channelActive(ctx);
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    System.out.println("发生错误");
                                    cause.printStackTrace();
                                    ctx.close();
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("服务端关闭");
                                    super.channelInactive(ctx);
                                }
                            });
                }
            });
            b.option(ChannelOption.SO_BACKLOG, 1);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 服务器绑定端口监听
            ChannelFuture f = b.bind(8099).sync();
            // 监听服务器关闭监听
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
