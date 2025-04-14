package org.example.client.circuitBreaker;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sd
 * @date 2025/3/11 21:26
 * @description: 提供熔断器服务
 */
public class CircuitBreakerProvider {

    private Map<String,CircuitBreaker> circuitBreakerMap = new HashMap<>();

    //根据服务名获取对应的熔断器
    public synchronized CircuitBreaker getCircuitBreaker(String serviceName){
        CircuitBreaker circuitBreaker;

        if (circuitBreakerMap.containsKey(serviceName)){
            circuitBreaker = circuitBreakerMap.get(serviceName);
        }else {
            System.out.println("serviceName="+serviceName+"创建一个新的熔断器");
            circuitBreaker = new CircuitBreaker(3,0.5,10000);
            circuitBreakerMap.put(serviceName,circuitBreaker);
        }
        return circuitBreaker;
    }

}
