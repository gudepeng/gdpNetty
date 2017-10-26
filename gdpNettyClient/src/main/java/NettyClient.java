import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by 我是金角大王 on 2017-10-22.
 */
public class NettyClient {


    public RpcResponse getServerMessage(String message){
        final ClientChannelInboundHandler serverchannelinboundhandler = new ClientChannelInboundHandler();
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
                            .addLast(new RpcEncoder(RpcRequest.class)) // 编码 RPC 请求
                            .addLast(new RpcDecoder(RpcResponse.class))
                            .addLast(serverchannelinboundhandler);
                }
            });
            // 连接服务端
            ChannelFuture ch = b.connect("127.0.0.1", 8099).sync();
            RpcRequest request = new RpcRequest();
            request.setInterfaceName("客户端class名");
            ch.channel().writeAndFlush(request).sync();
            ch.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return serverchannelinboundhandler.getResponse();
    }
}
