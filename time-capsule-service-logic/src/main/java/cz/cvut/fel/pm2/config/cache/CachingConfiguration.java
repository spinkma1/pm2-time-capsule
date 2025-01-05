package cz.cvut.fel.pm2.config.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CachingConfiguration {

    /**
     * Configures the cache manager with specified cache names.
     *
     * @return the cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("capsules", "users");
    }

}