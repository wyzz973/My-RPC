package org.example.server.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import org.example.message.RpcRequest;
import org.example.message.RpcResponse;
import org.example.server.provider.ServiceProvider;
import org.example.server.ratelimit.RateLimit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author sd
 * @date 2025/3/9 21:09
 * @description: NettyRPCServerHandler类，接收来自客户端的信息，并解析调用
 */
@AllArgsConstructor
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private ServiceProvider serviceProvider;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        RpcResponse response = getResponse(rpcRequest);
        channelHandlerContext.writeAndFlush(response);
        channelHandlerContext.close();
    }

    private RpcResponse getResponse(RpcRequest rpcRequest) {
        //得到服务名
        String interfaceName = rpcRequest.getInterfaceName();

        //接口限流降级
        RateLimit rateLimit = serviceProvider.getRateLimitProvider().getRateLimit(interfaceName);
        if (!rateLimit.getToken()){
            //如果获取令牌失败，进行限流降级，快速返回结果
            System.out.println("服务限流！！");
            return RpcResponse.fail();
        }



        //得到服务端相应服务实现类
        Object service = serviceProvider.getService(interfaceName);

        //反射调用方法
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamsType());
            Object invoke = method.invoke(service, rpcRequest.getParams());
//            Thread.sleep(100000000);//模拟服务器反应慢
            return RpcResponse.sussess(invoke);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RpcResponse.fail();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
