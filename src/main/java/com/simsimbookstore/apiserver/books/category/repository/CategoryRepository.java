package com.simsimbookstore.apiserver.books.category.repository;

import com.simsimbookstore.apiserver.books.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.categoryName = :categoryName AND c.parent = :parent")
    Optional<Category> findByCategoryNameAndParent(@Param("categoryName") String categoryName, @Param("parent") Category parent);

    @Query("SELECT c FROM Category c WHERE c.categoryName = :categoryName AND c.parent IS NULL")
    Optional<Category> findByCategoryNameAndParentIsNull(@Param("categoryName") String categoryName);

}
