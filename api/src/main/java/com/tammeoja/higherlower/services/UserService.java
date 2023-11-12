package com.tammeoja.higherlower.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Clock clock;
    public void setAsUser(HttpServletResponse response) {
        var cookie = ResponseCookie.from("userId").value(randomUUID().toString())
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(LocalDateTime.now().plusYears(100).toEpochSecond(ZoneOffset.UTC))
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
