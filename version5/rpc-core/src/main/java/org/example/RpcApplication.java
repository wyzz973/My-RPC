package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.config.RpcConfig;
import org.example.config.RpcConstant;
import org.example.util.ConfigUtil;

/**
 * @author sd
 * @date 2025/3/12 15:40
 * @description: 维护全局对象
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfigInstance;

    public static void initialize(RpcConfig customRpcConfig){
        rpcConfigInstance = customRpcConfig;
        log.info("RPC 框架初始化，配置 = {}", customRpcConfig);
    }

    public static void initialize(){
        RpcConfig customRpcConfig;
        try{
            customRpcConfig = ConfigUtil.loadConfig(RpcConfig.class, RpcConstant.CONFIG_FILE_PREFIX);
            log.info("成功加载配置文件，配置文件名称 = {}", RpcConstant.CONFIG_FILE_PREFIX); // 添加成功加载的日志
        }catch (Exception e){
            // 配置加载失败，使用默认配置
            customRpcConfig = new RpcConfig();
            log.warn("配置加载失败，使用默认配置",e);
        }
        initialize(customRpcConfig);
    }

    /**
     * 这个方法保证 RpcConfig 只被初始化一次：
     * 第一次访问时：
     * 如果 rpcConfigInstance == null，则进入 synchronized 块。
     * 在 synchronized 块中再次检查 rpcConfigInstance 是否为 null（双重检查锁）。
     * 如果仍然是 null，调用 initialze() 进行初始化。
     * 后续访问时：
     * 直接返回 rpcConfigInstance，避免不必要的同步锁，提高性能。
     * @return
     */
    public static RpcConfig getRpcConfigInstance() {
        if (rpcConfigInstance == null){
            synchronized (RpcApplication.class){
                if (rpcConfigInstance == null){
                    initialize();
                }
            }
        }
        return rpcConfigInstance;
    }
}
