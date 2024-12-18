package com.simsimbookstore.apiserver.books.bookcontributor.repository;

import com.simsimbookstore.apiserver.books.bookcontributor.entity.BookContributor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookContributorRepository extends JpaRepository<BookContributor,Long> {
}
