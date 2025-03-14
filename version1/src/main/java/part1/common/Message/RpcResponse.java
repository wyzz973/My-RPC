package part1.common.Message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sd
 * @date 2025/3/8 16:26
 * @description: 定义返回信息格式RpcResponse（类似http格式）
 */
@Data
@Builder
public class RpcResponse implements Serializable {
    //状态码
    private int code;
    //状态信息
    private String message;
    //具体数据
    private Object data;

    //构造成功信息
    public static RpcResponse sussess(Object data){
        return RpcResponse.builder().code(200).data(data).build();
    }

    //构造失败信息
    public static RpcResponse fail(){
        return RpcResponse.builder().code(500).message("服务器发生错误!!!!").build();
    }

}
