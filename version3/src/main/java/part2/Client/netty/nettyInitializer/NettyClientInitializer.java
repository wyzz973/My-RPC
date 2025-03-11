package part2.Client.netty.nettyInitializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import part2.common.serializer.mySerializer.JsonSerializer;
import part2.Client.netty.handler.NettyClientHandler;
import part2.common.serializer.myCode.MyDecoder;
import part2.common.serializer.myCode.MyEncoder;

/**
 * @author sd
 * @date 2025/3/9 19:42
 * @description: 配置netty对消息的处理机制
 * <p>
 * 指定编码器（将消息转为字节数组），解码器（将字节数组转为消息）
 * <p>
 * 指定消息格式，消息长度，解决沾包问题
 * <p>
 * 什么是沾包问题？
 * <p>
 * netty默认底层通过TCP 进行传输，TCP是面向流的协议，接收方在接收到数据时无法直接得知一条消息的具体字节数，
 * 不知道数据的界限。由于TCP的流量控制机制，发生沾包或拆包，会导致接收的一个包可能会有多条消息或者不足一条消息，
 * 从而会出现接收方少读或者多读导致消息不能读完全的情况发生
 * <p>
 * 在发送消息时，先告诉接收方消息的长度，让接收方读取指定长度的字节，就能避免这个问题
 * <p>
 * 指定对接收的消息的处理handler
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();

        //消息格式 【长度】【消息体】，解决沾包问题
        //粘包/拆包问题是网络传输中常见的问题，因为 TCP 是基于流的协议，发送端和接收端对数据边界没有天然的区分。
        /**
         * LengthFieldBasedFrameDecoder 用于根据消息前面的长度字段来进行拆包：
         * 第一个参数：最大帧长度（Integer.MAX_VALUE），避免超过这个长度会被认为数据错误。
         * 第二个参数：长度字段的起始位置（0），表示长度信息从消息的第 0 个字节开始。
         * 第三个参数：长度字段的长度（4），表示前 4 个字节存储了消息体的长度。
         * 第四个参数：长度调整值（0），对长度值进行额外调整。
         * 第五个参数：剥离字节数（4），即解码时剥离掉前 4 个字节（长度字段本身）。

        pipeline.addLast(
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)
        );

        //计算当前待发送消息的长度，写入到前4个字节中
        /**
         * LengthFieldPrepender 用于在发送消息时，将消息体长度作为前缀写入数据中：
         * 参数 4 表示将消息长度以 4 个字节写入。这样发送方在发出消息时自动添加了消息长度，
         * 接收方的 LengthFieldBasedFrameDecoder 就可以正确地解析消息边界。

        pipeline.addLast(new LengthFieldPrepender(4));

        //使用Java序列化方式，netty的自带的解码编码支持传输这种结构
        pipeline.addLast(new ObjectEncoder());

        //使用了Netty中的ObjectDecoder，它用于将字节流解码为 Java 对象。
        //在ObjectDecoder的构造函数中传入了一个ClassResolver 对象，用于解析类名并加载相应的类。

//        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
//            @Override
//            public Class<?> resolve(String s) throws ClassNotFoundException {
//                return Class.forName(s);
//            }
//        }));
        //lamda
//        pipeline.addLast(new ObjectDecoder(s -> Class.forName(s)));


        // 方法引用
        pipeline.addLast(new ObjectDecoder(Class::forName));

         */


        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new NettyClientHandler());
    }
}























