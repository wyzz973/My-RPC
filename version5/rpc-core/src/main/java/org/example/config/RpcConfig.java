package org.example.config;

import lombok.*;
import org.example.client.serviceCenter.balance.impl.ConsistencyHashBalance;
import org.example.serializer.mySerializer.Serializer;
import org.example.server.serviceRegister.impl.ZKServiceRegister;

/**
 * @author sd
 * @date 2025/3/12 15:31
 * @description: 配置顶实现
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcConfig {
    //名称
    private String name = "MyRPC";
    //端口
    private Integer port = 9999;
    //主机名
    private String host = "localhost";
    //版本号
    private String version = "1.0.0";
    //注册中心
    private String registry = new ZKServiceRegister().toString();
    //序列化器
    private String serializer = Serializer.getSerializerByCode(1).toString();
    //负载均衡
    private String loadBalance = new ConsistencyHashBalance().toString();

}
