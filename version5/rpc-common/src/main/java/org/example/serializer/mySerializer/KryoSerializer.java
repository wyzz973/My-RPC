package org.example.serializer.mySerializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.example.exception.SerializeException;
import org.example.pojo.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author sd
 * @date 2025/3/12 16:12
 * @description: 处理Java对象的二进制序列化
 */
public class KryoSerializer implements Serializer{

    private Kryo kryo;

    public KryoSerializer(){
        kryo = new Kryo();
    }

    @Override
    public byte[] serializer(Object obj) {
        if (obj == null){
            throw new IllegalArgumentException("Cannot serialize null object");
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try{
           Output output = new Output(bos);
           kryo.writeObject(output,obj);  // 使用 Kryo 写入对象
           return output.toBytes();
        }catch (Exception e){
            throw new SerializeException("Serialization failed");
        }
    }

    @Override
    public Object deserializer(byte[] bytes, int messageType) throws IOException {
        if (bytes == null || bytes.length == 0){
            throw new IllegalArgumentException("Cannot deserialize null or empty byte array");
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try {
            Input input = new Input(byteArrayInputStream);

            // 根据 messageType 来反序列化不同的类
            Class<?> clazz = getClassForMessageType(messageType);
            return kryo.readObject(input,clazz);
        }catch (Exception e){
            throw new SerializeException("Deserialization failed");
        }
    }

    @Override
    public int getType() {
        return 2;
    }


    private Class<?> getClassForMessageType(int messageType) {
        if (messageType == 1) {
            return User.class;  // 假设我们在此反序列化成 User 类
        } else {
            throw new SerializeException("Unknown message type: " + messageType);
        }
    }

    @Override
    public String toString() {
        return "Kryo";
    }
}
