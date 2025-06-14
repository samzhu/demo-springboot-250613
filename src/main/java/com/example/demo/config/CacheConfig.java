package com.example.demo.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * 快取配置類別
 * 使用 @EnableCaching 啟用 Spring 的快取功能
 * 
 * 注意：
 * 1. Spring Boot 會自動配置預設的 ConcurrentMapCacheManager
 * 2. 在生產環境中，建議在 application.yml 中配置 Redis 等分散式快取
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * 書本快取的名稱常量
     * 用於在 @Cacheable 和 @CacheEvict 註解中引用
     */
    public static final String BOOKS_CACHE = "books";
} 