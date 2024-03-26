package com.nhnacademy.inkbridge.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * class: RedisConfig.
 *
 * @author devminseo
 * @version 2/23/24
 */
@EnableRedisHttpSession(redisNamespace = "redis:session")
@Configuration
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "inkbridge.redis")
public class RedisConfig implements BeanClassLoaderAware {
    private final KeyMangerConfig keyMangerConfig;
    private String host;
    private String port;
    private String password;
    private String database;
    private ClassLoader classLoader;

    /**
     * redis 연결 위한 빈 설정
     *
     * @return factory 반환
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(keyMangerConfig.keyStore(host));
        configuration.setPort(Integer.parseInt(keyMangerConfig.keyStore(port)));
        configuration.setPassword(keyMangerConfig.keyStore(password));
        configuration.setDatabase(Integer.parseInt(keyMangerConfig.keyStore(database)));

        return new LettuceConnectionFactory(configuration);
    }

    /**
     * redis 직렬화 설정 메서드.
     *
     * @return redis 서버에 crud 가능한 객체 반환
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());

        return redisTemplate;
    }


    private ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(SecurityJackson2Modules.getModules(classLoader));

        return objectMapper;
    }
    @Bean
    public RedisSerializer<Object> sessionRedis() {
        return new GenericJackson2JsonRedisSerializer(objectMapper());
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
