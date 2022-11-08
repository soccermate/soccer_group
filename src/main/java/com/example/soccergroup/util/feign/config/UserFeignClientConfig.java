package com.example.soccergroup.util.feign.config;

import com.example.soccergroup.util.feign.UserFeignClient;
import feign.FeignException;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactivefeign.ReactiveOptions;
import reactivefeign.client.log.DefaultReactiveLogger;
import reactivefeign.client.log.ReactiveLoggerListener;
import reactivefeign.retry.BasicReactiveRetryPolicy;
import reactivefeign.retry.FilteredReactiveRetryPolicy;
import reactivefeign.retry.ReactiveRetryPolicy;
import reactivefeign.webclient.WebReactiveOptions;

import java.time.Clock;

@Configuration
public class UserFeignClientConfig {

    @Bean
    public ReactiveOptions reactiveOptions(){
        return new WebReactiveOptions.Builder()
                .setReadTimeoutMillis(2000)
                .setWriteTimeoutMillis(2000)
                .setResponseTimeoutMillis(2000)
                .build();
    }

    @Bean
    public ReactiveLoggerListener loggerListener(){
        return new DefaultReactiveLogger(Clock.systemUTC(), LoggerFactory.getLogger(UserFeignClient.class.getName()));
    }

    @Bean
    public ReactiveRetryPolicy reactiveRetryPolicy(){
        return FilteredReactiveRetryPolicy
                .notRetryOn(
                        BasicReactiveRetryPolicy.retryWithBackoff(3, 100),
                        FeignException.FeignClientException.class
                );

    }
}
