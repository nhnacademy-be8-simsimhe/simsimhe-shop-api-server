package com.simsimbookstore.apiserver.books.contributor.repository;

import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class ContributorRepositroyTest {

    @Autowired
    private ContributorRepositroy contributorRepositroy;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("기여자 이름으로 기여자 조회")
    void findByContributorName() {
        Contributor contributor = Contributor.builder().contributorName("test").contributorRole("지은이")
                .build();

        entityManager.persist(contributor);
        entityManager.flush();

        Optional<Contributor> optionalContributor = contributorRepositroy.findByContributorName("test");
        Contributor findContributor = optionalContributor.get();

        Assertions.assertNotNull(findContributor);
        Assertions.assertEquals(findContributor.getContributorName(), contributor.getContributorName());
        Assertions.assertEquals(findContributor.getContributorRole(), contributor.getContributorRole());
    }

    @Test
    @DisplayName("기여자 이름,역할로 기여자 조회")
    void findByContributorNameAndRoleName() {
        Contributor contributor = Contributor.builder().contributorName("임채환").contributorRole("작가")
                .build();

        entityManager.persist(contributor);
        entityManager.flush();

        Optional<Contributor> optionalContributor = contributorRepositroy.findByContributorNameAndRoleName(contributor.getContributorName(), contributor.getContributorRole());
        Contributor findContributor = optionalContributor.get();


        Assertions.assertNotNull(findContributor);
        Assertions.assertEquals(findContributor.getContributorName(), contributor.getContributorName());
        Assertions.assertEquals(findContributor.getContributorRole(), contributor.getContributorRole());
    }

    @Test
    @DisplayName("기여자 모두 조회")
    void findAllContributors() {
        Contributor contributor1 = Contributor.builder().contributorName("임채환").contributorRole("작가")
                .build();
        Contributor contributo2 = Contributor.builder().contributorName("박신지").contributorRole("지은이")
                .build();
        entityManager.persist(contributor1);
        entityManager.persist(contributo2);

        List<Contributor> allContributors = contributorRepositroy.findAllContributors();
        entityManager.flush();

        Assertions.assertNotNull(allContributors);
        Assertions.assertEquals(allContributors.get(0).getContributorName(), contributor1.getContributorName());
        Assertions.assertEquals(allContributors.get(0).getContributorRole(), contributor1.getContributorRole());
        Assertions.assertEquals(allContributors.get(1).getContributorName(), contributo2.getContributorName());
        Assertions.assertEquals(allContributors.get(1).getContributorRole(), contributo2.getContributorRole());
    }

    @Test
    @DisplayName("페이징 처리된 기여자 목록 조회")
    void findAllWithPagination() {

        IntStream.rangeClosed(1, 15).forEach(i -> {
            Contributor contributor = Contributor.builder()
                    .contributorName("기여자" + i)
                    .contributorRole("역할" + i)
                    .build();
            entityManager.persist(contributor);
        });
        entityManager.flush();

        Pageable pageable = PageRequest.of(0,5);
        Page<Contributor> contributorPage = contributorRepositroy.findAll(pageable);

        Assertions.assertNotNull(contributorPage);
        Assertions.assertEquals(5,contributorPage.getContent().size());
        Assertions.assertEquals(15,contributorPage.getTotalElements());
        Assertions.assertTrue(contributorPage.isFirst());
        Assertions.assertTrue(contributorPage.hasNext());

    }
}