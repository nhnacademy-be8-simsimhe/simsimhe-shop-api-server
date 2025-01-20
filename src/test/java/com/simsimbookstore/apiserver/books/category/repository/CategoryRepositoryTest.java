package com.simsimbookstore.apiserver.books.category.repository;

import com.simsimbookstore.apiserver.books.category.entity.Category;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import jakarta.persistence.EntityManager;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;


@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUP() {
        entityManager.clear();
    }

    @Test
    @DisplayName("카테고리 이름과 부모 카테고리로 조회")
    void findByCategoryNameAndParent() {
        Category parentCategory = Category.builder()
                .categoryName("부모카테고리")
                .build();

        Category childCategory = Category.builder()
                .categoryName("자식카테고리")
                .parent(parentCategory)
                .build();

        entityManager.persist(parentCategory);
        entityManager.persist(childCategory);
        entityManager.flush();

        Optional<Category> optionalCategory = categoryRepository.findByCategoryNameAndParent("자식카테고리", parentCategory);
        Assertions.assertNotNull(optionalCategory);
        Assertions.assertEquals(optionalCategory.get().getCategoryName(), childCategory.getCategoryName());
        Assertions.assertEquals(optionalCategory.get().getParent().getCategoryName(), parentCategory.getCategoryName());
    }

    @Test
    @DisplayName("부모 카테고리가 없는 카테고리 조회")
    void findByCategoryNameAndParentIsNull() {
        Category category = Category.builder()
                .categoryName("레츠고도리")
                .build();

        entityManager.persist(category);
        entityManager.flush();

        Optional<Category> optionalCategory = categoryRepository.findByCategoryNameAndParentIsNull(category.getCategoryName());

        Assertions.assertEquals(optionalCategory.get().getCategoryName(), category.getCategoryName());
        Assertions.assertNull(optionalCategory.get().getParent());
    }

    @Test
    @DisplayName("ID 기준 정렬된 전체 카테고리 조회")
    void findAllOrderedById() {
        // Given
        Category category1 = Category.builder().categoryName("철학").build();
        Category category2 = Category.builder().categoryName("역사").build();
        entityManager.persist(category1);
        entityManager.persist(category2);
        entityManager.flush();

        List<Category> categories = categoryRepository.findAllOrderedById();

        Assertions.assertEquals(categories.get(0).getCategoryName(), "철학");
        Assertions.assertEquals(categories.get(1).getCategoryName(), "역사");
        Assertions.assertEquals(2, categories.size());
    }

    @Test
    @DisplayName("부모 카테고리를 기준으로 자식 카테고리 조회")
    void findAllByParent() {
        // Given
        Category parentCategory = Category.builder()
                .categoryName("과학")
                .build();

        Category child1 = Category.builder()
                .categoryName("물리학")
                .parent(parentCategory)
                .build();

        Category child2 = Category.builder()
                .categoryName("생물학")
                .parent(parentCategory)
                .build();

        entityManager.persist(parentCategory);
        entityManager.persist(child1);
        entityManager.persist(child2);
        entityManager.flush();

        List<Category> categories = categoryRepository.findAllByParent(parentCategory);
        Assertions.assertEquals(2,categories.size());
        Assertions.assertEquals(categories.get(0).getCategoryName(),"물리학");
        Assertions.assertEquals(categories.get(1).getCategoryName(),"생물학");

    }

}