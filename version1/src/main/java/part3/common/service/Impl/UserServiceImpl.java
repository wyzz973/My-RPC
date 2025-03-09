package part3.common.service.Impl;

import part3.common.pojo.User;
import part3.common.service.UserService;

import java.util.Random;
import java.util.UUID;

/**
 * @author sd
 * @date 2025/3/8 17:03
 * @description: 服务实现类
 */
public class UserServiceImpl implements UserService {

    @Override
    public User getUserByUserId(Integer i) {
        System.out.println("客户端查询了"+i+"的用户");
        // 模拟从数据库中取用户的行为
        Random random = new Random();
        User user = User.builder().userName(UUID.randomUUID().toString())
                .id(i)
                .sex(random.nextBoolean()).build();
        return user;
    }

    @Override
    public Integer insertUserId(User user) {
        System.out.println("插入数据成功"+user.getUserName());
        return user.getId();
    }
}
