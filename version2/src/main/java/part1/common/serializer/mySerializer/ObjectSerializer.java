package part1.common.serializer.mySerializer;

import java.io.*;

/**
 * @author sd
 * @date 2025/3/10 15:29
 * @description:
 */
public class ObjectSerializer implements Serializer{


    //利用Java io 对象 -》字节数组

    /**
     * 目的：将 Java 对象转换为字节数组，以便网络传输或存储。
     * 步骤说明：
     * ByteArrayOutputStream 用于在内存中创建一个缓冲区，保存写入的字节数据。
     * ObjectOutputStream 包装 ByteArrayOutputStream，负责将对象转换成字节流格式。
     * 调用 oos.writeObject(obj) 将传入的对象写入流中。
     * oos.flush() 确保所有数据从缓冲区刷新到底层流中。
     * 最后调用 bos.toByteArray() 获取整个序列化后的字节数组。
     *
     * @param obj
     * @return
     */
    @Override
    public byte[] serializer(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            //是一个对象输出流，用于将 Java 对象序列化为字节流，并将其连接到bos上
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            //刷新 ObjectOutputStream，确保所有缓冲区中的数据都被写入到底层流中。
            oos.flush();
            //将bos其内部缓冲区中的数据转换为字节数组
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }


    //字节数组 -》对象

    /**
     * 目的：将接收到的字节数组还原为 Java 对象。
     * 步骤说明：
     * ByteArrayInputStream 根据传入的字节数组创建一个输入流。
     * ObjectInputStream 包装 ByteArrayInputStream，负责将字节流转换回 Java 对象。
     * 调用 ois.readObject() 读取流中的数据，并将其还原为对象。
     * 捕获 IOException 和 ClassNotFoundException 异常，这两种异常分别对应 I/O 错误和无法加载类的情况。
     *
     * @param bytes
     * @param messageType
     * @return
     */
    @Override
    public Object deserializer(byte[] bytes, int messageType){
        Object obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try{
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return obj;
    }


    //0 代表Java 原生序列器
    @Override
    public int getType() {
        return 0;
    }
}
