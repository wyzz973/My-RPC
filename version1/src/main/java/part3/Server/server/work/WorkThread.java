package part3.Server.server.work;


import lombok.AllArgsConstructor;
import part3.Server.provider.ServiceProvider;
import part3.common.Message.RpcRequest;
import part3.common.Message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author sd
 * @date 2025/3/8 17:18
 * @description: WorkThread类负责启动线程和客户端进行数据传输
 * WorkThread类中的getResponse方法负责解析收到的request信息，
 * 寻找服务进行调用并返回结果  因为一个服务器会有多个服务，
 * 所以需要设置一个本地服务存放器serviceProvider存放服务
 * 在接收到服务端的request信息之后，
 * 我们在本地服务存放器找到需要的服务，
 * 通过反射调用方法，得到结果并返回
 */
@AllArgsConstructor
public class WorkThread implements Runnable{
    private Socket socket;
    private ServiceProvider serviceProvider;

    @Override
    public void run() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            //读取客户端传过来的request
            RpcRequest request = (RpcRequest) objectInputStream.readObject();
            //反射调用服务方法获取返回值
            RpcResponse response = geResponse(request);

            //向客户端写入response
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private RpcResponse geResponse(RpcRequest request) {
        //得到服务名
        String interfaceName = request.getInterfaceName();
        //得到服务端相应服务实现类
        Object service = serviceProvider.getService(interfaceName);
        //反射调用方法
        Method method;

        try {
            method = service.getClass().getMethod(request.getMethodName(),request.getParamsType());
            Object invoke = method.invoke(service,request.getParams());
            return RpcResponse.sussess(invoke);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RpcResponse.fail();
        }
    }
}
