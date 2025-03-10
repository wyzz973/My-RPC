package part1.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author sd
 * @date 2025/3/8 16:59
 * @description: 实体对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {

    // 客户端和服务端共有的
    private Integer id;
    private String userName;
    private Boolean sex;
}
