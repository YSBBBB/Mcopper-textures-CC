package inventorypreviewpatch.helper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 一个用来判断一个方法是否正在被执行的类
 */
public abstract class MethodExecuteHelper {

    private static final int SHARD_COUNT = 4; // 分片数量
    @SuppressWarnings("unchecked")
    private static final ConcurrentHashMap<String, AtomicInteger>[] COUNTERS_SHARDS =
            (ConcurrentHashMap<String, AtomicInteger>[]) new ConcurrentHashMap<?, ?>[SHARD_COUNT];
    @SuppressWarnings("unchecked")
    private static final ConcurrentHashMap<String, AtomicLong>[] LAST_CALL_TIME_SHARDS =
            (ConcurrentHashMap<String, AtomicLong>[]) new ConcurrentHashMap<?, ?>[SHARD_COUNT];

    static {
        for (int i = 0; i < SHARD_COUNT; i++) {
            COUNTERS_SHARDS[i] = new ConcurrentHashMap<>();
            LAST_CALL_TIME_SHARDS[i] = new ConcurrentHashMap<>();
        }
    }

    /**
     * @param id 是区分不同位置的标识符
     * @return 返回id被分配后所在的片
     */

    private static int getShardIndex(String id) {
        return Math.abs(id.hashCode()) % SHARD_COUNT;
    }

    /**
     * 该方法用于要标记的方法的开头，
     * 在记录方法执行的总次数和最后一次执行方法的时间
     *
     * @param id        是区分不同位置的标识符
     * @param threshold 作为计数器的阈值，方法执行的总次数达到阈值后会清零
     */
    public static void startExecute(String id, int threshold) {
        counterIncrease(id, threshold);
        setLastCallTime(id);
    }

    /**
     * 记录方法最近一次被调用的时间
     *
     * @param id
     */
    private static void setLastCallTime(String id) {
        int shardIndex = getShardIndex(id);
        long lastCallTime = System.currentTimeMillis();
        LAST_CALL_TIME_SHARDS[shardIndex].computeIfAbsent(id, k -> new AtomicLong(0)).set(lastCallTime);
    }

    /**
     * 记录方法执行的总次数
     *
     * @param id
     * @param threshold
     */
    private static void counterIncrease(String id, int threshold) {
        int shardIndex = getShardIndex(id);
        AtomicInteger counter = COUNTERS_SHARDS[shardIndex].getOrDefault(id, new AtomicInteger(0));
        if (threshold > 0 && getCounter(id) >= threshold) {
            counter.set(0);
        }
        counter.incrementAndGet();
    }

    /**
     * @param id
     * @return 返回指定id的方法的使用次数
     */
    public static int getCounter(String id) {
        int shardIndex = getShardIndex(id);
        return COUNTERS_SHARDS[shardIndex].getOrDefault(id, new AtomicInteger(0)).get();
    }

    /**
     * 用于获取指定id的方法是否正在被执行的布尔值
     *
     * @param id
     * @param timeoutMS 如果方法的使用间隔大于这个时间，则判断执行结束
     * @return 返回方法是否正在被执行的布尔值
     */
    public static boolean getExecutionState(String id, long timeoutMS) {
        int shardIndex = getShardIndex(id);
        long timestamp = LAST_CALL_TIME_SHARDS[shardIndex].getOrDefault(id, new AtomicLong(0)).get();
        return System.currentTimeMillis() - timestamp < timeoutMS;
    }
}
