package com.simsimbookstore.apiserver.books.book.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simsimbookstore.apiserver.books.book.dto.BookListResponse;
import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.entity.QBook;
import com.simsimbookstore.apiserver.books.bookcategory.entity.QBookCategory;
import com.simsimbookstore.apiserver.books.bookcontributor.dto.BookContributorResponsDto;
import com.simsimbookstore.apiserver.books.bookcontributor.entity.QBookContributor;
import com.simsimbookstore.apiserver.books.bookimage.entity.BookImagePath;
import com.simsimbookstore.apiserver.books.bookimage.entity.QBookImagePath;
import com.simsimbookstore.apiserver.books.booktag.entity.QBookTag;
import com.simsimbookstore.apiserver.books.category.dto.CategoryResponseDto;
import com.simsimbookstore.apiserver.books.category.entity.Category;
import com.simsimbookstore.apiserver.books.category.entity.QCategory;
import com.simsimbookstore.apiserver.books.contributor.dto.ContributorResponseDto;
import com.simsimbookstore.apiserver.books.contributor.entity.QContributor;
import com.simsimbookstore.apiserver.books.tag.domain.QTag;
import com.simsimbookstore.apiserver.books.tag.dto.TagResponseDto;
import com.simsimbookstore.apiserver.like.entity.QBookLike;
import com.simsimbookstore.apiserver.orders.orderbook.entity.QOrderBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class BookCustomRepositoryImpl implements BookCustomRepository {

    private final JPAQueryFactory queryFactory;

    QBook book = QBook.book;
    QBookContributor bookContributor = QBookContributor.bookContributor;
    QCategory category = QCategory.category;
    QBookCategory bookCategory = QBookCategory.bookCategory;
    QContributor contributor = QContributor.contributor;
    QTag tag = QTag.tag;
    QBookTag bookTag = QBookTag.bookTag;
    QBookLike bookLike = QBookLike.bookLike;
    QOrderBook orderBook = QOrderBook.orderBook;
    QBookImagePath bookImagePath = QBookImagePath.bookImagePath;

    public BookCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    /**
     * 가장 최근에 출판된 책 6권을 조회하는 메서드
     *
     * @return
     */
    @Override
    public List<BookListResponse> getNewBookList() {
        return queryFactory
                .select(Projections.fields(BookListResponse.class,
                        book.bookId.as("bookId"),                  // Book 엔티티의 ID
                        bookImagePath.imagePath.as("imagePath"),
                        book.title.as("title"),                // 책 제목
                        book.publicationDate.as("publicationDate"), // 출판일
                        book.price.as("price"),                // 가격
                        book.saleprice.as("saleprice"),        // 세일 가격
                        book.publisher.as("publisher"),   // 출판사 이름
                        book.bookStatus.as("bookStatus"),      // 책 상태
                        book.quantity.as("quantity")            // 재고 수량
                ))
                .from(book)
                .orderBy(book.publicationDate.desc()) // 출판일 기준 최신순 정렬
                .innerJoin(bookImagePath).on(book.bookId.eq(bookImagePath.book.bookId))
                .where(bookImagePath.imageType.eq(BookImagePath.ImageType.THUMBNAIL))
                .limit(6)                           // 상위 6권만 조회
                .fetch();                           // 결과 가져오기
    }

    /**
     * 모든 책 조회하는 메서드
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
//                .orderBy(book.publicationDate.desc()) // 출판일 기준 최신순 정렬
                .orderBy(book.bookId.asc())
                .fetch();

        // 전체 데이터 수 조회 (페이징 처리에 필요)
        Long total = queryFactory
                .select(book.count())
                .from(book)
                .fetchOne();

        List<BookListResponse> bookListResponses = this.toListResponseList(content);


        // Page 객체로 반환
        return new PageImpl<>(bookListResponses, pageable, total);
    }


    /**
     * 카테고리와 하위 카테고리에 해당하는 책들을 조회하는 메서드
     *
     * @param userId
     * @param categoryId
     * @param pageable
     * @return
     */
    @Override
    public Page<BookListResponse> getBookListByCategory(Long userId, Long categoryId, Pageable pageable) {
        // 좋아요 여부를 설정
        BooleanExpression isLiked = getLikeExpression(userId);


        // 특정 카테고리와 하위 카테고리 ID를 조회
        List<Long> categoryIds = queryFactory
                .select(category.categoryId)
                .from(category)
                .where(category.categoryId.eq(categoryId)
                        .or(category.parent.categoryId.eq(categoryId)))
                .fetch();

        // 책 리스트를 조회
        List<BookListResponse> content = queryFactory
                .select(Projections.fields(BookListResponse.class,
                        book.bookId.as("bookId"),
                        book.title.as("title"),
                        book.publicationDate.as("publicationDate"),
                        book.price.as("price"),
                        book.saleprice.as("saleprice"),
                        book.publisher.as("publisher"),
                        book.bookStatus.as("bookStatus"),
                        book.quantity.as("quantity"),
                        isLiked.as("isLiked")
                ))
                .from(book)
                .innerJoin(bookCategory).on(book.bookId.eq(bookCategory.book.bookId))
                .leftJoin(bookLike).on(book.bookId.eq(bookLike.book.bookId))
                .where(bookCategory.catagory.categoryId.in(categoryIds))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(book.publicationDate.desc())
                .fetch();

        // 전체 데이터 수 조회
        Long total = queryFactory
                .select(book.count())
                .from(book)
                .leftJoin(bookCategory).on(book.bookId.eq(bookCategory.book.bookId))
                .where(bookCategory.catagory.categoryId.in(categoryIds))
                .fetchOne();

        List<BookListResponse> bookListResponses = this.toListResponseList(content);

        // Page 객체로 반환
        //return PageableExecutionUtils.getPage(bookListResponses, pageable, () -> total);
        return new PageImpl<>(bookListResponses, pageable, total);
    }

    /**
     * 특징 태그에 해당하는 도서조회
     *
     * @param userId
     * @param tagId
     * @param pageable
     * @return
     */
    @Override
    public Page<BookListResponse> getBookListByTag(Long userId, Long tagId, Pageable pageable) {
        // 좋아요 여부를 설정
        BooleanExpression isLiked = getLikeExpression(userId);

        List<BookListResponse> content = queryFactory
                .select(Projections.fields(BookListResponse.class,
                        book.bookId.as("bookId"),
                        book.title.as("title"),
                        book.publicationDate.as("publicationDate"),
                        book.price.as("price"),
                        book.saleprice.as("saleprice"),
                        book.publisher.as("publisher"),
                        book.bookStatus.as("bookStatus"),
                        book.quantity.as("quantity"),
                        isLiked.as("isLiked")
                ))
                .from(book)
                .leftJoin(bookTag).on(book.bookId.eq(bookTag.book.bookId))
                .leftJoin(bookLike).on(book.bookId.eq(bookLike.book.bookId))
                .where(bookTag.tag.tagId.eq(tagId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(book.publicationDate.desc())
                .fetch();

        // 전체 데이터 수 조회
        Long total = queryFactory
                .select(book.count())
                .from(book)
                .innerJoin(bookTag).on(book.bookId.eq(bookTag.book.bookId))
                .where(bookTag.tag.tagId.eq(tagId))
                .fetchOne();

        List<BookListResponse> bookListResponses = this.toListResponseList(content);


        // Page 객체로 반환
        return new PageImpl<>(bookListResponses, pageable, total);

    }


    /**
     * 책 상세조회
     *
     * @param userId
     * @param bookId
     * @return
     */
//    @Override
//    public BookResponseDto getBookDetail(Long userId, Long bookId) {
//        // 좋아요 여부를 설정
//        BooleanExpression isLiked = getLikeExpression(userId);
//        // 책 상세 정보를 조회
//        BookResponseDto bookResponse = queryFactory
//                .select(Projections.fields(BookResponseDto.class,
//                        book.bookId.as("bookId"),
//                        book.title.as("title"),
//                        book.description.as("description"),
//                        book.bookIndex.as("bookIndex"),
//                        book.publisher.as("publisher"),
//                        book.isbn.as("isbn"),
//                        book.viewCount.as("viewCount"),
//                        book.price.as("price"),
//                        book.saleprice.as("saleprice"),
//                        book.publicationDate.as("publicationDate"),
//                        book.pages.as("pages"),
//                        book.quantity.as("quantity"),
//                        isLiked.as("isLiked"),
//                        book.bookStatus.as("bookStatus")
//                ))
//                .from(book)
//                .leftJoin(bookLike).on(book.bookId.eq(bookLike.book.bookId))
//                .innerJoin(bookImagePath).on(book.bookId.eq(bookImagePath.book.bookId))
//                .where(book.bookId.eq(bookId))
//                .groupBy(book, bookLike)
//                .fetchOne();
//
//
//        // 조회수 증가
//        this.addViewCount(book, bookId);
//
//        return this.toResponse(bookId, bookResponse);
//
//    }
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
                        book.bookStatus.as("bookStatus"),
                        book.giftPackaging,
                        // 이미지 경로들 가져오기
                        Expressions.stringTemplate(
                                "GROUP_CONCAT(CASE WHEN {0} = 'THUMBNAIL' THEN {1} END)",
                                bookImagePath.imageType, bookImagePath.imagePath
                        ).as("thumbnailImage"),
                        Expressions.stringTemplate(
                                "GROUP_CONCAT(CASE WHEN {0} = 'DETAIL' THEN {1} END)",
                                bookImagePath.imageType, bookImagePath.imagePath
                        ).as("detailImage")
                ))
                .from(book)
                .leftJoin(bookLike).on(book.bookId.eq(bookLike.book.bookId))
                .innerJoin(bookImagePath).on(book.bookId.eq(bookImagePath.book.bookId))
                .where(book.bookId.eq(bookId))
                .groupBy(book, bookLike)
                .fetchOne();

        // 조회수 증가
        this.addViewCount(book, bookId);

        return this.toResponse(bookId, bookResponse);
    }


    /**
     * 받은 카테고리 아이디에서 최하위 카테고리만 얻는 메서드
     *
     * @param categoryIdList
     * @return
     */
    @Override
    public List<Long> getLowestCategoryId(List<Long> categoryIdList) {
        if (categoryIdList == null || categoryIdList.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .select(category.categoryId)
                .from(category)
                .where(category.categoryId.in(categoryIdList)
                        .and(category.children.isEmpty())) // 자식 카테고리가 없는 조건
                .fetch();
    }


    /**
     * 회원이 좋아요한 책을 조회하는 메서드
     *
     * @param pageable
     * @param userId
     * @return
     */
    @Override
    public Page<BookListResponse> getUserLikeBook(Pageable pageable, Long userId) {

        //좋아요 설정
        BooleanExpression isLiked = this.getLikeExpression(userId);

        // 좋아요한 책 데이터 조회
        List<BookListResponse> bookList = queryFactory
                .select(Projections.fields(BookListResponse.class,
                        book.bookId.as("bookId"),           // 책 ID
                        book.title.as("title"),             // 책 제목
                        book.bookStatus.as("bookStatus"),   // 책 상태
                        book.quantity.as("quantity"),       // 책 재고
                        isLiked.as("isLiked")
                ))
                .from(bookLike)
                .join(bookLike.book, book) // bookLike와 book 조인
                .where(bookLike.user.userId.eq(userId)) // 특정 회원이 좋아요한 책만 필터링
                .offset(pageable.getOffset())          // 페이징 시작점
                .limit(pageable.getPageSize())         // 페이지 크기
                .fetch();

        // 전체 좋아요 데이터 수 조회
        Long totalCount = queryFactory
                .select(bookLike.count())
                .from(bookLike)
                .where(bookLike.user.userId.eq(userId)) // 특정 회원이 좋아요한 책만 카운트
                .fetchOne();

        // Page 객체로 반환
        return new PageImpl<>(bookList, pageable, totalCount);
    }


    /**
     * 책 수량을 조회하는 메서드
     *
     * @param bookIdList
     * @return
     */
    @Override
    public List<BookListResponse> getBooksForCheck(List<Long> bookIdList) {
        return queryFactory.select(Projections.fields(BookListResponse.class
                        , book.bookId
                        , book.title
                        , book.saleprice
                        , book.quantity))
                .from(book)
                .where(book.bookId.in(bookIdList))
                .fetch();
    }

    /**
     * 주문량이 많은 책 6개 조회
     *
     * @return
     */
    @Override
    public List<BookListResponse> getPopularityBook() {
        return queryFactory.select(Projections.fields(BookListResponse.class,
                        book.bookId.as("bookId"),
                        book.title.as("title"),
                        book.publicationDate.as("publicationDate"),
                        book.quantity.as("quantity"),
                        book.price.as("price"),
                        book.saleprice.as("saleprice"),
                        book.bookStatus.as("bookStatus"),
                        book.publisher.as("publisher")))
                .from(orderBook)
                .innerJoin(orderBook.book, book) // 명시적으로 조인
                .groupBy(
                        book.bookId,
                        book.title,
                        book.publicationDate,
                        book.quantity,
                        book.price,
                        book.saleprice,
                        book.bookStatus,
                        book.publisher
                ) // 필요한 필드를 명시적으로 그룹화
                .orderBy(orderBook.quantity.sum().desc())
                .limit(6)
                .fetch();
    }

    /**
     * 특정 도서를 제외한 동일 카테고리 내 인기 도서 추천 기능
     *
     * @param categoryIdList
     * @param bookId
     * @return
     */
    @Override
    public List<BookListResponse> getRecommendBook(List<Long> categoryIdList, Long bookId) {
        return queryFactory.select(Projections.fields(BookListResponse.class
                        , book.bookId
                        , book.title
                        , book.quantity
                        , book.bookStatus
                        , book.viewCount))
                .from(book)
                .innerJoin(bookCategory).on(book.bookId.eq(bookCategory.book.bookId))
                .where(bookCategory.book.bookId.ne(bookId).and(bookCategory.catagory.categoryId.in(categoryIdList)))
                .distinct()
                .orderBy(book.viewCount.desc())
                .limit(10)
                .fetch();
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


    // 사용자의 로그인 여부확인
    private BooleanExpression getLikeExpression(Long userId) {
        return userId == null
                ? Expressions.asBoolean(false) // 로그인하지 않은 사용자
                : bookLike.user.userId.eq(userId); // 로그인한 사용자의 좋아요 여부 확인
    }


    /**
     * 도서에 기여자와 역할 정보를 설정하고 반환하는 메서드
     *
     * @param responseList
     * @return
     */
    private List<BookListResponse> toListResponseList(List<BookListResponse> responseList) {

        if (responseList.isEmpty()) {
            return responseList;
        }

        for (BookListResponse bookListResponse : responseList) {
            List<BookContributorResponsDto> bookContributorResponsDtoList = queryFactory.select(Projections.fields(BookContributorResponsDto.class
                            , contributor.contributorId
                            , contributor.contributorName
                            , contributor.contributorRole))
                    .from(bookContributor)
                    .innerJoin(contributor).on(bookContributor.contributor.contributorId.eq(contributor.contributorId))
                    .where(bookContributor.book.bookId.eq(bookListResponse.getBookId()))
                    .fetch();

            bookListResponse.setContributorList(bookContributorResponsDtoList);

        }
        return responseList;
    }

    /**
     * 도서 상세정보에 대한 객체를 반환하는 메서드
     *
     * @param bookId
     * @param bookResponseDto
     * @return
     */
    private BookResponseDto toResponse(Long bookId, BookResponseDto bookResponseDto) {
        // 기여자 역할 정보 설정
        List<BookContributorResponsDto> bookContributorResponsDtoList = queryFactory
                .select(Projections.fields(
                        BookContributorResponsDto.class,
                        bookContributor.contributor.contributorId,
                        bookContributor.contributor.contributorName,
                        bookContributor.contributor.contributorRole

                ))
                .from(bookContributor)
                .innerJoin(contributor).on(bookContributor.contributor.contributorId.eq(contributor.contributorId))
                .where(bookContributor.book.bookId.eq(bookId))
                .fetch();

        // 책에 연관된 카테고리 ID를 가져옵니다.
        List<Long> categoryIdList = queryFactory
                .select(bookCategory.catagory.categoryId) // 카테고리 ID를 선택
                .from(bookCategory)                       // bookCategory 테이블 기준
                .join(bookCategory.catagory, category)    // bookCategory와 category를 조인
                .where(bookCategory.book.bookId.eq(bookId)) // 특정 책 ID에 해당하는 카테고리 필터
                .fetch();                                 // 결과 가져오기

        // 필요한 카테고리와 부모 정보를 미리 로드합니다.
        Map<Long, Category> categoryMap = queryFactory
                .selectFrom(category)
                .where(category.categoryId.in(categoryIdList))
                .fetch()
                .stream()
                .collect(Collectors.toMap(Category::getCategoryId, Function.identity()));

        List<List<CategoryResponseDto>> categoriesList = new ArrayList<>();

        // 각 카테고리에 대해 계층 구조를 생성합니다.
        for (Long categoryId : categoryIdList) {
            List<CategoryResponseDto> categories = new ArrayList<>();
            Category currentCategory = categoryMap.get(categoryId);

            while (currentCategory != null) {
                CategoryResponseDto dto = CategoryResponseDto.builder()
                        .categoryId(currentCategory.getCategoryId())
                        .categoryName(currentCategory.getCategoryName())
                        .parentId(currentCategory.getParent() != null ? currentCategory.getParent().getCategoryId() : null)
                        .parentName(currentCategory.getParent() != null ? currentCategory.getParent().getCategoryName() : null)
                        .build();
                categories.add(dto);
                currentCategory = currentCategory.getParent();
            }

            // 계층 순서를 뒤집어서 올바른 순서로 저장
            Collections.reverse(categories);
            categoriesList.add(categories);
        }

        List<TagResponseDto> bookTagList = queryFactory
                .select(Projections.fields(TagResponseDto.class,
                        tag.tagId,
                        tag.tagName))
                .from(bookTag)
                .innerJoin(tag).on(bookTag.tag.tagId.eq(tag.tagId))
                .where(bookTag.book.bookId.eq(bookId))
                .fetch();

        bookResponseDto.setContributorResponsDtos(bookContributorResponsDtoList);
        bookResponseDto.setCategoryList(categoriesList);
        bookResponseDto.setTagList(bookTagList);


        return bookResponseDto;
    }


}
