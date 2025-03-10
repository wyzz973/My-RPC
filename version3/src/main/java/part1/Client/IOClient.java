package part1.Client;

import part1.common.Message.RpcRequest;
import part1.common.Message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author sd
 * @date 2025/3/8 16:45
 * @description: IOClient类 sendRequest方法负责和服务端进行数据传输
 *              负责底层与服务端的通信，发送request，返回response
 */
public class IOClient {
    public static RpcResponse sendRequest(String host, int port, RpcRequest request){
        try {
            // 1. 创建 Socket 连接到服务端
            Socket socket = new Socket(host, port);
            // 2. 创建对象输出流（发送请求）
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // 3. 创建对象输入流（接收响应）
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            // 4. 发送请求对象到服务端
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            // 5. 等待并读取服务器返回的响应对象
            RpcResponse response = (RpcResponse) objectInputStream.readObject();

            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
