package org.example.client.rpcClient;


import org.example.message.RpcRequest;
import org.example.message.RpcResponse;

/**
 * @author sd
 * @date 2025/3/9 19:36
 * @description: 定义底层通信的方法
 */
public interface RpcClient {
    //定义底层通信的方法
    RpcResponse sendRequest(RpcRequest request);
}
