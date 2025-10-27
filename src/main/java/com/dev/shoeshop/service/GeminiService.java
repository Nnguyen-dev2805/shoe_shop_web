package com.dev.shoeshop.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {
    private final Client client;
    private final GeminiDataQueryService dataQueryService;
    
    /**
     * Main method: Phân tích intent và trả lời với dữ liệu thực
     */
    public String askGemini(String prompt, Long userId) {
        try {
            // 1. Phân tích intent và lấy dữ liệu liên quan
            String contextData = extractAndQueryData(prompt, userId);
            
            // 2. Tạo system prompt cho chatbot
            String systemPrompt = buildSystemPrompt();
            
            // 3. Tạo full prompt với context
            String fullPrompt = systemPrompt + "\n\n" +
                               "Context/Dữ liệu: " + contextData + "\n\n" +
                               "Câu hỏi của khách hàng: " + prompt + "\n\n" +
                               "Hãy trả lời thân thiện và dựa trên dữ liệu context trên.";
            
            // 4. Gọi Gemini
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    fullPrompt,
                    null
            );

            return formatGeminiResponse(response.text());
        } catch (Exception e) {
            log.error("Error in askGemini: ", e);
            return "Xin lỗi, tôi đang gặp sự cố kỹ thuật. Vui lòng thử lại sau!";
        }
    }
    
    /**
     * Backward compatibility: Version không cần userId
     */
    public String askGemini(String prompt) {
        return askGemini(prompt, null);
    }
    
    /**
     * Phân tích intent và query dữ liệu từ database
     */
    private String extractAndQueryData(String prompt, Long userId) {
        String lowerPrompt = prompt.toLowerCase();
        StringBuilder context = new StringBuilder();
        
        // Intent 0: Tư vấn sản phẩm (cao priority - check trước)
        if (containsAny(lowerPrompt, "tư vấn", "gợi ý", "phù hợp", "nên", "nào", "recommend", "suggest", "đi lại", "hoạt động")) {
            // Đây là request tư vấn - lấy TOÀN BỘ sản phẩm để Gemini có thể phân tích
            context.append("📋 DANH SÁCH SẢN PHẨM CÓ SẴN:\n\n");
            context.append(dataQueryService.getAllProductsForRecommendation()).append("\n\n");
            
            // Thêm categories để Gemini hiểu về phân loại
            context.append("📂 DANH MỤC:\n");
            context.append(dataQueryService.getCategories()).append("\n\n");
            
            context.append("💡 YÊU CẦU: Hãy phân tích nhu cầu của khách hàng và GỢI Ý 2-3 sản phẩm CỤ THỂ từ danh sách trên với LÝ DO phù hợp.\n\n");
        }
        // Intent 1: Tìm sản phẩm cụ thể
        else if (containsAny(lowerPrompt, "tìm", "search", "có", "giày", "sản phẩm", "mua")) {
            // Extract keyword
            String keyword = extractProductKeyword(lowerPrompt);
            if (!keyword.isEmpty()) {
                context.append(dataQueryService.searchProducts(keyword)).append("\n\n");
            } else {
                // Nếu không có keyword cụ thể, show một số sản phẩm mẫu
                context.append(dataQueryService.getAllProductsForRecommendation()).append("\n\n");
            }
        }
        
        // Intent 2: Hỏi về danh mục
        if (containsAny(lowerPrompt, "danh mục", "loại", "category", "phân loại")) {
            context.append(dataQueryService.getCategories()).append("\n\n");
        }
        
        // Intent 3: Hỏi về thương hiệu
        if (containsAny(lowerPrompt, "thương hiệu", "brand", "hãng", "nike", "adidas", "puma")) {
            String brandName = extractBrandName(lowerPrompt);
            if (!brandName.isEmpty()) {
                context.append(dataQueryService.getProductsByBrand(brandName)).append("\n\n");
            } else {
                context.append(dataQueryService.getBrands()).append("\n\n");
            }
        }
        
        // Intent 4: Hỏi về đơn hàng
        if (containsAny(lowerPrompt, "đơn hàng", "order")) {
            if (userId != null) {
                context.append(dataQueryService.getUserOrders(userId)).append("\n\n");
            } else {
                context.append("Để xem đơn hàng, bạn cần đăng nhập.\n\n");
            }
        }
        
        // Intent 5: Hỏi về cửa hàng
        if (containsAny(lowerPrompt, "cửa hàng", "địa chỉ", "liên hệ", "hotline", "giờ mở cửa")) {
            context.append(dataQueryService.getStoreInfo()).append("\n\n");
        }
        
        // Intent 6: Hỏi về chính sách
        if (containsAny(lowerPrompt, "đổi trả", "chính sách", "policy", "hoàn tiền", "return")) {
            context.append(dataQueryService.getReturnPolicy()).append("\n\n");
        }
        
        // Nếu không có context nào, show sản phẩm chung
        if (context.isEmpty()) {
            context.append("Bạn đang ở DeeG Shoe Shop - Cửa hàng giày dép thể thao uy tín.\n\n");
            context.append(dataQueryService.getAllProductsForRecommendation()).append("\n\n");
            context.append("Tôi có thể giúp bạn:\n");
            context.append("- Tư vấn sản phẩm phù hợp\n");
            context.append("- Tìm kiếm sản phẩm\n");
            context.append("- Thông tin đơn hàng\n");
            context.append("- Chính sách đổi trả\n");
        }
        
        return context.toString();
    }
    
    /**
     * Tạo system prompt cho chatbot
     */
    private String buildSystemPrompt() {
        return """
                Bạn là chuyên viên tư vấn giày dép thông minh của DeeG Shoe Shop.
                
                NHIỆM VỤ CHÍNH - TƯ VẤN PROACTIVE:
                - Khi khách hỏi tư vấn, HÃY GỢI Ý 2-3 SẢN PHẨM CỤ THỂ từ danh sách được cung cấp
                - PHÂN TÍCH nhu cầu: tuổi, mục đích sử dụng (đi lại nhiều, thể thao, đi chơi...)
                - ĐƯA RA LÝ DO phù hợp cho mỗi sản phẩm (êm ái, bền, phong cách...)
                - LUÔN đề xuất sản phẩm CÓ TRONG DANH SÁCH, KHÔNG bịa đặt
                
                FORMAT TRẢ LỜI (QUAN TRỌNG):
                - Câu mở đầu ngắn gọn
                - MỖI SẢN PHẨM PHẢI XUỐNG HÀNG (dùng line break)
                - Format: "Số. Tên sản phẩm - Giá"
                - Dòng tiếp theo: "→ Lý do" (dùng mũi tên)
                - Giữa các sản phẩm có 1 dòng trống
                - Emoji đặt ở ĐẦU CÂU hoặc CUỐI CÂU, KHÔNG chen vào giữa từ
                
                VÍ DỤ FORMAT CHUẨN:
                
                Với bạn 20 tuổi đi lại nhiều, mình gợi ý: 😊
                
                1. Nike Air Max 270 - 2,500,000đ
                → Đế Air êm ái, hỗ trợ tốt khi đi bộ lâu 👟
                
                2. Adidas Ultraboost - 2,800,000đ
                → Công nghệ Boost siêu nhẹ, phù hợp di chuyển cả ngày 🏃
                
                3. Vans Old Skool - 1,600,000đ
                → Phong cách trẻ trung, bền bỉ, giá tốt ✨
                
                CÁCH TRẢ LỜI:
                - Ngắn gọn, LUÔN có sản phẩm CỤ THỂ
                - XUỐNG HÀNG rõ ràng giữa các sản phẩm
                - Emoji ở đầu/cuối, KHÔNG chen vào giữa từ
                - CHỈ bảo liên hệ hotline KHI THỰC SỰ không có dữ liệu
                
                LƯU Ý:
                - KHÔNG nói "vui lòng liên hệ" khi ĐÃ CÓ dữ liệu sản phẩm
                - KHÔNG viết emoji vào giữa từ (SAI: "👟 giày", ĐÚNG: "giày 👟" hoặc "👟 Giày...")
                - LUÔN gợi ý từ danh sách có sẵn
                - Trả lời tự nhiên, thân thiện như người bán hàng thực sự
                """;
    }
    
    /**
     * Extract product keyword from user prompt
     */
    private String extractProductKeyword(String prompt) {
        // Danh sách keywords phổ biến về giày
        String[] productKeywords = {
            "nike", "adidas", "puma", "converse", "vans", "sneaker", "thể thao",
            "chạy bộ", "bóng đá", "bóng rổ", "tennis", "sandal", "dép", "boots"
        };
        
        for (String keyword : productKeywords) {
            if (prompt.contains(keyword)) {
                return keyword;
            }
        }
        
        // Fallback: Lấy từ có ý nghĩa
        String[] words = prompt.split("\\s+");
        for (String word : words) {
            if (word.length() > 3 && !isCommonWord(word)) {
                return word;
            }
        }
        
        return "";
    }
    
    /**
     * Extract brand name from prompt
     */
    private String extractBrandName(String prompt) {
        String[] brands = {"nike", "adidas", "puma", "converse", "vans", "reebok", "new balance"};
        
        for (String brand : brands) {
            if (prompt.contains(brand)) {
                return brand;
            }
        }
        
        return "";
    }
    
    /**
     * Check if prompt contains any of the keywords
     */
    private boolean containsAny(String prompt, String... keywords) {
        for (String keyword : keywords) {
            if (prompt.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if word is common (to filter out)
     */
    private boolean isCommonWord(String word) {
        String[] commonWords = {"có", "là", "của", "cho", "trong", "với", "được", "này", "đó", "gì", "như", "về"};
        for (String common : commonWords) {
            if (word.equals(common)) {
                return true;
            }
        }
        return false;
    }
    
    private String formatGeminiResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return "Xin lỗi, tôi không thể trả lời câu hỏi này.";
        }
        
        // Loại bỏ markdown formatting nhưng GIỮ line breaks
        String formatted = response
            // Loại bỏ **bold** và __bold__
            .replaceAll("\\*\\*(.*?)\\*\\*", "$1")
            .replaceAll("__(.*?)__", "$1")
            // Loại bỏ *italic* và _italic_ (nhưng không phải * đầu dòng cho bullet point)
            .replaceAll("\\s\\*(\\S.*?)\\*\\s", " $1 ")
            .replaceAll("\\s_(\\S.*?)_\\s", " $1 ")
            // Loại bỏ `code`
            .replaceAll("`(.*?)`", "$1")
            // Loại bỏ # headers
            .replaceAll("^#+\\s*", "")
            .replaceAll("\n#+\\s*", "\n")
            // Loại bỏ [link](url) - chỉ giữ text
            .replaceAll("\\[([^\\]]+)\\]\\([^\\)]+\\)", "$1")
            // Chuẩn hóa line breaks
            .replaceAll("\r\n", "\n")  // Windows line ending
            .replaceAll("\r", "\n")    // Old Mac line ending
            // Loại bỏ > 2 dòng trống liên tiếp
            .replaceAll("\n{3,}", "\n\n")
            .trim();
        
        // KHÔNG thêm emoji tự động nữa (để Gemini tự thêm theo hướng dẫn)
        // formatted = addEmojis(formatted);
        
        return formatted;
    }
    
    /**
     * DEPRECATED: Không dùng auto emoji inject nữa
     * Để Gemini tự thêm emoji theo system prompt
     */
    @Deprecated
    private String addEmojis(String text) {
        // KHÔNG thêm emoji tự động vì:
        // 1. Làm rối format (chen vào giữa từ)
        // 2. Gemini đã tự thêm emoji theo hướng dẫn
        // 3. Duplicate emoji không cần thiết
        return text;
    } 
}
