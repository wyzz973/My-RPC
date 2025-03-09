package part3.Server.provider;

import part3.Server.serviceRegister.ServiceRegister;
import part3.Server.serviceRegister.impl.ZKServiceRegister;

import javax.imageio.spi.ServiceRegistry;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sd
 * @date 2025/3/8 17:20
 * @description: 因为一个服务器会有多个服务，所以需要设置一个本地服务存放器serviceProvider存放服务
 * 在接收到服务端的request信息之后，我们在本地服务存放器找到需要的服务，通过反射调用方法，得到结果并返回
 */

/**
 * new!!!!
 * 我们添加一个ServiceRegister变量用于注册服务到注册中心
 * 在服务端上线时调用ServiceProvider的provideServiceInterface方法中添加逻辑，
 * 使得本地注册服务时也注册服务到注册中心上
 */
public class ServiceProvider {
    //集合中存放服务的实例
    private Map<String, Object> interfaceProvider;

    private String host;
    private int port;
    //注册服务类
    private ServiceRegister serviceRegister;

    public ServiceProvider(String host,int port){
        this.host = host;
        this.port = port;
        this.interfaceProvider = new HashMap<>();
        this.serviceRegister = new ZKServiceRegister();
    }


    public ServiceProvider(){
        this.interfaceProvider = new HashMap<>();
    }
    //本地注册服务
    public void provideServiceInterface(Object service){
        Class<?>[] interfaceName = service.getClass().getInterfaces();

        for (Class<?> aClass : interfaceName) {
            //本机的映射表
            interfaceProvider.put(aClass.getName(),service);

            //在注册中心注册服务
            serviceRegister.register(aClass.getName(), new InetSocketAddress(host,port));

        }
    }

    //获取服务实例
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }

}
