package com.code.generic.method;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author shunhua
 * @date 2019-10-22
 */
@Data
@AllArgsConstructor
public class User {
    /**
     * 名称
     */
    private String name;
    /**
     * 年龄
     */
    private Integer age;
}
