package com.tammeoja.higherlower.services;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserServiceTest {
    Clock clock = Clock.fixed(Instant.parse("2023-11-12T11:55:55.00Z"), ZoneOffset.systemDefault());
    UserService service = new UserService(clock);

    HttpServletResponse response = mock();

    @Test
    void setAsUser() {
        service.setAsUser(response);

        var cookieCaptor = ArgumentCaptor.forClass(String.class);

        verify(response).addHeader(eq("Set-Cookie"), cookieCaptor.capture());
        assertThat(cookieCaptor.getValue()).containsAnyOf(
                "userId=", "Path=/; Secure; HttpOnly", "4855506755",
                "Expires=Tue, 23 Sep 2177 21:45:10 GMT"
        );
        assertThat(cookieCaptor.getValue().split(";")).hasSize(6);
    }
}