package part1.Server.server;

/**
 * @author sd
 * @date 2025/3/8 17:12
 * @description: 定义服务端Server的接口，指定方法
 */
public interface RpcServer {
    //开启监听
    void start(int port);
    void stop();

}
