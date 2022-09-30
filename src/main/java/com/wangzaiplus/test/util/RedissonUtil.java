package com.wangzaiplus.test.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.Redisson;
import org.redisson.RedissonLock;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: fangxin
 * @Date: 2019/7/1 12:24
 * @Description:
 */
@Component
@Slf4j
public class RedissonUtil {

    /**
     * 时间单位：秒
     */
    private static TimeUnit unit = TimeUnit.SECONDS;

    /**
     * 最多等待时间：2秒
     */
    private static int waitTime = 2;

    /**
     * 上锁后自动释放锁时间：两分钟
     */
    private static int leaseTime = 120;

    private static RedissonClient redissonClient;

    // redis集群
    private static String cluster;

    @Value("${spring.redis.cluster.nodes}")
    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    /**
     * 初始化redissonClient
     */
    @PostConstruct
    public static void init() {
        String[] nodes = cluster.split(",");
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = "redis://" + nodes[i];
        }
        RedissonClient redisson = null;
        Config config = new Config();
        //单机版
        config.useSingleServer().setAddress("redis://47.106.123.175:6379").setPassword("131452").setTimeout(10000);
        //集群版
/*        config.useClusterServers()
                .setScanInterval(2000) //设置集群状态扫描时间
                .addNodeAddress(nodes).setTimeout(5000);*/
        redissonClient = Redisson.create(config);
    }


    /**
     * 可重入锁
     *
     * @param lockKey
     * @param unit    时间单位
     * @param timeout 超时时间
     * @return
     * @Title: lock
     * @Description: 带超时的锁
     * @Date 2019年7月2日
     */
    public static RLock lock(String lockKey, TimeUnit unit, int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, unit);
        return lock;
    }

    /**
     * 可重入锁
     *
     * @param lockKey
     * @param unit      时间单位
     * @param waitTime  最多等待时间
     * @param leaseTime 上锁后自动释放锁时间
     * @return
     * @Title: tryLock
     * @Description: 尝试获取锁
     * @Date 2019年7月2日
     */
    public static boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * 可重入锁
     *
     * @param lockKey
     * @Title: unlock
     * @Description: 释放锁
     * @Date 2019年7月2日
     */
    public static void unlock(String lockKey) {
        try {
            RLock lock = redissonClient.getLock(lockKey);
            lock.unlock();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * @param lockKeys
     * @return
     * @Title: getMultiLock
     * @Description: 获取多sku联锁
     * @Date 2018年11月14日_上午11:14:41
     */
    public static RedissonMultiLock getMultiLock(Set<String> lockKeys) {
        if (CollectionUtils.isNotEmpty(lockKeys)) {
            Set<RLock> rLocks = new HashSet<RLock>();

            Iterator<String> iterator = lockKeys.iterator();
            while (iterator.hasNext()) {
                String lockKey = iterator.next();
                RLock rLock = redissonClient.getLock(lockKey);
                rLocks.add(rLock);
            }

            RLock[] rLockArray = new RLock[rLocks.size()];
            rLockArray = rLocks.toArray(rLockArray);
            // 联锁
            RedissonMultiLock multiLock = new RedissonMultiLock(rLockArray);

            return multiLock;
        }

        return null;
    }

    /**
     * @param multiLock 联锁对象
     * @param attempts  尝试次数
     * @return
     * @Description: 联锁 -> 尝试加锁
     * @Author: Administrator
     * @Date: 2018/12/11
     * @Version: 0.0.1
     */
    public static boolean multiTryLock(RedissonMultiLock multiLock, int attempts) {
        if (null == multiLock) {
            return false;
        }

        attempts--;

        boolean isLock = false;
        try {
            isLock = multiLock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            log.error("multiLock加锁失败！", e);
        }

        // 递归尝试加锁
        if (!isLock && attempts > 0) {
            try {
                // 线程等待5s重试加锁
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return multiTryLock(multiLock, attempts);
        }

        return isLock;
    }

    /**
     * @param :lockKeys
     * @Title: multiUnlock
     * @Description: 联锁释放
     * @Date 2018年11月14日_上午11:37:00
     */

    public static void multiUnlock(RedissonMultiLock multiLock) {
        try {
            if (null != multiLock) {
                multiLock.unlock();
            }
        } catch (Exception e) {
            log.error("multiLock释放锁失败！", e);
        }
    }

}
