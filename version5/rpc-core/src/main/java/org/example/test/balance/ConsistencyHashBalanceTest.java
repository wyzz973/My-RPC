package org.example.test.balance;

import org.example.client.serviceCenter.balance.impl.ConsistencyHashBalance;

import java.util.*;

public class ConsistencyHashBalanceTest {
    public static void main(String[] args) {
        // 创建一致性哈希负载均衡实例
        ConsistencyHashBalance hashBalance = new ConsistencyHashBalance();
        
        // 服务器列表
        List<String> servers = new ArrayList<>(Arrays.asList("192.168.1.1", "192.168.1.2", "192.168.1.3"));
        hashBalance.init(servers);

        // 1️⃣ **基本功能测试** - 检查特定节点分配
        System.out.println("===== 基本功能测试 =====");
        String node1 = "client-001";
        String server1 = hashBalance.getServer(node1, servers);
        System.out.println("客户端 [" + node1 + "] 被分配到服务器 [" + server1 + "]");

        String node2 = "client-002";
        String server2 = hashBalance.getServer(node2, servers);
        System.out.println("客户端 [" + node2 + "] 被分配到服务器 [" + server2 + "]");

        // 2️⃣ **负载均衡分布测试** - 统计多个请求的服务器分配
        System.out.println("\n===== 负载均衡分布测试 =====");
        Map<String, Integer> serverLoad = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            String client = "client-" + i;
            String assignedServer = hashBalance.getServer(client, servers);
            serverLoad.put(assignedServer, serverLoad.getOrDefault(assignedServer, 0) + 1);
        }

        // 打印每个服务器分配的请求数量
        for (Map.Entry<String, Integer> entry : serverLoad.entrySet()) {
            System.out.println("服务器 [" + entry.getKey() + "] 处理请求数: " + entry.getValue());
        }

        // 3️⃣ **添加节点测试** - 新增一个服务器
        System.out.println("\n===== 添加服务器测试 =====");
        String newServer = "192.168.1.4";
        hashBalance.addNode(newServer);
        servers.add(newServer);
        System.out.println("新增服务器 [" + newServer + "] 后，重新分配:");

        // 重新统计请求分布
        serverLoad.clear();
        for (int i = 0; i < 1000; i++) {
            String client = "client-" + i;
            String assignedServer = hashBalance.getServer(client, servers);
            serverLoad.put(assignedServer, serverLoad.getOrDefault(assignedServer, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : serverLoad.entrySet()) {
            System.out.println("服务器 [" + entry.getKey() + "] 处理请求数: " + entry.getValue());
        }

        // 4️⃣ **删除节点测试** - 移除一个服务器
        System.out.println("\n===== 删除服务器测试 =====");
        String removeServer = "192.168.1.2";
        hashBalance.delNode(removeServer);
        servers.remove(removeServer);
        System.out.println("移除服务器 [" + removeServer + "] 后，重新分配:");

        // 重新统计请求分布
        serverLoad.clear();
        for (int i = 0; i < 1000; i++) {
            String client = "client-" + i;
            String assignedServer = hashBalance.getServer(client, servers);
            serverLoad.put(assignedServer, serverLoad.getOrDefault(assignedServer, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : serverLoad.entrySet()) {
            System.out.println("服务器 [" + entry.getKey() + "] 处理请求数: " + entry.getValue());
        }
    }
}
