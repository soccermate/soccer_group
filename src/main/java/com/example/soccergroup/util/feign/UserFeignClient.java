package com.example.soccergroup.util.feign;

import com.example.soccergroup.util.feign.config.UserFeignClientConfig;
import com.example.soccergroup.util.feign.dto.GetUserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import static com.example.soccergroup.config.GlobalStaticVariable.AUTH_CREDENTIALS;

@ReactiveFeignClient(name="${spring.reactive-feign.service-name.user}",
        url="${spring.reactive-feign.service-name.user.url}",
        configuration = UserFeignClientConfig.class
)
public interface UserFeignClient {

    @GetMapping("/user")
    public Mono<GetUserResponseDto> getUser(@RequestHeader(AUTH_CREDENTIALS) String authStr);
}
