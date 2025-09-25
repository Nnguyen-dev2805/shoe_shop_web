package com.dev.shoeshop.config;

import com.dev.shoeshop.dto.category.CategoryResponse;
import com.dev.shoeshop.entity.Category;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // rule cho Category
        modelMapper.typeMap(Category.class, CategoryResponse.class).addMappings(mapper -> {
            mapper.map(src -> src.getProducts() != null ? src.getProducts().size() : 0,
                    CategoryResponse::setProductCount);
        });


        return modelMapper;
    }
}
