package com.tammeoja.higherlower.services;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import static java.util.UUID.randomUUID;

@Service
public class UserService {
    public void setAsUser(HttpServletResponse response) {
        var cookie = ResponseCookie.from("userId").value(randomUUID().toString()).httpOnly(true).path("/").secure(true).build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
