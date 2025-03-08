package part1.Server.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author sd
 * @date 2025/3/8 17:20
 * @description: 因为一个服务器会有多个服务，所以需要设置一个本地服务存放器serviceProvider存放服务
 * 在接收到服务端的request信息之后，我们在本地服务存放器找到需要的服务，通过反射调用方法，得到结果并返回
 */
public class ServiceProvider {
    //集合中存放服务的实例
    private Map<String, Object> interfaceProvider;

    public ServiceProvider(){
        this.interfaceProvider = new HashMap<>();
    }
    //本地注册服务

    public void provideServiceInterface(Object service){
        Class<?>[] interfaceName = service.getClass().getInterfaces();

        for (Class<?> aClass : interfaceName) {
            interfaceProvider.put(aClass.getName(),service);
        }
    }

    //获取服务实例
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }

}
