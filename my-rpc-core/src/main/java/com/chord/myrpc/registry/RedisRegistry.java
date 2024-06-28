package com.chord.myrpc.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.chord.myrpc.RpcApplication;
import com.chord.myrpc.config.RegistryConfig;
import com.chord.myrpc.model.ServiceMetaInfo;
import io.etcd.jetcd.ByteSequence;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public class RedisRegistry extends JedisPubSub implements Registry {

    private URI uri;

    private Jedis client;

    /**
     * 通过本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        uri = URI.create(registryConfig.getAddress());
        client = new Jedis(uri.getHost(), uri.getPort());

        startWatcher();
        startHearBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) {
        try {
            String key = generateKey(serviceMetaInfo);
            String value = JSONUtil.toJsonStr(serviceMetaInfo);
            client.set(key, value);
            // Redis 没有内建的租约机制，可以通过设置过期时间来模拟
            client.expire(key, 30); // 设置30秒过期时间
            // 添加节点 key 信息到本地缓存
            localRegisterNodeKeySet.add(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        try {
            String key = generateKey(serviceMetaInfo);
            client.del(key);
            // 将节点 key 从本地缓存移除
            localRegisterNodeKeySet.remove(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }

        try {
            Set<String> keys = client.keys(generatePattern(serviceKey));
            List<ServiceMetaInfo> serviceMetaInfoList = keys.stream()
                    .map(key -> {
                        String value = client.get(key);
                        watch(key);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        log.info("当前节点下线");
        // 清理所有注册的服务
        for (String key : localRegisterNodeKeySet) {
            try {
                client.del(key);
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败");
            }
        }
        if (client != null) {
            client.close();
        }
    }

    // 用于生成Redis的key
    private String generateKey(ServiceMetaInfo serviceMetaInfo) {
        return RpcApplication.getRpcConfig().getName() + ":"
                + serviceMetaInfo.getServiceNodeKey().replace("/", ":");
    }

    // 用于生成Redis的key模式，用于keys命令进行模式匹配
    private String generatePattern(String serviceKey) {
        return RpcApplication.getRpcConfig().getName() + ":" + serviceKey + "*";
    }

    // Redis没有内建的心跳续期机制，需要自定义实现心跳续期逻辑
    @Override
    public void startHearBeat() {
        // 10 秒续签一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // 遍历本节点所有 key
                for (String key : localRegisterNodeKeySet) {
                    try {
                        String value = client.get(key);
                        // 该节点已过期（需要重新启动节点）
                        if (StrUtil.isBlank(value)) {
                            continue;
                        }
                        // 节点未过期。重新注册（续签）
                        client.expire(key, 30);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });

        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    public void startWatcher() {
        /**
         * 用于发布订阅的线程池
         */
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(()-> {
            // 新开一个客户端用于监听
            Jedis watchClient = new Jedis(uri.getHost(), uri.getPort());
            watchClient.configSet("notify-keyspace-events", "KEA");
            watchClient.psubscribe(this, "__keyevent@0__:*");
        });
    }

    // Redis没有内建的监听机制，需要使用发布/订阅模式来实现
    @Override
    public void watch(String serviceNodeKey) {
        // 之前未被监听，开启监听
        watchingKeySet.add(serviceNodeKey);
        System.out.println("key, "+serviceNodeKey);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        if (watchingKeySet.contains(message)) {
            String cmd = message.split(":")[1];
            if (cmd.equals("del")) {
                registryServiceCache.notify(KeyEventType.Delete);
            }
            else if (cmd.equals("set")) {
                registryServiceCache.notify(KeyEventType.Delete);
            }
        }
    }
}