package com.simsimbookstore.apiserver.books.bookimage.repoitory;

import com.simsimbookstore.apiserver.books.bookimage.entity.BookImagePath;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookImageRepoisotry extends JpaRepository<BookImagePath,Long> {
}
