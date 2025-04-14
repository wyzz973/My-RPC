package org.example.client.serviceCenter.balance.impl;

import org.example.client.serviceCenter.balance.LoadBalance;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sd
 * @date 2025/3/10 21:33
 * @description: 轮询负载均衡
 */
public class RoundLoadBalance implements LoadBalance {
    private final CopyOnWriteArrayList<String> addressList = new CopyOnWriteArrayList<>();
    private AtomicInteger choose = new AtomicInteger(0);

    @Override
    public String balance(List<String> addressList) {
        if (addressList == null || addressList.isEmpty()) {
            throw new IllegalArgumentException("服务列表为空，无法进行负载均衡！");
        }
        int currentChoose = choose.getAndUpdate(i -> (i + 1) % addressList.size());
        System.out.println("轮询负载均衡选择了:" + addressList.get(currentChoose) + "服务器");
        return addressList.get(currentChoose);
    }

    @Override
    public void addNode(String node) {

    }

    @Override
    public void delNode(String node) {

    }
}
