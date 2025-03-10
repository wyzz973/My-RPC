package part1.Client.serviceCenter;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import part1.Client.cache.ServiceCache;
import part1.Client.serviceCenter.ZkWatcher.watchZK;
import part1.Client.serviceCenter.balance.impl.ConsistencyHashBalance;

import java.net.InetSocketAddress;
import java.util.List;


/**
 * @author sd
 * @date 2025/3/9 22:17
 * @description: 实现向注册中心中查询 服务地址的类 是实现ServiceCenter接口的ZKServiceCenter类
 */
public class ZKServiceCenter implements ServiceCenter {
    // curator 提供的zookeeper客户端
    private CuratorFramework client;

    //zookeeper根路径节点
    private static final String ROOT_PATH = "MyRPC";

    private ServiceCache serviceCache;

    //负责zookeeper客户端的初始化，并与zookeeper服务端进行连接
    public ZKServiceCenter() {
        // 指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);

        // zookeeper的地址固定，不管是服务提供者还是，消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系，
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20倍
        // 使用心跳监听状态

        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(policy)
                .namespace(ROOT_PATH)
                .build();
        this.client.start();
        System.out.println("zookeeper 连接成功");
        //初始化本地缓存
        serviceCache = new ServiceCache();
        //加入zookeeper事件监听器
        watchZK watcher = new watchZK(client,serviceCache);
        //监听启动
        watcher.watchToUpadte(ROOT_PATH);



    }

    //根据服务名（接口名）返回地址
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            //先从本地缓存中找
            List<String> serviceList = serviceCache.getServiceName(serviceName);
            if (serviceList == null) {
                serviceList = client.getChildren().forPath("/" + serviceName);
            }

            // 这里默认用的第一个，后面加负载均衡

            //new 负载均衡!!!!!!!
            String string = new ConsistencyHashBalance().balance(serviceList);
            return parseAddress(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 地址 -> XXX.XXX.XXX.XXX:port 字符串
    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() + ":" + serverAddress.getPort();
    }

    // 字符串解析为地址
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }

}
