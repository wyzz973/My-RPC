package part2.Server.serviceRegister;

import java.net.InetSocketAddress;

/**
 * @author sd
 * @date 2025/3/9 22:30
 * @description: 服务注册接口
 */
public interface ServiceRegister {
    //  注册：保存服务与地址。
    void register(String serviceName, InetSocketAddress serviceAddress);
}
