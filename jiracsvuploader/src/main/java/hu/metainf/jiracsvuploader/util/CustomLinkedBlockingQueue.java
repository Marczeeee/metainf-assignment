package hu.metainf.jiracsvuploader.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import hu.metainf.jiracsvuploader.stat.StatData;

/**
 * Custom {@link LinkedBlockingQueue} implementation registering waiting times tasks spending within
 * the queue.
 *
 */
public class CustomLinkedBlockingQueue extends LinkedBlockingQueue<Runnable> {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -3573032047260897279L;
    /**
     * {@link Map} holding task queuing times.
     */
    private final Map<Runnable, Long> taskWaitMap = new ConcurrentHashMap<>();

    @Override
    public boolean offer(final Runnable e) {
        if (e == null) {
            throw new NullPointerException();
        }
        taskWaitMap.put(e, System.currentTimeMillis());
        return super.offer(e);
    }

    @Override
    public Runnable take() throws InterruptedException {
        final Runnable runnable = super.take();
        if (runnable != null && taskWaitMap.containsKey(runnable)) {
            final Long taskWaitStart = taskWaitMap.remove(runnable);
            final Long queueTime = System.currentTimeMillis() - taskWaitStart;
            StatData.addSumValue(StatTypeKeys.TOTAL_QUEUE_TIME, queueTime);
        }
        return runnable;
    }
}
