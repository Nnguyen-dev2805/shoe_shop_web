package com.dev.shoeshop.dto.gemini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeminiChatResponse {
    private String response;
    private boolean success;
    private String error;
    
    public static GeminiChatResponse success(String response) {
        return GeminiChatResponse.builder()
                .response(response)
                .success(true)
                .build();
    }
    
    public static GeminiChatResponse error(String error) {
        return GeminiChatResponse.builder()
                .response("Xin lỗi, tôi đang gặp sự cố kỹ thuật. Vui lòng thử lại sau!")
                .success(false)
                .error(error)
                .build();
    }
}

