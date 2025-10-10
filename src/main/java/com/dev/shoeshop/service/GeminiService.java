package com.dev.shoeshop.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeminiService {
    private final Client client;
    public String askGemini(String prompt)
    {
        try {
            GenerateContentResponse response =
                client.models.generateContent(
                    "gemini-2.5-flash",
                    "hãy trả lời đơn giản câu hỏi sau không dài dòng: " + prompt,
                    null);

            return formatGeminiResponse(response.text());
        } 
        catch (Exception e) {
            return "Xin lỗi, tôi đang gặp sự cố kỹ thuật. Vui lòng thử lại sau!";
        }
    }
    
    private String formatGeminiResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return "Xin lỗi, tôi không thể trả lời câu hỏi này.";
        }
        
        // Loại bỏ markdown formatting
        String formatted = response
            // Loại bỏ **bold** và __bold__
            .replaceAll("\\*\\*(.*?)\\*\\*", "$1")
            .replaceAll("__(.*?)__", "$1")
            // Loại bỏ *italic* và _italic_
            .replaceAll("\\*(.*?)\\*", "$1")
            .replaceAll("_(.*?)_", "$1")
            // Loại bỏ `code`
            .replaceAll("`(.*?)`", "$1")
            // Loại bỏ # headers
            .replaceAll("^#+\\s*", "")
            // Loại bỏ [link](url) - chỉ giữ text
            .replaceAll("\\[([^\\]]+)\\]\\([^\\)]+\\)", "$1")
            // Loại bỏ các ký tự đặc biệt khác
            .replaceAll("\\*", "")
            .replaceAll("_", "")
            .replaceAll("`", "")
            // Loại bỏ khoảng trắng thừa
            .replaceAll("\\s+", " ")
            .trim();
        
        // Thêm emoji cho các từ khóa
        formatted = addEmojis(formatted);
        
        return formatted;
    }
    
    private String addEmojis(String text) {
        // Thêm emoji cho các từ khóa liên quan đến shoe shop
        text = text.replaceAll("(?i)\\bgiày\\b", "👟 giày");
        text = text.replaceAll("(?i)\\bgiày dép\\b", "👟 giày dép");
        text = text.replaceAll("(?i)\\bsize\\b", "📏 size");
        text = text.replaceAll("(?i)\\bmàu\\b", "🎨 màu");
        text = text.replaceAll("(?i)\\bgiá\\b", "💰 giá");
        text = text.replaceAll("(?i)\\bđặt hàng\\b", "🛒 đặt hàng");
        text = text.replaceAll("(?i)\\bthanh toán\\b", "💳 thanh toán");
        text = text.replaceAll("(?i)\\bgiao hàng\\b", "🚚 giao hàng");
        text = text.replaceAll("(?i)\\bđổi trả\\b", "🔄 đổi trả");
        text = text.replaceAll("(?i)\\bkhuyến mãi\\b", "🎉 khuyến mãi");
        text = text.replaceAll("(?i)\\bgiảm giá\\b", "🏷️ giảm giá");
        text = text.replaceAll("(?i)\\bchất lượng\\b", "⭐ chất lượng");
        text = text.replaceAll("(?i)\\bthương hiệu\\b", "🏷️ thương hiệu");
        text = text.replaceAll("(?i)\\bthể thao\\b", "⚽ thể thao");
        text = text.replaceAll("(?i)\\bchạy bộ\\b", "🏃 chạy bộ");
        text = text.replaceAll("(?i)\\bđi bộ\\b", "🚶 đi bộ");
        text = text.replaceAll("(?i)\\bbasketball\\b", "🏀 basketball");
        text = text.replaceAll("(?i)\\bfootball\\b", "⚽ football");
        text = text.replaceAll("(?i)\\btennis\\b", "🎾 tennis");
        
        return text;
    } 
}
