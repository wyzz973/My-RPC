package part2.Client.proxy;

import part2.Client.rpcClient.RpcClient;
import part2.Client.rpcClient.impl.SimpleSocketRpcCilent;
import part2.Client.rpcClient.impl.NettyRpcClient;
import part2.common.Message.RpcRequest;
import part2.common.Message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author sd
 * @date 2025/3/8 16:38
 * @description: 客户端动态代理的实现
 */
//@AllArgsConstructor
public class ClientProxy implements InvocationHandler {

    private RpcClient rpcClient;
    public ClientProxy(String host, int port, int choose) {
        switch (choose){
            case 0:
                rpcClient=new NettyRpcClient(host,port);
                break;
            case 1:
                rpcClient=new SimpleSocketRpcCilent(host,port);
        }
    }

    public ClientProxy(String host,int port){
        rpcClient=new NettyRpcClient(host,port);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes()).build();

        //IOClient.sendRequest 和服务端进行数据传输
        RpcResponse response = rpcClient.sendRequest(request);

        return response.getData();
    }

    /**
     * 生成动态代理对象：
     * Proxy.newProxyInstance 创建一个JDK动态代理对象，该对象实现 clazz 这个接口，并将方法调用交由 ClientProxy 处理。
     * 返回代理对象：
     * 由于 Proxy.newProxyInstance 返回的是 Object，所以需要手动转换为 T 类型。
     * @param clazz
     * @return
     * @param <T>
     */

    public <T> T getProxy(Class<T> clazz){
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
        return (T) o;
    }
}
