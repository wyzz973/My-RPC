package part2.common.serializer.myCode;

/**
 * @author sd
 * @date 2025/3/10 15:18
 * @description: 自定义编码器
 */

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import part2.common.Message.MessageType;
import part2.common.Message.RpcRequest;
import part2.common.Message.RpcResponse;
import part2.common.serializer.mySerializer.Serializer;


/**
 *   依次按照自定义的消息格式写入，传入的数据为request或者response
 *   需要持有一个serialize器，负责将传入的对象序列化成字节数组
 */
@AllArgsConstructor
public class MyEncoder extends MessageToByteEncoder {
    private Serializer serializer;
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        System.out.println(object.getClass());
        //1.写入消息类型
        if (object instanceof RpcRequest){
            byteBuf.writeShort(MessageType.REQUEST.getCode());
        } else if (object instanceof RpcResponse) {
            byteBuf.writeShort(MessageType.RESPONSE.getCode());
        }
        //2.写入序列化方式
        byteBuf.writeShort(serializer.getType());
        //得到序列化数组
        byte[] serializerBytes = serializer.serializer(object);
        //3.写入长度
        byteBuf.writeInt(serializerBytes.length);
        //4.写入序列化数组
        byteBuf.writeBytes(serializerBytes);

    }
}
