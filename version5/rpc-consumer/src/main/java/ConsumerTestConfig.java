import org.example.config.RpcConfig;
import org.example.util.ConfigUtil;

/**
 * @author sd
 * @date 2025/3/12 15:52
 * @description: TEST
 */
public class ConsumerTestConfig {
    public static void main(String[] args) {
        RpcConfig rpcConfig = ConfigUtil.loadConfig(RpcConfig.class,"rpc");
        System.out.println(rpcConfig);
    }
}
