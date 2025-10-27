package com.dev.shoeshop.dto.gemini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeminiChatRequest {
    /**
     * Câu hỏi/message của user (required)
     */
    private String message;
    
    /**
     * ⚠️ SECURITY NOTE: Field này sẽ bị IGNORE
     * 
     * userId sẽ được lấy từ authenticated user (Security Context/Session)
     * KHÔNG tin tưởng userId từ client để tránh unauthorized access
     * 
     * @deprecated Kept for backward compatibility, but will be ignored by backend
     */
    @Deprecated
    private Long userId;
}

