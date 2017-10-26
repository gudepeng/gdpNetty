/**
 * Created by 我是金角大王 on 2017-10-24.
 */
public class RunNetty {
    public static void main(String[] args) {
        NettyClient nettyclient = new NettyClient();
        RpcResponse servermassage = nettyclient.getServerMessage("客户端信息");
        System.out.println("最后结果:"+servermassage.getResult());
    }
}
