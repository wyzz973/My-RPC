package part2.common.serializer.myCode;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import part2.common.Message.MessageType;
import part2.common.serializer.mySerializer.Serializer;

import java.util.List;

/**
 * @author sd
 * @date 2025/3/10 16:47
 * @description: 自定义解码器
 */
public class MyDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //1.读取消息类型
        short messageType = byteBuf.readShort();

        // 现在还只支持request与response请求
        if (messageType != MessageType.REQUEST.getCode() &&
            messageType != MessageType.RESPONSE.getCode()
        ){
            System.out.println("暂不支持此种数据");
            return;
        }

        //2.读取序列化的方式&类型
        short serializerType = byteBuf.readShort();

        Serializer serializer = Serializer.getSerializerByCode(serializerType);

        if (serializer == null){
            throw new RuntimeException("不存在对应的序列化器");
        }
        //3.读取序列化数组长度
        int length = byteBuf.readInt();

        //4.读取序列化数组
        byte[] bytes = new byte[length];

        byteBuf.readBytes(bytes);

        Object deserialize = serializer.deserializer(bytes,messageType);

        list.add(deserialize);

    }
}
