package com.simsimbookstore.apiserver.books.tag.repository;

import com.simsimbookstore.apiserver.books.tag.domain.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("select t from Tag as t where t.tagName = :tagName")
    Optional<Tag> findByTagName(@Param("tagName") String tagName);

    Page<Tag> findAll(Pageable pageable);

    @Query("SELECT t FROM Tag t")
    List<Tag> findAllTags();
}
