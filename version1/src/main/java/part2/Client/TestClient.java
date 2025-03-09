package part2.Client;

import part2.Client.proxy.ClientProxy;
import part2.common.pojo.User;
import part2.common.service.UserService;

/**
 * @author sd
 * @date 2025/3/8 16:31
 * @description: 客户端
 */
public class TestClient {
    public static void main(String[] args) {
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 9999,0);

        UserService proxy = clientProxy.getProxy(UserService.class);

        User user = proxy.getUserByUserId(1);

        System.out.println("从服务端得到的user="+user.toString());

        User user1 = User.builder().id(973).userName("sd").sex(true).build();

        Integer id = proxy.insertUserId(user1);
        System.out.println("向服务端插入user的id"+id);


    }
}
