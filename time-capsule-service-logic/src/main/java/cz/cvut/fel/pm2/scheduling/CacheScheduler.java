package cz.cvut.fel.pm2.scheduling;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Scheduler component for evicting all caches at a specified interval.
 */
@Component
@RequiredArgsConstructor
public class CacheScheduler {

    private final CacheManager cacheManager;

    /**
     * Evicts all caches at 1 AM every day.
     * The cron expression "0 0 1 * * *" represents this schedule.
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void evictAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName ->
                    Optional.ofNullable(cacheManager.getCache(cacheName))
                        .ifPresent(Cache::clear));
    }

}
