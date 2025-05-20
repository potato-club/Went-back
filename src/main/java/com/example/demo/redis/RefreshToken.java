package com.example.demo.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24) //24시간 (초 단위)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private String email;  // key 역할 (식별자)


    private String token;  // 저장할 refresh 토큰

    public void setToken(String token) {
        this.token = token;
    }
}
