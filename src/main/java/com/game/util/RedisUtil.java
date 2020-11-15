package com.game.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redisTemplate
 */
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * set expire time
     *
     * @param key  string
     * @param time second long
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * get expire time by key
     *
     * @param key key
     * @return time 0 present never expire
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * if exist
     *
     * @param key
     * @return true false
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * delete
     *
     * @param key
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * Get string by key
     *
     * @param key string
     * @return string
     */
    public Object get(String key) {
        Object rtn = key == null ? null : redisTemplate.opsForValue().get(key);
        return rtn;
    }

    /**
     * Get string by key
     *
     * @param key string
     * @return string
     */
    public Object get(Integer key) {
        return get(String.valueOf(key));
    }

    /**
     * Set string
     *
     * @param key   string
     * @param value object
     * @return true false
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Set string
     *
     * @param key   int
     * @param value object
     * @return true false
     */
    public boolean set(Integer key, Object value) {
        try {
            redisTemplate.opsForValue().set(String.valueOf(key), value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Set string and expire time
     *
     * @param key   string
     * @param value object
     * @param time  long
     * @return true false
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * increment
     *
     * @param key string
     * @param delta string
     * @return string
     */
    public long incr(String key, long delta) {
        if(delta<0){
            throw new RuntimeException("delta error");
        }
        return redisTemplate.opsForValue().increment(key,delta);
    }


    /**
     * hset
     *
     * @param key   key
     * @param item  item
     * @param value
     * @return boolean
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * hmget
     * @param key 键
     * @return map
     */
    public Map<Object,Object> hmget(String key){
        try {
            return  redisTemplate.opsForHash().entries(key);
        } catch(Exception e) {
            return null;
        }
    }
}
