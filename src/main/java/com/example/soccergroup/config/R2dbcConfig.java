package com.example.soccergroup.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class R2dbcConfig {

    @Bean
    ReactiveTransactionManager r2dbcTransactionManager(ConnectionFactory connectionFactory)
    {
        return new R2dbcTransactionManager(connectionFactory);
    }
}
