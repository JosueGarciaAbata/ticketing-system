package com.josue.ticketing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * Configuración de conexión a Redis.
 */
@Configuration
public class RedisConfig {

    @Value("${REDIS_HOST:localhost}")
    private static String REDIS_HOST;

    @Value("${REDIS_PORT:6379}")
    private static int REDIS_PORT;

    /**
     * Crea la fábrica de conexiones a Redis usando Lettuce.
     * 
     * @return fábrica de conexiones configurada para localhost:6379
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(REDIS_HOST, REDIS_PORT));
    }

}
