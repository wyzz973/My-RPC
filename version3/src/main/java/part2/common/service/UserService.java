package part2.common.service;

import part2.common.pojo.User;

/**
 * @author sd
 * @date 2025/3/8 17:02
 * @description: 服务接口
 */
public interface UserService {
    User getUserByUserId(Integer i);

    Integer insertUserId(User user);
}
