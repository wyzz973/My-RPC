package org.example.test.balance;

import org.example.client.serviceCenter.balance.impl.RandomLoadBalance;

import java.util.Arrays;

public class TestRandomLoadBalance {
    public static void main(String[] args) {
        RandomLoadBalance loadBalance = new RandomLoadBalance();
        
        // 添加服务器节点
        loadBalance.addNode("192.168.1.1");
        loadBalance.addNode("192.168.1.2");
        loadBalance.addNode("192.168.1.3");

        // 测试负载均衡
        for (int i = 0; i < 5; i++) {
            System.out.println("请求被分配到: " + loadBalance.balance(loadBalance.getNodes()));
        }

        // 删除节点
        loadBalance.delNode("192.168.1.2");

        // 测试负载均衡（删除后）
        for (int i = 0; i < 5; i++) {
            System.out.println("请求被分配到: " + loadBalance.balance(loadBalance.getNodes()));
        }
    }
}
