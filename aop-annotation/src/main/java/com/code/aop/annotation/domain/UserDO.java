package com.code.aop.annotation.domain;

import lombok.Data;

/**
 * UserDO
 *
 * @author <a href="mailto:libao.huang@yunhutech.com">shunhua</a>
 * @since 2019/10/31
 * <p>
 * desc：
 */
@Data
public class UserDO {
    /**
     * id
     */
    private Long id;
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 真实姓名
     */
    private String name;


}
