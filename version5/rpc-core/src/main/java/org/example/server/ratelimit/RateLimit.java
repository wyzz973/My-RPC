package org.example.server.ratelimit;

/**
 * @author sd
 * @date 2025/3/11 20:07
 * @description: 定义限流机制
 */
public interface RateLimit {

    //获取访问许可
    boolean getToken();
}
