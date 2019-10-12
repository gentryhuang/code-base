package com.code.cache.jedis.cache;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author shunhua
 * @date 2019-09-29
 */
public interface ICacheService {
    /**
     * 回收redis连接到连接池
     *
     * @param redis
     */
    void returnResource(Jedis redis);

    /**
     * 取得redis的连接,使用完成之后 ,需要把连接返回连接池,不建议自己获取连接池,风险比较大
     *
     * @return
     */
    @Deprecated
    Jedis getResource();

    /**
     * 存储 object
     *
     * @param key
     * @param value
     */
    @Deprecated
    void setObject(String key, Object value);

    /**
     * 存储 object
     *
     * @param key
     * @param value
     * @param expireSecond 过期秒数
     */
    void setObject(String key, Object value, int expireSecond);

    /**
     * 返回obj 请使用getObject
     *
     * @param key
     * @return
     */
    @Deprecated
    Object getObjet(String key);

    /**
     * 返回obj
     *
     * @param key
     * @return
     */
    Object getObject(String key);

    /**
     * 存储 object
     *
     * @param key
     * @param value
     */
    @Deprecated
    void setObjectByZip(String key, Object value);

    /**
     * 存储 object
     *
     * @param key
     * @param value
     * @param expireSecond 过期秒数
     */
    void setObjectByZip(String key, Object value, int expireSecond);

    /**
     * 返回obj
     *
     * @param key
     * @return
     */
    Object getObjectByZip(String key);


    /**
     * 同时设置一个或多个 key-value 对。
     * 如果某个给定 key 已经存在，那么 MSET 会用新值覆盖原来的旧值;
     * 如果这不是你所希望的效果，请考虑使用 MSETNX 命令：它只会在所有给定 key 都不存在的情况下进行设置操作。
     * MSET 是一个原子性(atomic)操作，所有给定 key 都会在同一时间内被设置，某些给定 key 被更新而另一些给定 key 没有改变的情况，不可能发生。
     * @param keyValues
     * @param expireSecond
     * @return
     */
    Boolean mset(Map<String,String> keyValues, int expireSecond);

    /**
     * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在
     * 即使只有一个给定 key 已存在， MSETNX 也会拒绝执行所有给定 key 的设置操作。
     * @param keyValues
     * @param expireSecond
     * @return 当所有 key 都成功设置，返回 1 。如果所有给定 key 都设置失败(至少有一个 key 已经存在)，那么返回 0
     */
    long msetnx(Map<String,String> keyValues,int expireSecond);


    /**
     * 同时设置一个或多个 key-value 对。
     * 如果某个给定 key 已经存在，那么 MSET 会用新值覆盖原来的旧值;
     * 如果这不是你所希望的效果，请考虑使用 MSETNX 命令：它只会在所有给定 key 都不存在的情况下进行设置操作。
     * MSET 是一个原子性(atomic)操作，所有给定 key 都会在同一时间内被设置，某些给定 key 被更新而另一些给定 key 没有改变的情况，不可能发生。
     * @param keyValues
     * @param expireSecond
     * @return
     */
    Boolean msetObj(Map<String,Object> keyValues,int expireSecond);

    /**
     * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在
     * 即使只有一个给定 key 已存在， MSETNX 也会拒绝执行所有给定 key 的设置操作。
     * @param keyValues
     * @param expireSecond
     * @return 当所有 key 都成功设置，返回 1 。如果所有给定 key 都设置失败(至少有一个 key 已经存在)，那么返回 0
     */
    long msetObjnx(Map<String,Object> keyValues,int expireSecond);

    /**
     * 返回多个值
     *
     * @param keys
     * @return
     */
    <T> List<T> mget(byte[]... keys);

    /**
     * 返回多个值
     *
     * @param keys
     * @return
     */
    List<String> mget(String... keys);

    /**
     * 存储string
     *
     * @param key
     * @param value
     */
    @Deprecated
    void set(String key, String value);

    /**
     * 存储string
     *
     * @param key
     * @param value
     * @param expireSecond 过期秒数
     */
    void set(String key, String value, int expireSecond);

    /**
     * 取出string
     *
     * @param key
     * @return
     */
    String get(String key);

    /**
     *返回给定 key 的旧值。 当 key 没有旧值时，也即是， key 不存在时，返回 null 。
     * @param key
     * @param value
     * @return
     */
    String getset(String key,String value);

    /**
     * 删除key
     *
     * @param key
     * @return
     */
    long del(String... key);

    /**
     * 设置摸个hash值
     *
     * @param key
     * @param field
     * @return
     */
    String hget(String key, String field);

    /**
     * 设置hash 的某个值
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    @Deprecated
    Long hset(String key, String field, String value);

    /**
     * 设置hash 的某个值
     *
     * @param key
     * @param field
     * @param value
     * @param expireSecond
     * @return
     */
    Long hset(String key, String field, String value, int expireSecond);

    /**
     * 将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在。
     * 若域 field 已经存在，该操作无效
     * @param key
     * @param field
     * @param value
     * @param expireSecond
     * @return  设置成功，返回 1 。如果给定域已经存在且没有操作被执行，返回 0 。
     */
    Long hsetnx(String key, String field, String value, int expireSecond);

    /**
     * set hash map
     *
     * @param key
     * @param hash
     * @param expireSecond 过期秒数
     */
    void hmset(String key, Map<String, String> hash, int expireSecond);

    /**
     * set hash map
     *
     * @param key
     * @param hash
     */
    @Deprecated
    void hmset(String key, Map<String, String> hash);

    /**
     * 删除hash 中的某个field
     *
     * @param key
     * @param fields
     * @return
     */
    boolean hdel(String key, String... fields);

    /**
     * 返回hash map
     *
     * @param key
     * @return
     */
    Map<String, String> hgetAll(String key);

    /**
     * 返回多个hash value
     *
     * @param key
     * @param fields
     * @return
     */
    List<String> hmget(String key, String... fields);

    /**
     * 返回指定hash的field数量
     * @param key
     * @return
     */
    Long hlen(String key);

    /**
     * 查看哈希表 key 中，给定域 field 是否存在。
     * @param key
     * @param field
     * @return 如果哈希表含有给定域，返回 true 。如果哈希表不含有给定域，或 key 不存在，返回 false 。
     */
    Boolean hexists(String key,String field);

    /**
     * 返回哈希表 key 中的所有域。
     * @param key
     * @return
     */
    Set<String> hkeys(String key);

    /**
     * 在list尾部插入数据
     *
     * @param key
     * @param length list的长度，如果超过，就会除去最早进入的元素
     * @param values
     */
    void rpushLength(String key, long length, String... values);

    /**
     * 在list尾部插入数据
     *
     * @param key
     * @param values
     * @param expireSecond 整个list过期
     */
    void rpush(String key, int expireSecond, String... values);

    /**
     * 将值 value 插入到列表 key 的表尾，当且仅当 key 存在并且是一个列表。
     * 和 RPUSH 命令相反，当 key 不存在时， RPUSHX 命令什么也不做。
     * @param key
     * @param expireSecond
     * @param values
     * @return
     */
    void rpushx(String key,int expireSecond,  String... values);
    /**
     * 在list尾部插入数据,没有有效期
     *
     * @param key
     * @param values
     */
    @Deprecated
    void rpush(String key, String... values);

    /**
     * 返回list里的所有数据
     *
     * @param key
     */
    List<String> lrange(String key, long start, long end);

    /**
     * 返回list里的长度
     *
     * @param key
     */
    long llen(String key);

    /**
     * 返回列表 key 中，下标为 index 的元素。
     * 以 0 表示列表的第一个元素
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     * @param key
     * @param index
     * @return  列表中下标为 index 的元素。如果 index 参数的值不在列表的区间范围内(out of range)，返回 null 。
     */
    String lindex(final String key, final long index);

    /**
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     * 举个例子，执行命令 LTRIM list 0 2 ，表示只保留列表 list 的前三个元素，其余元素全部删除。
     * @param key
     * @param start
     * @param end
     * @return
     */
    boolean ltrim(final String key, final long start, final long end);

    /**
     * 从存于 key 的列表里移除前 count 次出现的值为 value 的元素。 这个 count 参数通过下面几种方式影响这个操作： count > 0: 从头往尾移除值为 value 的元素。 count < 0:
     * 从尾往头移除值为 value 的元素。 count = 0: 移除所有值为 value 的元素。
     *
     * @param key
     * @param count
     * @param value
     */
    long lrem(String key, int count, String value);

    /**
     * 执行原子加1操作
     *
     * @param key
     * @return 返回加1后的值
     */
    long incr(String key);

    /**
     * 执行原子加1操作，可设置key过期时间
     * @param key
     * @param expireSecond
     * @return
     */
    long incr(String key,int expireSecond);

    /**
     * 执行原子减1操作
     *
     * @param key
     * @return 返回减1后的值
     */
    long decr(String key);

    /**
     * 执行原子减1操作，可设置过期时间
     * @param key
     * @param expireSecond
     * @return
     */
    long decr(String key,int expireSecond);

    /**
     * 将 key 所储存的值加上增量 increment
     *
     * @param key
     * @return 加上增量 increment 值
     */
    long incrBy(String key, long increment);

    /**
     * 将 key 所储存的值加上增量 increment，可设置过期时间
     * @param key
     * @param increment
     * @param expireSecond
     * @return
     */
    long incrBy(String key, long increment,int expireSecond);

    /**
     * 将 key 所储存的值减去减量 decrement
     *
     * @param key
     * @return 减去减量 decrement的值
     */
    long decrBy(String key, long decrement);

    /**
     *  将 key 所储存的值减去减量 decrement，可设置过期时间
     * @param key
     * @param decrement
     * @param expireSecond
     * @return
     */
    long decrBy(String key, long decrement,int expireSecond);

    /**
     * 不存在则设置
     *
     * @param key
     * @param value
     * @param expireSecond 过期
     */
    Long setnx(String key, int expireSecond, String value);

    /**
     * 不存在则设置
     *
     * @param key
     * @param value
     */
    @Deprecated
    Long setnx(String key, String value);

    // add by pijiu 20141215 添加有序操作对象方法 start
    /**
     * 有序存储object
     *
     * @param key
     * @param score
     * @param value
     * @param expireSecond
     * @return 1:成功 0:不成功
     */
    int zadd(String key, double score, String value, int expireSecond);

    /**
     * 获取有序存储object的score值
     *
     * @param key
     * @param value
     * @return score值
     */
    double zscore(String key, String value);

    /**
     * 删除对应value值
     *
     * @param key
     * @param value
     * @return 1:成功 0:不成功
     */
    int zrem(String key, String value);

    /**
     * 返回有序集key中，指定区间内的成员
     *
     * @param key
     * @param start
     * @param end
     * @param isAsc 1:按score值递增(从小到大),2:按score值递增(从大到小)
     * @return List<String>
     */
    List<String> zrange(String key, long start, long end, int isAsc);

    /**
     * 返回有序集 key 中成员 member 的排名。
     *
     * @param key
     * @param member
     * @return
     */
    long zrank(String key, String member);

    /**
     * 返回有序集合的成员数
     *
     * @param key
     * @return
     */
    long zcard(String key);

    /**
     * 在给定的索引之内删除所有成员的有序集合
     *
     * @param key
     * @return
     */
    long zremrangebyrank(String key, long start, long stop);


    List<String> hvals(String key);

    /**
     * 从list队尾删除key对应value，返回value
     *
     * @param key
     * @return
     */
    String rpop(String key);

    /**
     * 在list头部插入数据
     *
     * @param key
     * @param expireSecond
     * @param values
     */
    void lpush(String key, int expireSecond, String... values);

    /**
     *将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端。
     * 将 source 弹出的元素插入到列表 destination ，作为 destination 列表的的头元素。
     * 例如：
     *  你有两个列表 source 和 destination ， source 列表有元素 a, b, c ， destination 列表有元素 x, y, z 。
     *  执行 RPOPLPUSH source destination 之后， source 列表包含元素 a, b ， destination 列表包含元素 c, x, y, z
     *  并且元素 c 会被返回给客户端。
     *  如果 source 不存在，值 nil 被返回，并且不执行其他动作。
     *  如果 source 和 destination 相同，则列表中的表尾元素被移动到表头，并返回该元素，可以把这种特殊情况视作列表的旋转(rotation)操作。
     * @param srckey
     * @param dstkey
     * @return
     */
    String rpoplpush(final String srckey, final String dstkey);

    /**
     * 将值 value 插入到列表 key 的表头，当且仅当 key 存在并且是一个列表。
     * 和 LPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做。
     * @param key
     * @param expireSecond
     * @param value
     * @return LPUSHX 命令执行之后，表的长度。
     */
    void lpushx(String key,int expireSecond,  String... value);

    /**
     * 在list头部插入数据,没有有效期
     *
     * @param key
     * @param values
     */
    void lpush(String key, String... values);

    /**
     * 从list头部删除key对应value，返回value
     *
     * @param key
     * @return
     */
    String lpop(String key);

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。 当 key 不是集合类型时，返回一个错误。
     *
     * @param key
     * @param values
     * @return
     */
    long sadd(String key, String... values);

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。 当 key 不是集合类型时，返回一个错误。
     *
     * @param key
     * @param expireSecond
     * @param values
     * @return
     */
    long sadd(String key, int expireSecond, String... values);

    /**
     * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。 当 key 不是集合类型，返回一个错误。
     *
     * @param key
     * @param values
     * @return
     */
    long srem(String key, String... values);

    /**
     * 返回集合 key 中的所有成员。 不存在的 key 被视为空集合。
     *
     * @param key
     * @return
     */
    Set<String> smembers(String key);

    /**
     * 判断 member 元素是否集合 key 的成员。
     * @param key
     * @param member
     * @return
     */
    Boolean sismember(final String key, final String member);

    /**
     * 返回集合key中元素数量
     * @param key
     * @return  key不存在，返回0
     */
    Long scard(final String key);

    /**
     * 移除并返回集合中的一个随机元素
     * @param key
     * @return
     */
    String spop(final String key);

    /**
     * 随机返回集合中的一个元素
     * @param key
     * @return
     */
    String srandmember(final String key);

    /**
     * 判断Key是否存在
     *
     * @param key
     * @return
     */
    boolean exists(byte[] key);

    /**
     * 判断Key是否存在
     *
     * @param key
     * @return
     */
    boolean exists(String key);

    /**
     * Return the all the elements in the sorted set at key with a score between min and max (including elements with
     * score equal to min or max).
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    Set<String> zrangeByScore(final String key, final double min, final double max);

    Set<String> zrevrangeByScore(final String key, final double max, final double min);

    /**
     * BLPOP (and BRPOP) is a blocking list pop primitive. You can see this commands as blocking versions of LPOP and
     * RPOP able to block if the specified keys don't exist or contain empty lists.
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    Long zcount(final String key, final double min, final double max);

    /**
     * 设置键的过期时间
     * @param key
     * @param expireSecond
     * @return 如果键不存在或无法设置超时时间，返回 0。如果成功地为该键设置了超时时间，返回 1
     */
    long expire(String key, int expireSecond);

    /**
     * 以秒为单位返回 key 的剩余过期时间
     * @param key
     * @return  当 key 不存在时，返回 -2 。
     *          当 key 存在但没有设置剩余生存时间时，返回 -1 。
     *          否则，以秒为单位，返回 key 的剩余生存时间。
     */
    long ttl(String key);

    /**
     * 当 oldkey 和 newkey 相同，或者 oldkey 不存在时，返回一个错误信息
     * 当 newkey 已经存在时， RENAME 命令将覆盖旧值。
     * @param oldkey
     * @param newkey
     * @return
     */
    boolean rename(String oldkey, String newkey);

    /**
     * 当且仅当 newkey 不存在时，将 key 改名为 newkey 。
     * 当 oldkey 不存在时，返回一个错误。
     * @param oldkey
     * @param newkey
     * @return  1成功，  0失败
     */
    Long renamenx(String oldkey, String newkey);

}
