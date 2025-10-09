package com.dev.shoeshop.controller;

import com.dev.shoeshop.controller.api.PasswordResetApiController;
import com.dev.shoeshop.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PasswordResetApiController.class)
public class PasswordResetApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSendVerificationCode_Success() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("email", "test@example.com");

        doNothing().when(userService).sendPasswordResetCode(anyString());

        // When & Then
        mockMvc.perform(post("/api/password-reset/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());

        verify(userService, times(1)).sendPasswordResetCode("test@example.com");
    }

    @Test
    public void testSendVerificationCode_EmptyEmail() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("email", "");

        // When & Then
        mockMvc.perform(post("/api/password-reset/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email không được để trống."));

        verify(userService, never()).sendPasswordResetCode(anyString());
    }

    @Test
    public void testVerifyCode_Success() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("resetEmail", "test@example.com");

        Map<String, String> request = new HashMap<>();
        request.put("code", "123456");

        when(userService.verifyResetCode("test@example.com", "123456")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/password-reset/verify-code")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());

        verify(userService, times(1)).verifyResetCode("test@example.com", "123456");
    }

    @Test
    public void testVerifyCode_InvalidCode() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("resetEmail", "test@example.com");

        Map<String, String> request = new HashMap<>();
        request.put("code", "000000");

        when(userService.verifyResetCode("test@example.com", "000000")).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/password-reset/verify-code")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Mã xác nhận không hợp lệ hoặc đã hết hạn. Vui lòng thử lại."));
    }

    @Test
    public void testResetPassword_Success() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("resetEmail", "test@example.com");
        session.setAttribute("isCodeVerified", true);

        Map<String, String> request = new HashMap<>();
        request.put("newPassword", "newpassword123");
        request.put("confirmPassword", "newpassword123");

        doNothing().when(userService).resetPassword("test@example.com", "newpassword123");

        // When & Then
        mockMvc.perform(post("/api/password-reset/reset-password")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());

        verify(userService, times(1)).resetPassword("test@example.com", "newpassword123");
    }

    @Test
    public void testResetPassword_PasswordMismatch() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("resetEmail", "test@example.com");
        session.setAttribute("isCodeVerified", true);

        Map<String, String> request = new HashMap<>();
        request.put("newPassword", "newpassword123");
        request.put("confirmPassword", "differentpassword");

        // When & Then
        mockMvc.perform(post("/api/password-reset/reset-password")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Mật khẩu xác nhận không khớp. Vui lòng thử lại."));

        verify(userService, never()).resetPassword(anyString(), anyString());
    }

    @Test
    public void testGetSessionStatus() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("resetEmail", "test@example.com");
        session.setAttribute("isEmailSent", true);
        session.setAttribute("isCodeVerified", false);

        // When & Then
        mockMvc.perform(get("/api/password-reset/session-status")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasEmail").value(true))
                .andExpect(jsonPath("$.isEmailSent").value(true))
                .andExpect(jsonPath("$.isCodeVerified").value(false));
    }
}
