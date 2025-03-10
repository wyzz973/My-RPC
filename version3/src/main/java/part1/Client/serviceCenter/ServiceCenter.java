package part1.Client.serviceCenter;

import java.net.InetSocketAddress;

/**
 * @author sd
 * @date 2025/3/9 22:15
 * @description: 服务中心接口
 */
public interface ServiceCenter {
    //  查询：根据服务名查找地址
    InetSocketAddress serviceDiscovery(String serviceName);
}
