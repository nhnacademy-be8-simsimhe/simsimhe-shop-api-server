package com.simsimbookstore.apiserver.books.category.repository;

import com.simsimbookstore.apiserver.books.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.categoryName = :categoryName AND c.parent = :parent")
    Optional<Category> findByCategoryNameAndParent(@Param("categoryName") String categoryName, @Param("parent") Category parent);

    //부모 카테고리가 없는 카테고리 조회
    @Query("SELECT c FROM Category c WHERE c.categoryName = :categoryName AND c.parent IS NULL")
    Optional<Category> findByCategoryNameAndParentIsNull(@Param("categoryName") String categoryName);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parent ORDER BY c.categoryId ASC")
    List<Category> findAllOrderedById();


    @Query("SELECT c FROM Category c WHERE c.parent = :parent")
    List<Category> findAllByParent(@Param("parent") Category parent);

}
