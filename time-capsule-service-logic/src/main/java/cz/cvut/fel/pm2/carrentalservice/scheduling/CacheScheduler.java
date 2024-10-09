package cz.cvut.fel.pm2.carrentalservice.scheduling;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CacheScheduler {

    private final CacheManager cacheManager;

    @Scheduled(cron = "0 0 1 * * *")
    public void evictAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName ->
                    Optional.ofNullable(cacheManager.getCache(cacheName))
                        .ifPresent(Cache::clear));
    }

}
