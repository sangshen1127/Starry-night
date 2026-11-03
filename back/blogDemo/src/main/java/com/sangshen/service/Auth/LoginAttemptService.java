package com.sangshen.service.Auth;

import com.sangshen.common.TooManyRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录失败限流：同一IP在滑动窗口内失败超过阈值就拒绝
 * 内存实现，重启即清空，单机够用，将来多实例在换Redis
 */
@Slf4j
@Service
public class LoginAttemptService {
    private static final int MAX_FAILURES = 5;
    private static final Duration WINDOW = Duration.ofMinutes(5);   // 定义当登录失败达到阈值后，滑动窗口的时间长度

    private final Map<String, Deque<Long>> failures = new ConcurrentHashMap<>();    // 一个线程安全的map，当多个线程同时读写不会出错，一个IP地址对应着一个登陆失败的时间列队

    //在认证之前超过限制直接返回429
    public void ensureAllowed(String ip) {
        Deque<Long> records = failures.get(ip); // 获取该IP的登录失败时间队列
        if(records == null) {
            return; //若改ip从没失败过，则直接放行
        }
        long now = System.currentTimeMillis();
        synchronized (records) {
            evictExpired(records,now);

            if (records.size() >= MAX_FAILURES){
                long retryAfterMs = WINDOW.toMillis() - (now - records.peekFirst()); //WINDOW.toMillis()窗口总长
                long seconds = Math.max(1, retryAfterMs / 1000);
                throw new TooManyRequestException("登录失败次数过多，请" + seconds + "秒后重试");
            }
        }
    }

    /**
     * 认证失败后调用,密码校验失败后
     */
    public void recordFailure(String ip, String username) {
        long now = System.currentTimeMillis();
        Deque<Long> records = failures.computeIfAbsent(ip, k -> new ArrayDeque<>());    // 获取该IP的登录失败时间队列，如果不存在则创建一个
        synchronized (records) {
            evictExpired(records, now);
            records.addLast(now);   //记录失败时间
        }
        log.warn("登陆失败 ip = {} username = {} 窗口失败次数 = {}", ip, username, records.size());
    }

    //认证成功后调用,清白记录
    public void clear(String ip) {
        failures.remove(ip);
    }

    //清除掉过期的登录失败请求
    private void evictExpired(Deque<Long> records, long now) {
        while (!records.isEmpty() && now - records.peekFirst() > WINDOW.toMillis()) {
            records.pollFirst();    //移出过期记录
        }
    }

    //防止长期运行内存只增不减，每分钟清空一次空桶和过期桶
    @Scheduled(fixedDelay = 60_000)
    public void purge() {
        long now = System.currentTimeMillis();
        failures.entrySet().removeIf(entry ->{
            Deque<Long> records = entry.getValue();
            synchronized (records) {
                evictExpired(records, now);
                return records.isEmpty();
            }
        });
    }
}
