package inventorypreviewpatch.helper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MethodExecuteHelper {
    private static final int SHARD_COUNT = 8; // 分片数量
    @SuppressWarnings("unchecked")
    private static final ConcurrentHashMap<String, AtomicInteger>[] COUNTERS_SHARDS =
            (ConcurrentHashMap<String, AtomicInteger>[]) new ConcurrentHashMap<?, ?>[SHARD_COUNT];
    @SuppressWarnings("unchecked")
    private static final ConcurrentHashMap<String, AtomicBoolean>[] STATES_SHARDS =
            (ConcurrentHashMap<String, AtomicBoolean>[]) new ConcurrentHashMap<?, ?>[SHARD_COUNT];

    static {
        for (int i = 0; i < SHARD_COUNT; i++) {
            COUNTERS_SHARDS[i] = new ConcurrentHashMap<>();
            STATES_SHARDS[i] = new ConcurrentHashMap<>();
        }
    }

    // 获取分片索引
    private static int getShardIndex(String id) {
        return Math.abs(id.hashCode()) % SHARD_COUNT;
    }

    public static void startExecute(String id) {
        setCounter(id, 100);
    }

    public static void updateCounter(String id) {
        int current = getCounter(id);
        if (current <= 0) {
            setCounter(id, 0);
            setExecutionState(id, false);
        } else {
            setCounter(id, current - 50); // 原子递减
            setExecutionState(id, true);
        }
    }

    private static void setCounter(String id, int value) {
        int shardIndex = getShardIndex(id);
        COUNTERS_SHARDS[shardIndex].computeIfAbsent(id, k -> new AtomicInteger()).set(value);
    }

    private static void setExecutionState(String id, boolean state) {
        int shardIndex = getShardIndex(id);
        STATES_SHARDS[shardIndex].computeIfAbsent(id, k -> new AtomicBoolean()).set(state);
    }

    public static int getCounter(String id) {
        int shardIndex = getShardIndex(id);
        return COUNTERS_SHARDS[shardIndex].getOrDefault(id, new AtomicInteger(0)).get();
    }

    public static boolean getExecutionState(String id) {
        int shardIndex = getShardIndex(id);
        return STATES_SHARDS[shardIndex].getOrDefault(id, new AtomicBoolean(false)).get();
    }
}