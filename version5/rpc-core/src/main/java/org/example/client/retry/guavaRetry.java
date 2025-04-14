package org.example.client.retry;

import com.github.rholder.retry.*;
import org.example.client.cache.ServiceCache;
import org.example.client.circuitBreaker.CircuitBreaker;
import org.example.client.circuitBreaker.CircuitBreakerState;
import org.example.client.rpcClient.RpcClient;
import org.example.message.RpcRequest;
import org.example.message.RpcResponse;


import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author sd
 * @date 2025/3/10 23:06
 * @description: 实现重试机制
 */
public class guavaRetry {
    //用来发送RPC 请求

    private RpcClient rpcClient;

    ServiceCache cache;

    public RpcResponse sendServiceWithRetry(RpcRequest request, RpcClient rpcClient, CircuitBreaker circuitBreaker) {
        this.rpcClient = rpcClient;
        Retryer<RpcResponse> retryer;
        if (circuitBreaker.getState() == CircuitBreakerState.HALF_OPEN) {
            retryer = RetryerBuilder.<RpcResponse>newBuilder()
                    .retryIfException()
                    .retryIfResult(response -> response == null || response.getCode() == 500)
                    .withStopStrategy(StopStrategies.stopAfterAttempt(1)) // HALF_OPEN 状态下只允许 1 次重试
                    .build();
        } else {
            retryer = RetryerBuilder.<RpcResponse>newBuilder()
                    //无论出现什么异常，都进行重试
                    .retryIfException()
                    //返回结果为 error时进行重试
                    .retryIfResult(response -> response == null || Objects.equals(response.getCode(), 500))
                    //重试等待策略：等待 2s 后再进行重试
                    .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                    //重试停止策略：重试达到 3 次
                    .withStopStrategy(StopStrategies.stopAfterAttempt(2))
                    .withRetryListener(new RetryListener() {
                        @Override
                        public <V> void onRetry(Attempt<V> attempt) {
                            System.out.println("RetryListener: 第" + attempt.getAttemptNumber() + "次调用");
                            circuitBreaker.recordFailure();
                        }
                    })
                    .build();
        }

        try {
            RpcResponse response = retryer.call(() -> rpcClient.sendRequest(request));

            if (response == null || Objects.equals(response.getCode(), 500)) {
                return RpcResponse.fail();
            }

            circuitBreaker.recordSuccess(); // 成功时才记录
            return response;
            //TODO 删除无用服务器
//            return retryer.call(() -> {
//                RpcResponse response = rpcClient.sendRequest(request);
//                if (response == null){
//                    System.err.println("检测到无效服务器，剔除：" + request.getInterfaceName());
//                    serviceCenter.getServiceCache().delete(request.getInterfaceName(), host + ":" + port);
//                }
//            });
        } catch (ExecutionException | RetryException e) {
            circuitBreaker.recordFailure();
            e.printStackTrace();
        }
        return RpcResponse.fail();
    }
}
