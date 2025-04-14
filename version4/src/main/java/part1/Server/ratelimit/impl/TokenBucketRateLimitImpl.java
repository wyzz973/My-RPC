package part1.Server.ratelimit.impl;

import org.checkerframework.checker.units.qual.C;
import part1.Server.ratelimit.RateLimit;

/**
 * @author sd
 * @date 2025/3/11 20:13
 * @description: 令牌桶算法限流
 */
public class TokenBucketRateLimitImpl implements RateLimit {
    //令牌产生速率(单位 ms)
    private static int RATE;
    //桶容量
    private static int CAPACITY;

    //当前桶容量
    private volatile int curCapcity;

    //上次请求的时间戳
    private volatile long timeStamp = System.currentTimeMillis();

    public TokenBucketRateLimitImpl(int rate, int capacity) {
        RATE = rate;
        CAPACITY = capacity;
        curCapcity = capacity;
    }

    @Override
    public synchronized boolean getToken() {
        //如果桶内还有令牌，直接消费一个令牌并返回true
        if (curCapcity > 0) {
            curCapcity--;
            return true;
        }


        //如果桶里没有令牌，开始计算生成令牌的情况
        long current = System.currentTimeMillis();

        //判断自上次获取令牌以来是否已经过去了足够时间
        if (current - timeStamp >= RATE) {

            //计算这段时间间隔中生成的令牌，如果>2,桶容量加上（计算的令牌-1）
            //如果==1，就不做操作（因为这一次操作要消耗一个令牌）
            if ((current - timeStamp) / RATE >= 2) {
                curCapcity += (int) (current - timeStamp) / RATE - 1;
            }

            if (curCapcity > CAPACITY){
                curCapcity = CAPACITY;
            }
            //刷新时间戳为本次请求
            timeStamp = current;
            return true;
        }


        return false;
    }
}
