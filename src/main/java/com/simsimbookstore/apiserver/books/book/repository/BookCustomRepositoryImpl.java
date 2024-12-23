package com.simsimbookstore.apiserver.books.book.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simsimbookstore.apiserver.books.book.dto.BookListResponse;
import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.entity.QBook;
import com.simsimbookstore.apiserver.books.bookcategory.entity.QBookCategory;
import com.simsimbookstore.apiserver.books.bookcontributor.entity.QBookContributor;
import com.simsimbookstore.apiserver.books.booktag.entity.QBookTag;
import com.simsimbookstore.apiserver.books.category.entity.QCategory;
import com.simsimbookstore.apiserver.books.contributor.entity.QContributor;
import com.simsimbookstore.apiserver.books.tag.domain.QTag;
import com.simsimbookstore.apiserver.like.entity.QBookLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

public class BookCustomRepositoryImpl implements BookCustomRepository {

    private final JPAQueryFactory queryFactory;

    QBook book = QBook.book;
    QBookContributor bookContributor = QBookContributor.bookContributor;
    QCategory category = QCategory.category;
    QBookCategory bookCategory = QBookCategory.bookCategory;
    QContributor contributor = QContributor.contributor;
    QTag tag = QTag.tag;
    QBookTag qBookTag = QBookTag.bookTag;
    QBookLike bookLike = QBookLike.bookLike;

    public BookCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    /**
     * 가장 최근에 출판된 책 5권을 조회하는 메서드
     *
     * @return
     */
    @Override
    public List<BookListResponse> getNewBookList() {
        return queryFactory
                .select(Projections.fields(BookListResponse.class,
                        book.bookId.as("bookId"),                  // Book 엔티티의 ID
                        book.title.as("title"),                // 책 제목
                        book.publicationDate.as("publicationDate"), // 출판일
                        book.price.as("price"),                // 가격
                        book.saleprice.as("saleprice"),        // 세일 가격
                        book.publisher.as("publisher"),   // 출판사 이름
                        book.bookStatus.as("bookStatus"),      // 책 상태
                        book.quantity.as("quntity")            // 재고 수량
                        // 필요한 경우 추가 필드를 여기에 매핑
                ))
                .from(book)
                .orderBy(book.publicationDate.desc()) // 출판일 기준 최신순 정렬
                .limit(5)                           // 상위 5권만 조회
                .fetch();                           // 결과 가져오기
    }

    /**
     * 모든 책 조회
     *
     * @param pageable
     * @return
     */
    @Override
    public Page<BookListResponse> getAllBook(Pageable pageable) {
        // QueryDSL로 데이터 조회
        List<BookListResponse> content = queryFactory
                .select(Projections.fields(BookListResponse.class,
                        book.bookId.as("bookId"),
                        book.title.as("title"),
                        book.publicationDate.as("publicationDate"),
                        book.price.as("price"),
                        book.saleprice.as("saleprice"),
                        book.publisher.as("publisher"),
                        book.bookStatus.as("bookStatus"),
                        book.quantity.as("quntity")
                ))
                .from(book)
                .offset(pageable.getOffset()) // 페이지 시작점
                .limit(pageable.getPageSize()) // 페이지 크기
                .orderBy(book.publicationDate.desc()) // 출판일 기준 최신순 정렬
                .fetch();

        // 전체 데이터 수 조회 (페이징 처리에 필요)
        long total = queryFactory
                .select(book.count())
                .from(book)
                .fetchOne();

        // Page 객체로 반환
        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }


    @Override
    public Page<BookListResponse> getBookListByCategory(Long userId, Long categoryId, Pageable pageable) {
        return null;
    }

    @Override
    public BookResponseDto getBookDetail(Long userId, Long bookId) {
        return null;
    }


    /**
     * 받은 카테고리 아이디에서 최하위 카테고리만 얻는 메서드
     *
     * @param categoryIdList
     * @return
     */
    @Override
    public List<Long> getLowestCategoryId(List<Long> categoryIdList) {
        return queryFactory
                .select(category.categoryId)
                .from(category)
                .leftJoin(category.children, QCategory.category) // 자식 카테고리 조인
                .where(category.categoryId.in(categoryIdList).and(category.children.isEmpty())) // 자식이 없는 카테고리(리프 노드)
                .fetch();
    }


    @Override
    public Page<BookListResponse> getUserLikeBook(Pageable pageable, Long userId) {
        return null;
    }
}
