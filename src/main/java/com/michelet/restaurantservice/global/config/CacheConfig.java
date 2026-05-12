package com.michelet.restaurantservice.global.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@EnableCaching
@Configuration
@EnableConfigurationProperties(CacheTtlProperties.class)
public class CacheConfig {

    private static final long DEFAULT_CACHE_TTL_SECONDS = 300L;

    private final CacheTtlProperties cacheTtlProperties;

    public CacheConfig(CacheTtlProperties cacheTtlProperties) {
        this.cacheTtlProperties = cacheTtlProperties;
    }


    // Redis 기반 Spring CacheManager를 설정
    // restaurantDetail: 식당 상세 조회
    // restaurantCourses: 코스 목록 조회
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(redisValueSerializer())
                )
                .entryTtl(Duration.ofSeconds(DEFAULT_CACHE_TTL_SECONDS));

        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                "restaurantDetail",
                defaultCacheConfiguration.entryTtl(Duration.ofSeconds(cacheTtlProperties.restaurantDetail())),

                "restaurantCourses",
                defaultCacheConfiguration.entryTtl(Duration.ofSeconds(cacheTtlProperties.restaurantCourses()))
        );

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    // Redis value 직렬화 방식을 설정
    private GenericJackson2JsonRedisSerializer redisValueSerializer() {
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

}
