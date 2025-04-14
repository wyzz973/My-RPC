package part1.Client.circuitBreaker;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sd
 * @date 2025/3/11 20:55
 * @description: 熔断器
 */
public class CircuitBreaker {
    //熔断器初始状态默认关闭
    @Getter
    private CircuitBreakerState state = CircuitBreakerState.CLOSED;

    //失败请求计数
    private AtomicInteger failureCount = new AtomicInteger(0);

    //成功请求计数
    private AtomicInteger successCount = new AtomicInteger(0);

    //请求总数
    private AtomicInteger requestCount = new AtomicInteger(0);

    //失败阈值
    private final int failureThreshold;

    //半开状态下的成功率阈值
    private final double halfOpenSuccessRate;

    //重置时间周期
    private final long resetTimePeriod;

    //最后一次失败的时间
    private long lastFailureTime = 0;

    public CircuitBreaker(int failureThreshold, double halfOpenSuccessRate, long resetTimePeriod) {
        this.failureThreshold = failureThreshold;
        this.halfOpenSuccessRate = halfOpenSuccessRate;
        this.resetTimePeriod = resetTimePeriod;
    }

    //初始化参数


    //根据当前熔断器状态判断是否允许请求
    public synchronized boolean allowRequest(){
        long currentTime = System.currentTimeMillis();
        System.out.println("熔断swtich之前!!!!!!!+failureNum=="+failureCount);

        switch (state){
            //熔断器开启时
            case OPEN:
                if (currentTime - lastFailureTime > resetTimePeriod){
                    state = CircuitBreakerState.HALF_OPEN;
                    resetCount();
                    return true;
                }
                return false;
            case HALF_OPEN:
                requestCount.incrementAndGet();
                return true;
            case CLOSED:
            default:
                return true;
        }
    }

    //记录一次成功的请求
    public synchronized void recordSuccess(){
        if (state == CircuitBreakerState.HALF_OPEN){
            successCount.incrementAndGet();
            if (requestCount.get() >= 10 && successCount.get() >= halfOpenSuccessRate * requestCount.get()) {
                state = CircuitBreakerState.CLOSED;
                resetCount();
            }
        }else {
            resetCount();   //不是半开状态，重置计数
        }
    }

    //记录一次失败的请求
    public synchronized void recordFailure(){
        failureCount.incrementAndGet();
        lastFailureTime = System.currentTimeMillis();

        if (System.currentTimeMillis() - lastFailureTime > resetTimePeriod){
            resetCount(); // 超时后清理失败计数
        }

        if (state == CircuitBreakerState.HALF_OPEN){
            state = CircuitBreakerState.OPEN;
        }else if(failureCount.get() > failureThreshold){
            state = CircuitBreakerState.OPEN;  //失败超过阈值，切换成打开状态
        }
    }


    private void resetCount() {
        failureCount.set(0);
        successCount.set(0);
        requestCount.set(0);
    }


}


