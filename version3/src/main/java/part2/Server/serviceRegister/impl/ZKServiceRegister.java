package part2.Server.serviceRegister.impl;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import part2.Server.serviceRegister.ServiceRegister;

import java.net.InetSocketAddress;

/**
 * @author sd
 * @date 2025/3/9 22:32
 * @description: 实现ServiceRegister接口的ZKServiceRegister类，提供了注册服务到注册中心的register方法
 */
public class ZKServiceRegister implements ServiceRegister {
    // curator 提供的zookeeper客户端
    private CuratorFramework client;

    //zookeeper根路径节点
    private static final String ROOT_PATH = "MyRPC";

    private static final String RETRY = "CanRetry";

    //负责zookeeper客户端的初始化，并与zookeeper服务端进行连接
    public ZKServiceRegister(){
        // 指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        // zookeeper的地址固定，不管是服务提供者还是，消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系，
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20倍
        // 使用心跳监听状态

        /**
         * 重试策略（ExponentialBackoffRetry）：当连接失败时，会按照指定的时间间隔和次数进行重试，增强连接的稳定性。
         * 连接参数：包括 ZooKeeper 地址、会话超时时间等。
         * 命名空间：设置后，所有操作的路径都自动加上 /MyRPC 前缀，起到逻辑隔离的作用。
         * 启动客户端：调用 client.start() 后就会与 ZooKeeper 服务建立连接。
         */
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181") // ZooKeeper 服务地址
                .sessionTimeoutMs(8000)         // 会话超时时间，与 ZooKeeper 配置相关
                .retryPolicy(policy)             // 使用指数退避重试策略
                .namespace(ROOT_PATH)            // 指定命名空间，每次创建的节点都会在 /MyRPC 下
                .build();
        this.client.start();
        System.out.println("zookeeper 连接成功");
    }

    //注册服务到注册中心
    @Override
    public void register(String serviceName, InetSocketAddress serviceAddress,boolean canRetry) {
        try {
            // serviceName创建成永久节点，服务提供者下线时，不删服务名，只删地址
            // 检查是否存在以服务名称命名的永久节点
            if (client.checkExists().forPath("/" + serviceName) == null){
                // 不存在则创建永久节点，服务下线时不会删除该节点，仅删除具体的地址信息
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/" + serviceName);
            }

            // 路径地址，一个/代表一个节点
            // 构造服务提供者的地址节点，格式为 /服务名/地址（例如：/UserService/127.0.0.1:8080）
            String path = "/" + serviceName + "/" + getServiceAddress(serviceAddress);

            System.out.println("临时节点....:" + path);

            // 临时节点，服务器下线就删除节点
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);

            //如果这个服务是幂等性的，就添加在节点中
            if (canRetry){
                path = "/" + RETRY + "/" + serviceName;
                System.out.println("可以retry的path:" + path);
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("此服务已存在");
        }
    }
    // 地址 -> XXX.XXX.XXX.XXX:port 字符串
    private String getServiceAddress(InetSocketAddress address){
        return address.getHostName() +
                ":" +
                address.getPort();
    }

    private InetSocketAddress parseAddress(String address){
        String[] result = address.split(":");
        return new InetSocketAddress(result[0],Integer.parseInt(result[1]));
    }
}
