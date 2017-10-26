import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by 我是金角大王 on 2017-10-24.
 */
public class ClientChannelInboundHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private RpcResponse response;

    public RpcResponse getResponse() {
        return this.response;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse s) throws Exception {
        System.out.println("接收到值: " + s.getResult());
        this.response=s;
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
}
