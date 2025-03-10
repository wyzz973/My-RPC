package part2.common.serializer.mySerializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import part2.common.Message.RpcRequest;
import part2.common.Message.RpcResponse;

import java.io.IOException;

/**
 * @author sd
 * @date 2025/3/10 15:47
 * @description: JsonSerializer定义Serializer接口
 */
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serializer(Object obj) {
        byte[] bytes = JSONObject.toJSONBytes(obj);
        return bytes;
    }

    @Override
    public Object deserializer(byte[] bytes, int messageType) throws IOException {
        Object obj = null;
        // 传输的消息分为request与response
        switch (messageType){
            case 0:
                RpcRequest request = JSON.parseObject(bytes, RpcRequest.class);

                /**
                 * RpcRequest 可能包含方法参数 params，
                 * 但 JSON.parseObject 解析出来的 params
                 * 可能是 JSONObject 而不是原始 Java 对象，因此需要转换：
                 */

                Object[] objects = new Object[request.getParams().length];
                // 把json字串转化成对应的对象， fastjson可以读出基本数据类型，不用转化
                // 对转换后的request中的params属性逐个进行类型判断
                for (int i = 0; i < objects.length; i++) {
                    Class<?> paramsType = request.getParamsType()[i];
                    //判断每个对象类型是否和paramsTypes中的一致
                    if (!paramsType.isAssignableFrom(request.getParams()[i].getClass())){
                        //如果不一致，就行进行类型转换
                        objects[i] = JSONObject.toJavaObject((JSONObject) request.getParams()[i],request.getParamsType()[i]);
                    }else {
                        //如果一致就直接赋给objects[i]
                        objects[i] = request.getParams()[i];
                    }
                }
                request.setParams(objects);
                obj = request;
                break;
            case 1:
                RpcResponse response = JSON.parseObject(bytes, RpcResponse.class);
                Class<?> dataType = response.getDataType();

                //判断转化后的response对象中的data的类型是否正确
                if (! dataType.isAssignableFrom(response.getData().getClass())){
                    response.setData(JSONObject.toJavaObject((JSONObject) response.getData(),dataType));
                }
                obj = response;
                break;
            default:
                System.out.println("暂时不支持此种消息");
                throw new RuntimeException();
        }
        return obj;

    }

    @Override
    public int getType() {
        return 1;
    }
}
