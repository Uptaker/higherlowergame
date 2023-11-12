package com.tammeoja.higherlower.services;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserServiceTest {
    UserService service = new UserService();

    HttpServletResponse response = mock();

    @Test
    void setAsUser() {
        service.setAsUser(response);

        var cookieCaptor = ArgumentCaptor.forClass(String.class);

        verify(response).addHeader(eq("Set-Cookie"), cookieCaptor.capture());
        assertThat(cookieCaptor.getValue()).containsAnyOf("userId=", "Path=/; Secure; HttpOnly");
        assertThat(cookieCaptor.getValue().split(";")).hasSize(4);
    }
}