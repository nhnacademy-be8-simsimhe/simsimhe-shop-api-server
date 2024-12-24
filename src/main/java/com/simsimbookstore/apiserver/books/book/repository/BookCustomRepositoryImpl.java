package com.simsimbookstore.apiserver.books.book.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
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
                        book.quantity.as("quantity")            // 재고 수량
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
                        book.quantity.as("quantity")
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

    /**
     * 책 상세조회
     *
     * @param userId
     * @param bookId
     * @return
     */
    @Override
    public BookResponseDto getBookDetail(Long userId, Long bookId) {
// 좋아요 여부를 설정
        BooleanExpression isLiked = getLikeExpression(userId);
        // 책 상세 정보를 조회
        BookResponseDto bookResponse = queryFactory
                .select(Projections.fields(BookResponseDto.class,
                        book.bookId.as("bookId"),
                        book.title.as("title"),
                        book.description.as("description"),
                        book.bookIndex.as("bookIndex"),
                        book.publisher.as("publisher"),
                        book.isbn.as("isbn"),
                        book.viewCount.as("viewCount"),
                        book.price.as("price"),
                        book.saleprice.as("saleprice"),
                        book.publicationDate.as("publicationDate"),
                        book.pages.as("pages"),
                        book.quantity.as("quantity"),
                        isLiked.as("isLiked"),
                        book.bookStatus.as("bookStatus")
                ))
                .from(book)
                .leftJoin(bookLike).on(book.bookId.eq(bookLike.book.bookId))
                .where(book.bookId.eq(bookId))
                .groupBy(book, bookLike)
                .fetchOne();

        // 조회수 증가
        this.addViewCount(book, bookId);

        return bookResponse;

    }

    /**
     * 도서 상세정보에 대한 객체를 반환하는 메서드
     * @param bookId
     * @param bookResponseDto
     * @return
     */
    private BookResponseDto toResponse(Long bookId, BookResponseDto bookResponseDto){
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
        // 입력이 없는 경우 빈 리스트 반환
        if (categoryIdList == null || categoryIdList.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .select(category.categoryId)
                .from(category)
                .leftJoin(category.children, QCategory.category) // 자식 카테고리 조인
                .where(isInCategoryList(categoryIdList)         // 카테고리 ID 리스트 내에 있는지
                        .and(hasNoChildren()))                   // 자식 카테고리가 없는지 확인
                .fetch();
    }


    /**
     * 회원이 좋아요한 책을 조회하는 메서드
     * @param pageable
     * @param userId
     * @return
     */
    @Override
    public Page<BookListResponse> getUserLikeBook(Pageable pageable, Long userId) {
        return null;
    }


    @Override
    public List<BookListResponse> getBooksForCheck(List<Long> bookIdList) {
        return List.of();
    }

    /**
     * 주문량이 많은 책 조회
     * @return
     */
    @Override
    public List<BookListResponse> getBestSeller() {
        return List.of();
    }

    /**
     * 조회수를 증가하는 메서드
     *
     * @param book
     * @param bookId
     */
    private synchronized void addViewCount(QBook book, Long bookId) {
        queryFactory.update(book)
                .set(book.viewCount, book.viewCount.add(1))
                .where(book.bookId.eq(bookId))
                .execute();
    }

    // 카테고리 ID 리스트 필터
    private BooleanExpression isInCategoryList(List<Long> categoryIdList) {
        return category.categoryId.in(categoryIdList);
    }

    // 자식 카테고리가 없는 조건
    private BooleanExpression hasNoChildren() {
        return category.children.isEmpty();
    }

    private BooleanExpression getLikeExpression(Long userId) {
        if (userId == null) {
            // 로그인하지 않은 사용자
            return Expressions.asBoolean(false);
        }
        // 로그인한 사용자의 좋아요 여부 확인
        return bookLike.user.userId.eq(userId);
    }

}
