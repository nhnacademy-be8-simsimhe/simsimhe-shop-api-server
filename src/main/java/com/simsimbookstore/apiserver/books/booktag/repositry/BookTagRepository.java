package com.simsimbookstore.apiserver.books.booktag.repositry;

import com.simsimbookstore.apiserver.books.booktag.entity.BookTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookTagRepository extends JpaRepository<BookTag,Long> {
}
