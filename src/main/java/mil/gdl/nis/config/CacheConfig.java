package mil.gdl.nis.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;


@EnableCaching
@Configuration
public class CacheConfig {
	
	/*
	 * @Bean public CacheManager cacheManager() { SimpleCacheManager
	 * simpleCacheaManager = new SimpleCacheManager();
	 * 
	 * simpleCacheaManager.setCaches(Arrays.asList(new
	 * ConcurrentMapCache("sessionStore"))); return simpleCacheaManager; }
	 */
}
