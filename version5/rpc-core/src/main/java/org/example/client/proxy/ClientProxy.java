package org.example.client.proxy;

import org.example.client.circuitBreaker.CircuitBreaker;
import org.example.client.circuitBreaker.CircuitBreakerProvider;
import org.example.client.retry.guavaRetry;
import org.example.client.rpcClient.RpcClient;
import org.example.client.rpcClient.impl.NettyRpcClient;
import org.example.client.serviceCenter.ServiceCenter;
import org.example.client.serviceCenter.ZKServiceCenter;
import org.example.message.RpcRequest;
import org.example.message.RpcResponse;

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

    private ServiceCenter serviceCenter;

    private CircuitBreakerProvider circuitBreakerProvider;

    public ClientProxy() {
        serviceCenter = new ZKServiceCenter();
        rpcClient = new NettyRpcClient(serviceCenter);
        circuitBreakerProvider = new CircuitBreakerProvider();
    }

    //jdk动态代理，每一次代理对象调用方法，都会经过此方法增强（反射获取request对象，socket发送到服务端）
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes()).build();

        //熔断器
        CircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(method.getName());
        //判断熔断器是否允许通过
        if (!circuitBreaker.allowRequest()) {
            //这里可以针对熔断做特殊处理，返回特殊值
            System.out.println("被熔断了！！！！");
            return RpcResponse.fail();
        }


        //IOClient.sendRequest 和服务端进行数据传输
        //   RpcResponse response = rpcClient.sendRequest(request);
        RpcResponse response;
        //为保持幂等性，只对白名单上的服务进行重试
        if (serviceCenter.checkRetry(request.getInterfaceName())) {
            //调用retry框架进行重试操作
            response = new guavaRetry().sendServiceWithRetry(request, rpcClient,circuitBreaker);
            if (response == null) {
                response = RpcResponse.builder().code(500).message("Service unavailable").build();
            }
        } else {
            //只调用一次
            response = rpcClient.sendRequest(request);
        }
        /**
         //记录response的状态，上报给熔断器
         if (response.getCode() == 200){
         circuitBreaker.recordSuccess();
         }
         if (response == null || response.getCode() == 500){
         circuitBreaker.recordFailure();
         }
         */

        if (response != null) {
            if (response.getCode() == 200) {
                circuitBreaker.recordSuccess();
            } else {
                circuitBreaker.recordFailure();
            }
        } else {
            circuitBreaker.recordFailure();
        }


        return response != null ? response.getData() : null;
    }

    /**
     * 生成动态代理对象：
     * Proxy.newProxyInstance 创建一个JDK动态代理对象，该对象实现 clazz 这个接口，并将方法调用交由 ClientProxy 处理。
     * 返回代理对象：
     * 由于 Proxy.newProxyInstance 返回的是 Object，所以需要手动转换为 T 类型。
     *
     * @param clazz
     * @param <T>
     * @return
     */

    public <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }
}
