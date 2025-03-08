package part1.Client.proxy;

import lombok.AllArgsConstructor;
import part1.Client.IOClient;
import part1.common.Message.RpcRequest;
import part1.common.Message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author sd
 * @date 2025/3/8 16:38
 * @description: 客户端动态代理的实现
 */
@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    private String host;
    private int port;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes()).build();

        RpcResponse response = IOClient.sendRequest(host,port,request);

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
