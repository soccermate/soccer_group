package com.example.soccergroup.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalConfig {

    ObjectMapper objectMapper()
    {
        return new ObjectMapper();
    }
}
