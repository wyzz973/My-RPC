package part2.common.Message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author sd
 * @date 2025/3/8 16:21
 * @description: 定义请求信息格式RpcRequest
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    //服务类名，客户端只知道接口
    private String interfaceName;
    //调用的方法名
    private String methodName;
    //参数列表
    private Object[] params;
    //参数类型
    private Class<?>[] paramsType;
}
