package part2.Client;

/**
 * @author sd
 * @date 2025/3/10 21:03
 * @description: 测试
 */
import part2.Client.proxy.ClientProxy;
import part2.common.pojo.User;
import part2.common.service.UserService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestClient1 {
    public static void main(String[] args) {
        // 线程池，模拟 50 个并发请求
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        ClientProxy clientProxy = new ClientProxy();
        UserService proxy = clientProxy.getProxy(UserService.class);

        long startTime = System.currentTimeMillis(); // 记录开始时间

        for (int i = 0; i < threadCount; i++) {
            final int userId = i + 1;
            executorService.submit(() -> {
                try {
                    long threadStart = System.nanoTime(); // 记录单个请求开始时间

                    User user = proxy.getUserByUserId(userId);

                    long threadEnd = System.nanoTime(); // 记录单个请求结束时间
                    double elapsedTime = (threadEnd - threadStart) / 1_000_000.0; // 转换为 ms
                    System.out.println("线程 " + Thread.currentThread().getId() + " 请求用户 " + userId +
                            " 响应时间: " + elapsedTime + " ms, user=" + user);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await(); // 等待所有请求完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis(); // 记录结束时间
        double totalTime = (endTime - startTime) / 1000.0; // 转换为秒
        double qps = threadCount / totalTime; // 计算 QPS

        System.out.println("\n=== 测试完成 ===");
        System.out.println("总耗时: " + totalTime + " 秒");
        System.out.println("QPS: " + qps);

        executorService.shutdown();
    }
}

