package com.simsimbookstore.apiserver.books.tag.repository;

import com.simsimbookstore.apiserver.books.tag.domain.Tag;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
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
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("태그 이름으로 태그 조회")
    void findByTagName() {
        Tag tag = Tag.builder().tagName("테스트태그").isActivated(true).build();
        entityManager.persist(tag);
        entityManager.flush();

        Optional<Tag> optionalTag = tagRepository.findByTagName("테스트태그");
        Tag findTag = optionalTag.get();

        Assertions.assertNotNull(findTag);
        Assertions.assertEquals(findTag.getTagName(), tag.getTagName());
    }


    @Test
    @DisplayName("활성화된 태그 조회")
    void findALlActivated() {
        Tag tag1 = Tag.builder().tagName("테스트태그1").isActivated(true).build();
        Tag tag2 = Tag.builder().tagName("테스트태그2").isActivated(true).build();
        entityManager.persist(tag1);
        entityManager.persist(tag2);
        entityManager.flush();

        List<Tag> allActivated = tagRepository.findAllActivated();
        Assertions.assertNotNull(allActivated);
        Assertions.assertEquals("테스트태그1",allActivated.get(0).getTagName());
        Assertions.assertEquals("테스트태그2",allActivated.get(1).getTagName());

    }

}