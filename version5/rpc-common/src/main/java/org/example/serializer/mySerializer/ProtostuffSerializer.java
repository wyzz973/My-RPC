package org.example.serializer.mySerializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.example.exception.SerializeException;
import org.example.pojo.User;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author sd
 * @date 2025/3/12 16:26
 * @description: protostuff序列化
 */
public class ProtostuffSerializer implements Serializer{
    @Override
    public byte[] serializer(Object obj) {
        if (obj == null){
            throw new IllegalArgumentException("Cannot serialize null object");
        }
        // 获取对象的 schema
        Schema schema = RuntimeSchema.getSchema(obj.getClass());

        // 使用 LinkedBuffer 来创建缓冲区（默认大小 1024）

        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        // 序列化对象为字节数组
        byte[] bytes;
        try {
            bytes = ProtobufIOUtil.toByteArray(obj,schema,buffer);
        }finally {
            buffer.clear();
        }
        return bytes;

    }

    @Override
    public Object deserializer(byte[] bytes, int messageType) throws IOException {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Cannot deserialize null or empty byte array");
        }

        // 根据 messageType 来决定反序列化的类，这里假设 `messageType` 是类的标识符
        Class<?> clazz = getClassForMessageType(messageType);

        // 获取对象的 schema
        Schema schema = RuntimeSchema.getSchema(clazz);

        Object obj;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new SerializeException("Deserialization failed due to reflection issues");
        }

        //反序列化字节数组为对象
        ProtobufIOUtil.mergeFrom(bytes,obj,schema);

        return obj;

    }

    @Override
    public int getType() {
        return 4;
    }

    // 用于根据 messageType 获取对应的类
    private Class<?> getClassForMessageType(int messageType) {
        if (messageType == 1) {
            return User.class;  // 假设我们在此反序列化成 User 类
        } else {
            throw new SerializeException("Unknown message type: " + messageType);
        }
    }

    @Override
    public String toString() {
        return "Protostuff";
    }
}
