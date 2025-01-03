package com.simsimbookstore.apiserver.carts.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simsimbookstore.apiserver.books.book.entity.QBook;
import com.simsimbookstore.apiserver.books.bookimage.entity.BookImagePath;
import com.simsimbookstore.apiserver.books.bookimage.entity.QBookImagePath;
import com.simsimbookstore.apiserver.carts.dto.CartResponseDto;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class CartCustomRepositoryImpl implements CartCustomRepository {

    private final JPAQueryFactory queryFactory;

    QBook book = QBook.book;
    QBookImagePath bookImagePath = QBookImagePath.bookImagePath;


    /**
     * 장바구니에 있는 도서들 조회
     * @param bookId
     * @return
     */
    @Override
    public CartResponseDto getBookForCart(Long bookId) {
        return queryFactory
                .select(Projections.fields(CartResponseDto.class
                        , book.bookId
                        , book.title
                        , book.saleprice.as("price")
                        , bookImagePath.imagePath.as("imagePath")
                        , book.quantity.as("bookQuantity")))
                .from(book)
                .innerJoin(bookImagePath).on(book.bookId.eq(bookImagePath.book.bookId))
                .where(book.bookId.eq(bookId).and(bookImagePath.imageType.eq(BookImagePath.ImageType.THUMBNAIL)))
                .fetchOne();

    }
}
