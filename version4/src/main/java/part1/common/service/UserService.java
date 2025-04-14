package part1.common.service;

import part1.common.pojo.User;

/**
 * @author sd
 * @date 2025/3/8 17:02
 * @description: 服务接口
 */
public interface UserService {
    User getUserByUserId(Integer i);

    Integer insertUserId(User user);
}
