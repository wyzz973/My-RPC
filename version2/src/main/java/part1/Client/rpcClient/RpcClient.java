package part1.Client.rpcClient;


import part1.common.Message.RpcRequest;
import part1.common.Message.RpcResponse;

/**
 * @author sd
 * @date 2025/3/9 19:36
 * @description: 定义底层通信的方法
 */
public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
