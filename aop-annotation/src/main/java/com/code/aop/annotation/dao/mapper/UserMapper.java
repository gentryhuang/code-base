package com.code.aop.annotation.dao.mapper;

import org.apache.ibatis.annotations.Param;
import com.code.aop.annotation.domain.UserDO;

import java.util.List;
import java.util.Set;


public interface UserMapper {

    /**
     * 插入单条数据库记录
     *
     * @param userDO 记录
     * @return 条数
     */
    int insert(UserDO userDO);

    /**
     * 根据主键来更新数据库记录
     *
     * @param userDO 更新数据
     * @return 更新条数
     */
    int updateById(UserDO userDO);

    /**
     * 修改手机号
     *
     * @param userDO
     * @return
     */
    int updateMobileById(UserDO userDO);

    /**
     * 根据主键获取一条数据库记录
     *
     * @param userId 用户id
     * @return 记录
     */
    UserDO selectById(@Param("id") Long userId);


    /**
     * 根据手机号查询用户信息 （备份表处理）
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    UserDO selectByMobile(String mobile);

    /**
     * 根据用户id集合查询
     *
     * @param userIds 用户id集合
     * @return 用户信息
     */
    List<UserDO> selectByUserIds(@Param("userIds") Set<Long> userIds);

}