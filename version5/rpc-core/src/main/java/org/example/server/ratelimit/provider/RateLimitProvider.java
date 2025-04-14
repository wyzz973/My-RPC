package org.example.server.ratelimit.provider;

import org.example.server.ratelimit.RateLimit;
import org.example.server.ratelimit.impl.TokenBucketRateLimitImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sd
 * @date 2025/3/11 20:34
 * @description: 提供速率限制相关服务
 */
public class RateLimitProvider {
    //用于存储每个接口名称与对应的速率限制器实例之间的映射关系。
    private Map<String, RateLimit> rateLimitMap = new HashMap<>();


    public RateLimit getRateLimit(String interfaceName){

        if (!rateLimitMap.containsKey(interfaceName)){
            RateLimit rateLimit = new TokenBucketRateLimitImpl(100,50);

            rateLimitMap.put(interfaceName,rateLimit);

            return rateLimit;
        }

        return rateLimitMap.get(interfaceName);
    }


}
