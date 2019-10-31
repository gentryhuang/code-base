package com.code.aop.annotation.constant;

/**
 * CacheKey
 *
 * @author <a href="mailto:libao.huang@yunhutech.com">shunhua</a>
 * @since 2019/10/31
 * <p>
 * desc：
 */
public enum CacheKey {
    USER_ID("user_id:%s", new String[]{"#userId"}, CacheConstant.CacheExpireTime.EXPIRE_WEEK),
    USER_MOBILE("user_mobile:%s", new String[]{"#mobile"}, CacheConstant.CacheExpireTime.EXPIRE_WEEK),
    USER_OLD_MOBILE("user_mobile:%s", new String[]{"#oldMobile"}, CacheConstant.CacheExpireTime.EXPIRE_WEEK),
    USER_DO_ID(USER_ID.keyExpression, new String[]{"#userDO.id"}, CacheConstant.CacheExpireTime.EXPIRE_WEEK),
    USER_DO_MOBILE(USER_MOBILE.keyExpression, new String[]{"#userDO.mobile"}, CacheConstant.CacheExpireTime.EXPIRE_WEEK),
    USER_ADDRESS("user_address:%s_%s", new String[]{"#userId", "#id"}, CacheConstant.CacheExpireTime.EXPIRE_WEEK),
    USER_ADDRESS_DO(USER_ADDRESS.keyExpression, new String[]{"#userAddressDO.userId", "#userAddressDO.id"}, CacheConstant.CacheExpireTime.EXPIRE_WEEK),
    USER_ADDRESS_DEFAULT("user_address_default:%s_%s", new String[]{"#userId", "#scene"}, CacheConstant.CacheExpireTime.EXPIRE_WEEK),;

    /**
     * key表达式
     */
    private String keyExpression;
    /**
     * 表达式对应的value (缓存key结构：filed： {@link CacheConfig} +":"+  key: String.format(key,keyReplaceValue...))
     */
    private String[] keyReplaceValues;


    /**
     * 过期时间
     */
    private Integer expirationTime;

    CacheKey(String keyExpression, String[] keyReplaceValues, Integer expirationTime) {
        this.keyExpression = keyExpression;
        this.keyReplaceValues = keyReplaceValues;
        this.expirationTime = expirationTime;
    }

    public String getKeyExpression() {
        return keyExpression;
    }

    public String[] getKeyReplaceValues() {
        return keyReplaceValues;
    }

    public Integer getExpirationTime() {
        return expirationTime;
    }
}
