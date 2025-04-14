package org.example.client.serviceCenter.balance.impl;

import org.example.client.serviceCenter.balance.LoadBalance;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author sd
 * @date 2025/3/10 21:43
 * @description: 随机负载均衡
 */
public class RandomLoadBalance implements LoadBalance {
    private final CopyOnWriteArrayList<String> addressList = new CopyOnWriteArrayList<>();
    @Override
    public String balance(List<String> addressList) {
        if (addressList == null || addressList.isEmpty()) {
            throw new IllegalArgumentException("服务列表为空，无法进行负载均衡！");
        }
//        Random random = new Random();
        // 使用 ThreadLocalRandom 替代 Random，提高并发性能
        int choose = ThreadLocalRandom.current().nextInt(addressList.size());
//        int choose = random.nextInt(addressList.size());
        System.out.println("随机负载均衡选择了:" + addressList.get(choose) + "服务器");
        return addressList.get(choose);
    }

    @Override
    public void addNode(String node) {
        if (!addressList.contains(node)) {
            addressList.add(node);
        }
    }

    @Override
    public void delNode(String node) {
        if (addressList.remove(node)) {
            System.out.println("移除服务器节点: " + node);
        } else {
            System.out.println("移除失败，服务器节点不存在: " + node);
        }
    }

    // 返回当前服务器列表
    public List<String> getNodes() {
        return new CopyOnWriteArrayList<>(addressList);
    }
}
