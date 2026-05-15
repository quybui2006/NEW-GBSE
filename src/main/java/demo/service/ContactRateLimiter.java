package demo.service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 * Simple in-memory per-IP rate limiter for the public contact form.
 * Allows MAX_REQUESTS submissions per IP within WINDOW_MS milliseconds.
 * No external dependencies required.
 */
@Service
public class ContactRateLimiter {

    private static final int  MAX_REQUESTS = 3;
    private static final long WINDOW_MS    = 10 * 60 * 1000L; // 10 minutes

    private final Map<String, Deque<Long>> ipTimestamps = new ConcurrentHashMap<>();

    public boolean isAllowed(String ip) {
        long now = System.currentTimeMillis();
        Deque<Long> timestamps = ipTimestamps.computeIfAbsent(ip, k -> new ArrayDeque<>());

        synchronized (timestamps) {
            // Evict timestamps older than the sliding window.
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > WINDOW_MS) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= MAX_REQUESTS) {
                return false;
            }
            timestamps.addLast(now);
            return true;
        }
    }
}
