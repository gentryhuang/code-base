package com.code.aop.annotation.dao;

import com.code.aop.annotation.annotation.CacheConfig;
import com.code.aop.annotation.annotation.CacheEnable;
import com.code.aop.annotation.annotation.CacheEvict;
import com.code.aop.annotation.constant.CacheKey;
import com.code.aop.annotation.dao.mapper.UserMapper;
import com.code.aop.annotation.domain.UserDO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * UserDAO
 *
 * @author <a href="mailto:libao.huang@yunhutech.com">shunhua</a>
 * @since 2019/10/31
 * <p>
 * desc：
 */
@Repository
@CacheConfig(directory = "userDO")
public class UserDAO {
    @Resource
    private UserMapper userMapper;

    public int insert(UserDO userDO) {
        return userMapper.insert(userDO);
    }


    @CacheEvict(cacheEvictKeys = {CacheKey.USER_DO_ID, CacheKey.USER_DO_MOBILE})
    public int updateById(UserDO userDO) {
        return userMapper.updateById(userDO);
    }


    @CacheEnable(getCacheKeyFromParam = CacheKey.USER_ID,
            putCacheComplexKeyFromReturn = {CacheKey.USER_DO_ID, CacheKey.USER_DO_MOBILE},
            clazz = UserDO.class)
    public UserDO selectById(Long userId) {
        return userMapper.selectById(userId);
    }


    @CacheEvict(cacheEvictKeys = {CacheKey.USER_DO_ID,
            CacheKey.USER_DO_MOBILE,
            CacheKey.USER_OLD_MOBILE})
    public int updateMobileById(String oldMobile, UserDO userDO) {
        return userMapper.updateMobileById(userDO);
    }


    @CacheEnable(getCacheKeyFromParam = CacheKey.USER_MOBILE, clazz = UserDO.class)
    public UserDO selectByMobile(String mobile) {
        return userMapper.selectByMobile(mobile);
    }

    public List<UserDO> selectByUserIds(Set<Long> userIds) {
        return userMapper.selectByUserIds(userIds);
    }
}