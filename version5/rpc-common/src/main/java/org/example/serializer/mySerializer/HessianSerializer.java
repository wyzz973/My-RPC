package org.example.serializer.mySerializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.example.exception.SerializeException;
import org.yaml.snakeyaml.serializer.SerializerException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author sd
 * @date 2025/3/12 16:01
 * @description: Hessian dubbo默认序列化方式
 */
public class HessianSerializer implements Serializer{
    @Override
    public byte[] serializer(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            HessianOutput hessianOutput = new HessianOutput(bos);
            hessianOutput.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new SerializeException("Serialization failed");
        }
    }

    @Override
    public Object deserializer(byte[] bytes, int messageType) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try{
            HessianInput hessianInput = new HessianInput(bis);
            return hessianInput.readObject();
        }catch (IOException e){
            throw new SerializeException("Serialization failed");
        }
    }

    @Override
    public int getType() {
        return 3;
    }

    @Override
    public String toString() {
        return "Hessian";
    }
}
