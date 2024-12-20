package com.simsimbookstore.apiserver.books.contributor.repository;

import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContributorRepositroy extends JpaRepository<Contributor,Long> {

    @Query("select c from Contributor as c where c.contributorName = :contributorName")
    Optional<Contributor> findByContributorName(@Param("contributorName") String contributorName);

    @Query("select c from Contributor as c where c.contributorName = :contributorName and c.contributorRole = :contributorRole")
    Optional<Contributor> findByContributorNameAndRoleName(@Param("contributorName") String contributorName, @Param("contributorRole") String contributorRole);

    @Query("select c from Contributor as c")
    List<Contributor> findAllContributors();

    Page<Contributor> findAll(Pageable pageable);
}
