package com.barry.common.spring.config.redis;

import com.barry.common.core.util.Assert;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * <p>Redis helper</p>
 *
 * @author chenpeng
 * Create at February 18, 2019 at 16:04:36 GMT+8
 */
public final class RedisHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisHelper.class);

    private RedisTemplate<String, Object> redisTemplate;

    RedisHelper(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 删除 key
     *
     * @param key
     * @author chenpeng
     * Create at February 18, 2019 at 16:04:36 GMT+8
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除 key
     *
     * @param keys
     */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 序列化key
     *
     * @param key
     * @return
     */
    public byte[] dump(String key) {
        return redisTemplate.dump(key);
    }

    /**
     * 是否存在key
     *
     * @param key
     * @return
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param date
     * @return
     */
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    /**
     * 查找匹配的key
     *
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 将 key 从当前选定的数据库 (请参阅 select 命令) 移动到指定的目标数据库。
     * 当 key 已存在于目标数据库中, 或者源数据库中不存在时, 它不执行任何操作。
     * 因此, 可以使用 move 作为锁定原语。
     *
     * @param key
     * @param dbIndex
     * @return
     */
    public Boolean move(String key, int dbIndex) {
        return redisTemplate.move(key, dbIndex);
    }

    /**
     * 删除 key 上的现有超时, 将 key 从可变 (具有过期时间的 key)
     * 转换为持久 (该 key 永远不会过期, 因为没有关联的超时时间)。
     *
     * @param key
     * @return
     */
    public Boolean persist(String key) {
        return redisTemplate.persist(key);
    }

    /**
     * 返回 key 的剩余的过期时间
     *
     * @param key
     * @param unit
     * @return
     */
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 返回 key 的剩余的过期时间
     *
     * @param key
     * @return
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 从当前数据库中随机返回一个 key
     *
     * @return
     */
    public String randomKey() {
        return redisTemplate.randomKey();
    }

    /**
     * 修改 key 的名称
     *
     * @param oldKey
     * @param newKey
     */
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 仅当 newkey 不存在时，将 oldKey 改名为 newkey
     *
     * @param oldKey
     * @param newKey
     * @return
     */
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 返回 key 所储存的值的类型
     *
     * @param key
     * @return
     */
    public DataType type(String key) {
        return redisTemplate.type(key);
    }

    /** -------------------string相关操作--------------------- */

    /**
     * 设置指定 key 的值
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 返回 key 中字符串值的子字符
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public String get(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * <p>通过指定 key 和 clazz，获得对应类型的数据</p>
     *
     * @author chenpeng
     * Create at February 20, 2019 at 19:56:38 GMT+8
     */
    public <T> T get(String key, Class<T> clazz) {
        return (T) get(key);
    }

    /**
     * <p>通过指定 key 和 clazz，获得对应类型的数据</p>
     *
     * @author chenpeng
     * Create at February 20, 2019 at 19:56:38 GMT+8
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        final List<T> results = Lists.newArrayList();
        Object values = get(key);
        if (values != null) {
            List r = (List) values;
            r.forEach(e -> {
                T t = null;
                if (e != null) {
                    t = (T) e;
                }
                results.add(t);
            });
        }
        return results;
    }

    /**
     * 获取指定 key 的值
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
     *
     * @param key
     * @param value
     * @return
     */
    public Object getAndSetObject(String key, Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
     *
     * @param key
     * @param value
     * @return
     */
    public <T> T getAndSet(String key, T value) {
        Object r = getAndSetObject(key, value);
        return r == null ? null : (T) r;
    }

    /**
     * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)
     *
     * @param key
     * @param offset
     * @return
     */
    public Boolean getBit(String key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 批量获取
     *
     * @param keys
     * @return
     */
    public List<Object> multiGetObject(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 批量获取
     *
     * @param keys
     * @return
     */
    public <T> List<T> multiGet(Collection<String> keys, Class<T> clazz) {
        List<Object> objects = multiGetObject(keys);
        return Optional.ofNullable(objects)
                .orElse(Lists.newArrayList())
                .stream()
                .map(e -> {
                    T r = apply(e);
                    return r;
                }).collect(Collectors.toList());
    }

    /**
     * 设置ASCII码, 字符串'a'的ASCII码是97, 转为二进制是'01100001', 此方法是将二进制第offset位值变为value
     *
     * @param key key
     * @param offset 位置
     * @param value   值,true为1, false为0
     * @return
     */
    public boolean setBit(String key, long offset, boolean value) {
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 timeout
     *
     * @param key
     * @param value
     * @param timeout 过期时间
     * @param unit    时间单位, 天:TimeUnit.DAYS 小时:TimeUnit.HOURS 分钟:TimeUnit.MINUTES
     *                秒:TimeUnit.SECONDS 毫秒:TimeUnit.MILLISECONDS
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 只有在 key 不存在时设置 key 的值
     *
     * @param key
     * @param value
     * @return 之前已经存在返回false, 不存在返回true
     */
    public boolean setIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始
     *
     * @param key
     * @param value
     * @param offset 从指定位置开始覆写
     */
    public void set(String key, Object value, long offset) {
        redisTemplate.opsForValue().set(key, value, offset);
    }

    /**
     * 获取字符串的长度
     *
     * @param key
     * @return
     */
    public Long size(String key) {
        return redisTemplate.opsForValue().size(key);
    }

    /**
     * 批量添加
     *
     * @param maps
     */
    public void multiSet(Map<String, String> maps) {
        redisTemplate.opsForValue().multiSet(maps);
    }

    /**
     * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在
     *
     * @param maps
     * @return 之前已经存在返回false, 不存在返回true
     */
    public boolean multiSetIfAbsent(Map<String, String> maps) {
        return redisTemplate.opsForValue().multiSetIfAbsent(maps);
    }

    /**
     * 增加(自增长), 负数则为自减
     *
     * @param key
     * @param increment
     * @return
     */
    public Long increment(String key, long increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * @param key
     * @param increment
     * @return
     */
    public Double increment(String key, double increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 追加到末尾
     *
     * @param key
     * @param value
     * @return
     */
    public Integer append(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }

    /** -------------------hash相关操作------------------------- */

    /**
     * 获取存储在哈希表中指定字段的值
     *
     * @param key
     * @param field
     * @return
     */
    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 获取存储在哈希表中指定字段的值
     *
     * @param key
     * @param field
     * @return
     */
    public <T> T hGet(String key, String field, Class<T> clazz) {
        Object r = hGet(key, field);
        return r == null ? null : (T) r;
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key
     * @return
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key
     * @param fields
     * @return
     */
    public List<Object> hMultiGet(String key, Collection<String> fields) {
        return redisTemplate.opsForHash().multiGet(key, (Collection) fields);
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key
     * @param fields
     * @return
     */
    public <T> List<T> hMultiGet(String key, Collection<String> fields, Class<T> clazz) {
        List<Object> results = hMultiGet(key, fields);
        return Optional.ofNullable(results)
                .orElse(Lists.newArrayList())
                .stream()
                .map(e -> {
                    T r = apply(e);
                    return r;
                }).collect(Collectors.toList());
    }

    public void hPut(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public void hPutAll(String key, Map<String, String> maps) {
        redisTemplate.opsForHash().putAll(key, maps);
    }

    /**
     * 仅当hashKey不存在时才设置
     *
     * @param key
     * @param hashKey
     * @param value
     * @return
     */
    public Boolean hPutIfAbsent(String key, String hashKey, String value) {
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 删除一个或多个哈希表字段
     *
     * @param key
     * @param fields
     * @return
     */
    public Long hDelete(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 查看哈希表 key 中，指定的字段是否存在
     *
     * @param key
     * @param field
     * @return
     */
    public Boolean hExists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     *
     * @param key
     * @param field
     * @param increment
     * @return
     */
    public Long hIncrBy(String key, Object field, long increment) {
        return redisTemplate.opsForHash().increment(key, field, increment);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     *
     * @param key
     * @param field
     * @param delta
     * @return
     */
    public Double hIncrByFloat(String key, Object field, double delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    /**
     * 获取所有哈希表中的字段
     *
     * @param key
     * @return
     */
    public Set<Object> hKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取哈希表中字段的数量
     *
     * @param key
     * @return
     */
    public Long hSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 获取哈希表中所有值
     *
     * @param key
     * @return
     */
    public List<Object> hValues(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * 迭代哈希表中的键值对
     *
     * @param key
     * @param options
     * @return
     */
    public Cursor<Map.Entry<Object, Object>> hScan(String key, ScanOptions options) {
        return redisTemplate.opsForHash().scan(key, options);
    }

    /** ------------------------list相关操作---------------------------- */

    /**
     * 通过索引获取列表中的元素
     *
     * @param key
     * @param index
     * @return
     */
    public Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 通过索引获取列表中的元素
     *
     * @param key
     * @param index
     * @return
     */
    public <T> T lIndex(String key, long index, Class<T> clazz) {
        Object r = lIndex(key, index);
        return r == null ? null : (T) r;
    }

    /**
     * 获取列表指定范围内的元素
     *
     * @param key
     * @param start 开始位置, 0是开始位置
     * @param end   结束位置, -1返回所有
     * @return
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取列表指定范围内的元素
     *
     * @param key
     * @param start 开始位置, 0是开始位置
     * @param end   结束位置, -1返回所有
     * @return
     */
    public <T> List<T> lRange(String key, long start, long end, Class<T> clazz) {
        List<Object> results = lRange(key, start, end);
        return Optional.ofNullable(results)
                .orElse(Lists.newArrayList())
                .stream()
                .map(e -> {
                    T r = apply(e);
                    return r;
                }).collect(Collectors.toList());
    }

    /**
     * 存储在list头部
     *
     * @param key
     * @param value
     * @return
     */
    public Long lLeftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public Long lLeftPushAll(String key, String... value) {
        return redisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public Long lLeftPushAll(String key, Collection<String> value) {
        return redisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * 当list存在的时候才加入
     *
     * @param key
     * @param value
     * @return
     */
    public Long lLeftPushIfPresent(String key, String value) {
        return redisTemplate.opsForList().leftPushIfPresent(key, value);
    }

    /**
     * 如果pivot存在,再pivot前面添加
     *
     * @param key
     * @param pivot
     * @param value
     * @return
     */
    public Long lLeftPush(String key, String pivot, String value) {
        return redisTemplate.opsForList().leftPush(key, pivot, value);
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public Long lRightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public Long lRightPushAll(String key, String... value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public Long lRightPushAll(String key, Collection<String> value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * 为已存在的列表添加值
     *
     * @param key
     * @param value
     * @return
     */
    public Long lRightPushIfPresent(String key, String value) {
        return redisTemplate.opsForList().rightPushIfPresent(key, value);
    }

    /**
     * 在pivot元素的右边添加值
     *
     * @param key
     * @param pivot
     * @param value
     * @return
     */
    public Long lRightPush(String key, String pivot, String value) {
        return redisTemplate.opsForList().rightPush(key, pivot, value);
    }

    /**
     * 通过索引设置列表元素的值
     *
     * @param key
     * @param index 位置
     * @param value
     */
    public void lSet(String key, long index, String value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 移出并获取列表的第一个元素
     *
     * @param key
     * @return 删除的元素
     */
    public Object lLeftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 移出并获取列表的第一个元素
     *
     * @param key
     * @return 删除的元素
     */
    public <T> T lLeftPop(String key, Class<T> clazz) {
        Object r = lLeftPop(key);
        return r == null ? null : (T) r;
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key
     * @param timeout 等待时间
     * @param unit    时间单位
     * @return
     */
    public Object lLeftPopBlock(String key, long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().leftPop(key, timeout, unit);
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key
     * @param timeout 等待时间
     * @param unit    时间单位
     * @return
     */
    public <T> T lLeftPopBlock(String key, long timeout, TimeUnit unit, Class<T> clazz) {
        Object r = lLeftPopBlock(key, timeout, unit);
        return r == null ? null : (T) r;
    }

    /**
     * 移除并获取列表最后一个元素
     *
     * @param key
     * @return 删除的元素
     */
    public Object lRightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 移除并获取列表最后一个元素
     *
     * @param key
     * @return 删除的元素
     */
    public <T> T lRightPop(String key, Class<T> clazz) {
        Object r = lRightPop(key);
        return r == null ? null : (T) r;
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key
     * @param timeout 等待时间
     * @param unit    时间单位
     * @return
     */
    public Object lRightPopBlock(String key, long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().rightPop(key, timeout, unit);
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key
     * @param timeout 等待时间
     * @param unit    时间单位
     * @return
     */
    public <T> T lRightPopBlock(String key, long timeout, TimeUnit unit, Class<T> clazz) {
        Object r = lRightPopBlock(key, timeout, unit);
        return r == null ? null : (T) r;
    }

    /**
     * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回
     *
     * @param sourceKey
     * @param destinationKey
     * @return
     */
    public Object lRightPopAndLeftPush(String sourceKey, String destinationKey) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey,
                destinationKey);
    }

    /**
     * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回
     *
     * @param sourceKey
     * @param destinationKey
     * @return
     */
    public <T> T lRightPopAndLeftPush(String sourceKey, String destinationKey, Class<T> clazz) {
        Object r = lRightPopAndLeftPush(sourceKey, destinationKey);
        return r == null ? null : (T) r;
    }

    /**
     * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param sourceKey
     * @param destinationKey
     * @param timeout
     * @param unit
     * @return
     */
    public Object lRightPopAndLeftPushBlock(String sourceKey, String destinationKey,
            long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey,
                destinationKey, timeout, unit);
    }

    /**
     * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param sourceKey
     * @param destinationKey
     * @param timeout
     * @param unit
     * @return
     */
    public <T> T lRightPopAndLeftPushBlock(String sourceKey, String destinationKey,
            long timeout, TimeUnit unit, Class<T> clazz) {
        Object r = lRightPopAndLeftPushBlock(sourceKey, destinationKey, timeout, unit);
        return r == null ? null : (T) r;
    }

    /**
     * 删除集合中值等于value得元素
     *
     * @param key
     * @param index index=0, 删除所有值等于value的元素; index>0, 从头部开始删除第一个值等于value的元素;
     *              index<0, 从尾部开始删除第一个值等于value的元素;
     * @param value
     * @return
     */
    public Long lRemove(String key, long index, String value) {
        return redisTemplate.opsForList().remove(key, index, value);
    }

    /**
     * 裁剪list
     *
     * @param key
     * @param start
     * @param end
     */
    public void lTrim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * 获取列表长度
     *
     * @param key
     * @return
     */
    public Long lLen(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /** --------------------set相关操作-------------------------- */

    /**
     * set添加元素
     *
     * @param key
     * @param values
     * @return
     */
    public Long sAdd(String key, String... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * set移除元素
     *
     * @param key
     * @param values
     * @return
     */
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 移除并返回集合的一个随机元素
     *
     * @param key
     * @return
     */
    public Object sPop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    /**
     * 移除并返回集合的一个随机元素
     *
     * @param key
     * @return
     */
    public <T> T sPop(String key, Class<T> clazz) {
        Object r = sPop(key);
        return r == null ? null : (T) r;
    }

    /**
     * 将元素value从一个集合移到另一个集合
     *
     * @param key
     * @param value
     * @param destKey
     * @return
     */
    public Boolean sMove(String key, String value, String destKey) {
        return redisTemplate.opsForSet().move(key, value, destKey);
    }

    /**
     * 获取集合的大小
     *
     * @param key
     * @return
     */
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 判断集合是否包含value
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取两个集合的交集
     *
     * @param key
     * @param otherKey
     * @return
     */
    public Set<Object> sIntersect(String key, String otherKey) {
        return redisTemplate.opsForSet().intersect(key, otherKey);
    }

    /**
     * 获取两个集合的交集
     *
     * @param key
     * @param otherKey
     * @return
     */
    public <T> Set<T> sIntersect(String key, String otherKey, Class<T> clazz) {
        return applyToSet(sIntersect(key, otherKey));
    }

    /**
     * 获取key集合与多个集合的交集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<Object> sIntersect(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().intersect(key, otherKeys);
    }

    /**
     * 获取key集合与多个集合的交集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public <T> Set<T> sIntersect(String key, Collection<String> otherKeys, Class<T> clazz) {
        return applyToSet(sIntersect(key, otherKeys));
    }

    /**
     * key集合与otherKey集合的交集存储到destKey集合中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long sIntersectAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key, otherKey,
                destKey);
    }

    /**
     * key集合与多个集合的交集存储到destKey集合中
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long sIntersectAndStore(String key, Collection<String> otherKeys,
            String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key, otherKeys,
                destKey);
    }

    /**
     * 获取两个集合的并集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<Object> sUnion(String key, String otherKeys) {
        return redisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * 获取两个集合的并集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public <T> Set<T> sUnion(String key, String otherKeys, Class<T> clazz) {
        return applyToSet(sUnion(key, otherKeys));
    }

    /**
     * 获取key集合与多个集合的并集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<Object> sUnion(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * 获取key集合与多个集合的并集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public <T> Set<T> sUnion(String key, Collection<String> otherKeys, Class<T> clazz) {
        return applyToSet(sUnion(key, otherKeys));
    }

    /**
     * key集合与otherKey集合的并集存储到destKey中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long sUnionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * key集合与多个集合的并集存储到destKey中
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long sUnionAndStore(String key, Collection<String> otherKeys,
            String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 获取两个集合的差集
     *
     * @param key
     * @param otherKey
     * @return
     */
    public Set<Object> sDifference(String key, String otherKey) {
        return redisTemplate.opsForSet().difference(key, otherKey);
    }

    /**
     * 获取两个集合的差集
     *
     * @param key
     * @param otherKey
     * @return
     */
    public <T> Set<T> sDifference(String key, String otherKey, Class<T> clazz) {
        return applyToSet(sDifference(key, otherKey));
    }

    /**
     * 获取key集合与多个集合的差集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<Object> sDifference(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().difference(key, otherKeys);
    }

    /**
     * 获取两个集合的差集
     *
     * @param key key
     * @param otherKeys otherKeys
     * @param clazz clazz
     * @return
     */
    public <T> Set<T> sDifference(String key, Collection<String> otherKeys, Class<T> clazz) {
        return applyToSet(sDifference(key, otherKeys));
    }

    /**
     * key集合与otherKey集合的差集存储到destKey中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long sDifferenceAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, otherKey,
                destKey);
    }

    /**
     * key集合与多个集合的差集存储到destKey中
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long sDifferenceAndStore(String key, Collection<String> otherKeys,
            String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, otherKeys,
                destKey);
    }

    /**
     * 获取集合所有元素
     *
     * @param key key
     * @return
     */
    public Set<Object> setMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 获取集合所有元素
     *
     * @param key key
     * @param clazz clazz
     * @return
     */
    public <T> Set<T> setMembers(String key, Class<T> clazz) {
        return applyToSet(setMembers(key));
    }

    /**
     * 随机获取集合中的一个元素
     *
     * @param key
     * @return
     */
    public Object sRandomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 随机获取集合中的一个元素
     *
     * @param key
     * @return
     */
    public <T> T sRandomMember(String key, Class<T> clazz) {
        Object r = sRandomMember(key);
        return r == null ? null : (T) r;
    }

    /**
     * 随机获取集合中count个元素
     *
     * @param key
     * @param count
     * @return
     */
    public List<Object> sRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 随机获取集合中count个元素
     *
     * @param key
     * @param count
     * @return
     */
    public <T> List<T> sRandomMembers(String key, long count, Class<T> clazz) {
        List<Object> results = sRandomMembers(key, count);
        return Optional.ofNullable(results)
                .orElse(Lists.newArrayList())
                .stream()
                .map(e -> {
                    T r = apply(e);
                    return r;
                }).collect(Collectors.toList());
    }

    /**
     * 随机获取集合中count个元素并且去除重复的
     *
     * @param key
     * @param count
     * @return
     */
    public Set<Object> sDistinctRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count);
    }

    /**
     * 随机获取集合中count个元素并且去除重复的
     *
     * @param key
     * @param count
     * @return
     */
    public <T> Set<T> sDistinctRandomMembers(String key, long count, Class<T> clazz) {
        return applyToSet(sDistinctRandomMembers(key, count));
    }

    /**
     * @param key
     * @param options
     * @return
     */
    public Cursor<Object> sScan(String key, ScanOptions options) {
        return redisTemplate.opsForSet().scan(key, options);
    }

    /**------------------zSet相关操作--------------------------------*/

    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     * @param value
     * @param score
     * @return
     */
    public Boolean zAdd(String key, String value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * @param key
     * @param values
     * @return
     */
    public Long zAdd(String key, Set<ZSetOperations.TypedTuple<Object>> values) {
        return redisTemplate.opsForZSet().add(key, values);
    }

    /**
     * @param key
     * @param values
     * @return
     */
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 增加元素的score值，并返回增加后的值
     *
     * @param key
     * @param value
     * @param delta
     * @return
     */
    public Double zIncrementScore(String key, String value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 返回元素在集合的排名,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     * @param value
     * @return 0表示第一位
     */
    public Long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 返回元素在集合的排名,按元素的score值由大到小排列
     *
     * @param key
     * @param value
     * @return
     */
    public Long zReverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 获取集合的元素, 从小到大排序
     *
     * @param key
     * @param start 开始位置
     * @param end   结束位置, -1查询所有
     * @return
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取集合的元素, 从小到大排序
     *
     * @param key
     * @param start 开始位置
     * @param end   结束位置, -1查询所有
     * @return
     */
    public <T> Set<T> zRange(String key, long start, long end, Class<T> clazz) {
        return applyToSet(zRange(key, start, end));
    }

    /**
     * 获取集合元素, 并且把score值也获取
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRangeWithScores(String key, long start,
            long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * 根据Score值查询集合元素
     *
     * @param key
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 根据Score值查询集合元素
     *
     * @param key
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public <T> Set<T> zRangeByScore(String key, double min, double max, Class<T> clazz) {
        return applyToSet(zRangeByScore(key, min, max));
    }

    /**
     * 根据Score值查询集合元素, 从小到大排序
     *
     * @param key
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key,
            double min, double max) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key,
            double min, double max,
            long start, long end) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max,
                start, end);
    }

    /**
     * 获取集合的元素, 从大到小排序
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取集合的元素, 从大到小排序
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public <T> Set<T> zReverseRange(String key, long start, long end, Class<T> clazz) {
        return applyToSet(zReverseRange(key, start, end));
    }

    /**
     * 获取集合的元素, 从大到小排序, 并返回score值
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeWithScores(String key,
            long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start,
                end);
    }

    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<Object> zReverseRangeByScore(String key, double min,
            double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public <T> Set<T> zReverseRangeByScore(String key, double min,
            double max, Class<T> clazz) {
        return applyToSet(zReverseRangeByScore(key, min, max));
    }

    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(
            String key, double min, double max) {


        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key,
                min, max);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zReverseRangeByScore(String key, double min,
            double max, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max,
                start, end);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public <T> Set<T> zReverseRangeByScore(String key, double min,
            double max, long start, long end, Class<T> clazz) {
        return applyToSet(zReverseRangeByScore(key, min, max, start, end));
    }

    /**
     * 根据score值获取集合元素数量
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zCount(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * 获取集合大小
     *
     * @param key
     * @return
     */
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 获取集合大小
     *
     * @param key
     * @return
     */
    public Long zZCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 获取集合中value元素的score值
     *
     * @param key
     * @param value
     * @return
     */
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 移除指定索引位置的成员
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Long zRemoveRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * 根据指定的score值的范围来移除成员
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /**
     * 获取key和otherKey的并集并存储在destKey中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long zUnionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long zUnionAndStore(String key, Collection<String> otherKeys,
            String destKey) {
        return redisTemplate.opsForZSet()
                .unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 交集
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long zIntersectAndStore(String key, String otherKey,
            String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, otherKey,
                destKey);
    }

    /**
     * 交集
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long zIntersectAndStore(String key, Collection<String> otherKeys,
            String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, otherKeys,
                destKey);
    }

    /**
     * @param key
     * @param options
     * @return
     */
    public Cursor<ZSetOperations.TypedTuple<Object>> zScan(String key, ScanOptions options) {
        return redisTemplate.opsForZSet().scan(key, options);
    }

    /**
     * <p>execute</p>
     *
     * @author chenpeng
     * Create at May 28, 2019 at 19:13:26 GMT+8
     */
    public <T> T execute(SessionCallback<T> callback) {
        if (callback != null) {
            return redisTemplate.execute(callback);
        }
        return null;
    }

    private <T> T apply(Object e) {
        T r = null;
        if (e != null) {
            r = (T) e;
        }
        return r;
    }

    private <T> Set<T> applyToSet(Set<Object> objects) {
        Set<Object> results = objects;
        return Optional.ofNullable(results)
                .orElse(Sets.newHashSet())
                .stream()
                .map(e -> {
                    T r = apply(e);
                    return r;
                }).collect(Collectors.toSet());
    }

    /**
     * 获取锁：自旋三次
     * 以key value的结构持有某个锁，value用于在解锁时候检查
     * 在一定时间后释放，也可以使用unlockLua方法进行解锁
     *
     * @param key key
     * @param value value
     * @param time：过期时间 单位-秒
     * @return
     */
    public boolean lock(String key, Object value, Long time) {
        Assert.check(StringUtils.isNotBlank(key), "key is blank.");
        Assert.check(Objects.nonNull(value), "value is null.");
        boolean locked = false;
        int tryCount = 3;
        while (!locked && tryCount > 0) {
            if (Objects.isNull(time)) {
                locked = redisTemplate.opsForValue().setIfAbsent(key, value);
            } else {
                locked = redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS);
            }
            tryCount--;
            if (!locked) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return locked;
    }

    /**
     * 获取锁：自旋三次
     * 以key value的结构持有某个锁，不自动释放
     *
     * @param key key
     * @param value value
     * @return
     */
    public boolean lock(String key, Object value) {
        return lock(key, value, null);
    }


    /**
     * 可重入锁，通过对某个key值自增实现, 在解锁时对锁的值-1，如果值为0时则释放锁
     * 可以与lock方法配合使用对同一个key上锁实现互斥
     * 提供超时时间
     * @param key key
     * @param time expire time
     * @return locked
     */
    public boolean lockReentrant(String key, Long time) {
        Assert.check(StringUtils.isNotBlank(key), "key is blank.");
        boolean locked = false;
        Long lockCount;
        String script = " local value = redis.call('incr', KEYS[1]); " +
            "redis.call('expire', KEYS[1], ARGV[1]); " +
            "return value";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        try {
            if (Objects.isNull(time)) {
                lockCount = redisTemplate.execute(redisScript, Collections.singletonList(key));
            } else {
                lockCount = redisTemplate.execute(redisScript, Collections.singletonList(key), time);
            }
        } catch (Exception e) {
            return false;
        }
        if (lockCount > 0) {
            locked = true;
        }
        return locked;
    }

    /**
     * 无超时时间的可重入锁
     * @param key key
     * @return locked
     */
    public boolean lockReentrant(String key) {
        return lockReentrant(key, null);
    }

    /**
     * 通过lua脚本对字符串类型的key进行解锁，
     * 检查key与value，如果一致则del key
     * @param key key
     * @param value value
     * @return result
     */
    public boolean unlockLua(String key, Object value) {
        Assert.check(StringUtils.isNotBlank(key), "key is blank.");
        Assert.check(Objects.nonNull(value), "value is null.");
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Object result;
        try {
            result = redisTemplate.execute(redisScript, Collections.singletonList(key), value);
        } catch (Exception e) {
            return false;
        }
        //没有指定序列化方式，默认使用上面配置的
        return result.equals(1L);
    }

    /**
     * 通过lua脚本对可重入锁进行解锁操作
     * 对value为字符串类型则解锁失败，如果value为数字型的则对value-1,如果value=0则del key实现解锁
     * 与lockReentrant配合使用
     * @param key
     * @return result
     */
    public boolean unlockReentrantLua(String key) {
        Assert.check(StringUtils.isNotBlank(key), "key is blank.");
        String script = "local value = redis.call('get', KEYS[1]); " +
            "if (type(tonumber(value)) == 'number') then value = redis.call('decr', KEYS[1]) end; " +
            "if (value == 0) then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Object result;
        try {
            result = redisTemplate.execute(redisScript, Collections.singletonList(key));
        } catch (Exception e) {
            return false;
        }
        //没有指定序列化方式，默认使用上面配置的
        return result.equals(1L);
    }

    /**
     * 对id批量上锁
     *
     * @param keyIdList      keyIdList
     * @param lockKeyPrefix  lockKeyPrefix
     * @param lockExpireTime lockExpireTime
     * @return locked
     */
    public boolean lockBatch(List<String> keyIdList, String lockKeyPrefix, long lockExpireTime) {
        String lua =
                "for i=1, #KEYS do\n" +
                        "    local flag = redis.call('exists', KEYS[i])\n" +
                        "    if (flag == 1) then return 0 end\n" +
                        "end\n" +
                        "for i=1, #KEYS do \n" +
                        "    redis.call('set', KEYS[i], \"1\") \n" +
                        "    redis.call('expire', KEYS[i], " + lockExpireTime + ")\n" +
                        "end\n" +
                        "return 1";
        List<String> keyList = keyIdList.stream().map(it -> lockKeyPrefix + it).collect(toList());
        Long result = redisTemplate.execute(new DefaultRedisScript<>(lua, Long.class), keyList);
        return Objects.equals(result, 1L);
    }

    /**
     * 检测目标key是否存在且value等于expectValue, 如果符合条件, 赋值为新value, 设置过期时间, 返回新value, 否则返回当前value
     *
     * @param key           key
     * @param expectValue   期望值
     * @param newValue      新值
     * @param expireSeconds 过期时间
     * @return 如果修改成功, 返回newValue, 如果修改失败, 返回null/oldValue
     */
    public String setIfPresentAndEquals(String key, String expectValue, String newValue, long expireSeconds) {
        String lua =
                "local value = redis.call('get', KEYS[1])\n" +
                        "if (value == ARGV[1]) then\n" +
                        "    redis.call('set', KEYS[1], ARGV[2])\n" +
                        "    redis.call('expire', KEYS[1], ARGV[3])\n" +
                        "    return ARGV[2]\n" +
                        "else\n" +
                        "    return value\n" +
                        "end";
        return redisTemplate.execute(new DefaultRedisScript<>(lua, String.class), Collections.singletonList(key), expectValue, newValue, expireSeconds);
    }
}
