package com.example.demo.config;

import com.example.demo.entity.Category;
import com.example.demo.entity.enums.CategoryType;
import com.example.demo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
// CategoryType enum 값을 기반으로 DB의 tb_category에 초기 데이터 삽입
public class DataInitializer implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() > 0) return;

        for (CategoryType categoryType : CategoryType.values()) {
            Category category = new Category(categoryType.name(), categoryType);
            categoryRepository.save(category);
        }
    }
}
