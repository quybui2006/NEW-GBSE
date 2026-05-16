package demo.service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * In-memory brute-force guard for the admin login form.
 *
 * Tracks failed attempts by a string key (username or IP address).
 * After MAX_ATTEMPTS failures inside WINDOW_MS the key is blocked.
 * The block automatically expires when the oldest failure slides out
 * of the window — no scheduled cleanup thread is required.
 *
 * Uses the same sliding-window + synchronized-Deque pattern as
 * ContactRateLimiter so the two are consistent and easy to maintain.
 *
 * Limitation: state is in-memory and lost on restart. In a clustered
 * deployment each node has its own counter; attackers can distribute
 * requests across nodes to avoid any single node's limit.
 */
@Service
public class LoginAttemptService {

    private static final Logger log = LoggerFactory.getLogger(LoginAttemptService.class);

    static final int  MAX_ATTEMPTS = 5;
    static final long WINDOW_MS    = 10 * 60 * 1000L; // 10 minutes

    private final Map<String, Deque<Long>> timestamps = new ConcurrentHashMap<>();

    /** Called on every authentication failure for the given key. */
    public void recordFailure(String key) {
        if (key == null || key.isBlank()) return;
        long now = System.currentTimeMillis();
        Deque<Long> q = timestamps.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (q) {
            evict(q, now);
            q.addLast(now);
            if (q.size() >= MAX_ATTEMPTS) {
                log.warn("[LoginAttemptService] Key '{}' reached {} failures — blocked for {} min.",
                         key, q.size(), WINDOW_MS / 60_000);
            }
        }
    }

    /** Returns true when the key has reached the failure threshold. */
    public boolean isBlocked(String key) {
        if (key == null || key.isBlank()) return false;
        Deque<Long> q = timestamps.get(key);
        if (q == null) return false;
        long now = System.currentTimeMillis();
        synchronized (q) {
            evict(q, now);
            return q.size() >= MAX_ATTEMPTS;
        }
    }

    /** Called on successful authentication to clear the failure counter. */
    public void recordSuccess(String key) {
        if (key == null || key.isBlank()) return;
        timestamps.remove(key);
    }

    /** Removes timestamps older than the sliding window. */
    private void evict(Deque<Long> q, long now) {
        while (!q.isEmpty() && now - q.peekFirst() > WINDOW_MS) {
            q.pollFirst();
        }
    }
}
