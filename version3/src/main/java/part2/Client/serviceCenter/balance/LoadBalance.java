package part2.Client.serviceCenter.balance;

import java.util.List;

/**
 * @author sd
 * @date 2025/3/10 21:22
 * @description: 实现负载均衡算法的接口
 */
public interface LoadBalance {

    //负责实现具体算法，返回分配的地址
    String balance(List<String> addressList);

    //添加节点
    void addNode(String node);

    //删除节点
    void delNode(String node);
}
