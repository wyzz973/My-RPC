package org.example.consumer;

import org.example.client.proxy.ClientProxy;
import org.example.pojo.User;
import org.example.service.UserService;

/**
 * @author sd
 * @date 2025/3/8 16:31
 * @description: 客户端
 */
public class ConsumerTest {
    public static void main(String[] args) throws InterruptedException {
//        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 9999,0);
        ClientProxy clientProxy = new ClientProxy();
        UserService proxy = clientProxy.getProxy(UserService.class);
//        for (int i = 0; i < 100; i++) {
////            Thread.sleep();
//            User user = proxy.getUserByUserId(i);
//            System.out.println("从服务端得到的user="+user.toString());
//        }

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    try {
                        User user = proxy.getUserByUserId(j);
                        System.out.println(Thread.currentThread().getName() + " 从服务端得到的 user=" + user);
                    }catch (Exception e){
                        System.out.println(Thread.currentThread().getName() + " 发生错误：" + e.getMessage());
                    }
                }
            }).start();
        }


        User user1 = User.builder().id(973).userName("sd").sex(true).build();

        Integer id = proxy.insertUserId(user1);
        System.out.println("向服务端插入user的id"+id);


    }
}
