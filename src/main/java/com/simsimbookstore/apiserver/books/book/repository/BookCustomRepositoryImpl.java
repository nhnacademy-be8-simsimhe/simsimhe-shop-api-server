package com.simsimbookstore.apiserver.books.book.repository;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simsimbookstore.apiserver.books.book.dto.BookListResponse;
import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
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
import com.simsimbookstore.apiserver.books.contributor.entity.QContributor;
import com.simsimbookstore.apiserver.books.tag.domain.QTag;
import com.simsimbookstore.apiserver.books.tag.dto.TagResponseDto;
import com.simsimbookstore.apiserver.like.entity.QBookLike;
import com.simsimbookstore.apiserver.orders.orderbook.entity.QOrderBook;
import com.simsimbookstore.apiserver.reviews.review.entity.QReview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.simsimbookstore.apiserver.books.book.repository.BookConst.*;

@RequiredArgsConstructor
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
    QReview review = QReview.review;


    /**
     * 가장 최근에 출판된 책 8권을 조회하는 메서드(국내도서)
     * 출판일 기준으로 orderBy
     *
     * @return
     */
    @Override
    public List<BookListResponse> getNewBookList() {
        return queryFactory
                .select(Projections.fields(BookListResponse.class,
                        book.bookId.as(BOOK_ID),                  // Book 엔티티의 ID
                        bookImagePath.imagePath.as(IMAGE_PATH),
                        book.title.as(TITLE),                // 책 제목
                        book.publicationDate.as(PUBLICATION_DATE), // 출판일
                        book.price.as(PRICE),                // 가격
                        book.saleprice.as(SALE_PRICE),        // 세일 가격
                        book.publisher.as(PUBLISHER),   // 출판사 이름
                        book.bookStatus.as(BOOK_STATUS),      // 책 상태
                        book.quantity.as(QUANTITY)            // 재고 수량
                ))
                .from(book)
                .orderBy(book.publicationDate.desc()) // 출판일 기준 최신순 정렬
                .innerJoin(bookImagePath).on(book.bookId.eq(bookImagePath.book.bookId))
                .innerJoin(bookTag).on(book.bookId.eq(bookTag.book.bookId))
                .where(bookImagePath.imageType.eq(BookImagePath.ImageType.THUMBNAIL).and(bookTag.tag.tagId.eq(1L)).and(book.bookStatus.ne(BookStatus.DELETED)))
                .limit(8)                           // 상위 8권만 조회
                .fetch();                           // 결과 가져오기
    }

    /**
     * 모든 책 조회하는 메서드(관리자만 볼수 있음)
     *
     * @param pageable
     * @return
     */
    @Override
    public Page<BookListResponse> getAllBook(Pageable pageable) {

        List<BookListResponse> content = queryFactory
                .select(Projections.fields(BookListResponse.class,
                        book.bookId.as(BOOK_ID),
                        bookImagePath.imagePath.as(IMAGE_PATH),
                        book.title.as(TITLE),
                        book.publicationDate.as(PUBLICATION_DATE),
                        book.price.as(PRICE),
                        book.saleprice.as(SALE_PRICE),
                        book.publisher.as(PUBLISHER),
                        book.bookStatus.as(BOOK_STATUS),
                        book.giftPackaging,
                        book.quantity.as(QUANTITY)

                ))
                .from(book)
                .where(book.bookStatus.ne(BookStatus.DELETED)) //삭제된 도서는 안보이게
                .offset(pageable.getOffset()) // 페이지 시작점
                .limit(pageable.getPageSize()) // 페이지 크기
                .innerJoin(bookImagePath).on(book.bookId.eq(bookImagePath.book.bookId)
                        .and(bookImagePath.imageType.eq(BookImagePath.ImageType.THUMBNAIL)))
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

        // 모든 하위 카테고리 ID를 가져오는 재귀 메서드
        List<Long> categoryIds = fetchAllCategoryIds(categoryId);

        if (categoryIds.isEmpty()) {
            return Page.empty(pageable); // 카테고리가 없으면 빈 결과 반환
        }

        // 정렬 조건 생성
        List<OrderSpecifier<?>> orderSpecifiers = createOrderSpecifiers(pageable);

        // 책 리스트 조회
        List<BookListResponse> content = queryFactory
                .select(Projections.fields(BookListResponse.class,
                        book.bookId.as(BOOK_ID),
                        bookImagePath.imagePath.as(IMAGE_PATH),
                        book.title.as(TITLE),
                        book.publicationDate.as(PUBLICATION_DATE),
                        book.price.as(PRICE),
                        book.saleprice.as(SALE_PRICE),
                        book.publisher.as(PUBLISHER),
                        book.bookStatus.as(BOOK_STATUS),
                        book.quantity.as(QUANTITY),
                        review.count().coalesce(0L).as(REVIEW_COUNT), // 리뷰 개수 추가
                        isLiked.as(IS_LIKED)
                ))
                .from(book)
                .innerJoin(bookCategory).on(book.bookId.eq(bookCategory.book.bookId))
                .innerJoin(bookImagePath).on(book.bookId.eq(bookImagePath.book.bookId)
                        .and(bookImagePath.imageType.eq(BookImagePath.ImageType.THUMBNAIL)))
                .leftJoin(review).on(book.bookId.eq(review.book.bookId)) // 리뷰 조인
                .leftJoin(bookLike).on(book.bookId.eq(bookLike.book.bookId))
                .where(bookCategory.catagory.categoryId.in(categoryIds)
                        .and(book.bookStatus.ne(BookStatus.DELETED))) // 카테고리 조건
                .groupBy( // GROUP BY 추가
                        book.bookId,
                        book.title,
                        book.publicationDate,
                        book.price,
                        book.saleprice,
                        book.publisher,
                        book.bookStatus,
                        book.quantity,
                        bookImagePath.imagePath,
                        bookLike.user.userId
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier<?>[0]))
                .fetch();

        // 전체 데이터 수 조회
        Long total = queryFactory
                .select(book.count())
                .from(book)
                .leftJoin(bookCategory).on(book.bookId.eq(bookCategory.book.bookId))
                .where(bookCategory.catagory.categoryId.in(categoryIds))
                .fetchOne();

        if (total == null) {
            total = 0L;
        }

        // Page 객체로 반환
        return new PageImpl<>(content, pageable, total);
    }

    // 하위 카테고리를 재귀적으로 탐색하여 모든 ID를 가져오는 메서드
    private List<Long> fetchAllCategoryIds(Long parentCategoryId) {
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(parentCategoryId);

        List<Long> childCategoryIds = queryFactory
                .select(category.categoryId)
                .from(category)
                .where(category.parent.categoryId.eq(parentCategoryId))
                .fetch();

        for (Long childId : childCategoryIds) {
            categoryIds.addAll(fetchAllCategoryIds(childId)); // 재귀 호출
        }

        return categoryIds;
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
        // 정렬 조건 생성
        List<OrderSpecifier<?>> orderSpecifiers = createOrderSpecifiers(pageable);
        // 데이터 조회
        List<BookListResponse> content = queryFactory
                .selectDistinct(Projections.fields(BookListResponse.class,
                        book.bookId.as(BOOK_ID),
                        book.title.as(TITLE),
                        book.publicationDate.as(PUBLICATION_DATE),
                        book.price.as(PRICE),
                        book.saleprice.as(SALE_PRICE),
                        book.publisher.as(PUBLISHER),
                        book.bookStatus.as(BOOK_STATUS),
                        book.quantity.as(QUANTITY),
                        bookImagePath.imagePath.as(IMAGE_PATH),
                        review.count().coalesce(0L).as(REVIEW_COUNT), // 리뷰 개수 추가
                        isLiked.as(IS_LIKED)
                ))
                .from(book)
                .innerJoin(bookTag).on(book.bookId.eq(bookTag.book.bookId))
                .leftJoin(bookLike).on(book.bookId.eq(bookLike.book.bookId))
                .leftJoin(review).on(book.bookId.eq(review.book.bookId)) // 리뷰 조인
                .innerJoin(bookImagePath).on(book.bookId.eq(bookImagePath.book.bookId))
                .where(bookTag.tag.tagId.eq(tagId)
                        .and(bookImagePath.imageType.eq(BookImagePath.ImageType.THUMBNAIL))
                        .and(book.bookStatus.ne(BookStatus.DELETED)))
                .groupBy( // GROUP BY 추가
                        book.bookId,
                        book.title,
                        book.publicationDate,
                        book.price,
                        book.saleprice,
                        book.publisher,
                        book.bookStatus,
                        book.quantity,
                        bookImagePath.imagePath,
                        bookLike.user.userId
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier<?>[0]))
                .fetch();

        // 전체 데이터 수 조회
        Long total = queryFactory
                .select(book.count())
                .from(book)
                .innerJoin(bookTag).on(book.bookId.eq(bookTag.book.bookId))
                .where(bookTag.tag.tagId.eq(tagId))
                .fetchOne();

        // Page 객체로 반환
        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 정렬 조건 생성 메서드
     */
    private List<OrderSpecifier<?>> createOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                OrderSpecifier<?> orderSpecifier;
                switch (order.getProperty()) {
                    case "publicationDate": // 최신순 정렬
                        orderSpecifier = order.isAscending()
                                ? new OrderSpecifier<>(Order.ASC, book.publicationDate)
                                : new OrderSpecifier<>(Order.DESC, book.publicationDate);
                        break;
                    case "price": // 가격 정렬
                        orderSpecifier = order.isAscending()
                                ? new OrderSpecifier<>(Order.ASC, book.price)
                                : new OrderSpecifier<>(Order.DESC, book.price);
                        break;
                    case "reviewCount": // 리뷰 많은 순 정렬
                        NumberExpression<Long> reviewCount = review.count().coalesce(0L); // 리뷰 개수 계산
                        orderSpecifier = order.isAscending()
                                ? new OrderSpecifier<>(Order.ASC, reviewCount)
                                : new OrderSpecifier<>(Order.DESC, reviewCount);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid sort property: " + order.getProperty());
                }
                orderSpecifiers.add(orderSpecifier);
            });
        }

        // 기본 정렬 조건 추가 (ID 오름차순)
        orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, book.bookId));

        return orderSpecifiers;
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

        // 리뷰가 없어서 평점이 null이면 0으로하고 아니면 평점 계산
        NumberExpression<Double> score = new CaseBuilder().when(review.score.avg().isNull()).then(0D).otherwise(review.score.avg());

        // 책 상세 정보를 조회
        BookResponseDto bookResponse = queryFactory
                .select(Projections.fields(BookResponseDto.class,
                        book.bookId.as(BOOK_ID),
                        book.title.as(TITLE),
                        book.description.as(DESCRIPTION),
                        book.bookIndex.as(BOOK_INDEX),
                        book.publisher.as(PUBLISHER),
                        book.isbn.as(ISBN),
                        book.viewCount.as(VIEW_COUNT),
                        book.price.as(PRICE),
                        book.saleprice.as(SALE_PRICE),
                        book.publicationDate.as(PUBLICATION_DATE),
                        book.pages.as(PAGES),
                        book.quantity.as(QUANTITY),
                        isLiked.as(IS_LIKED),
                        book.bookStatus.as(BOOK_STATUS),
                        book.giftPackaging,
                        review.count().as(REVIEW_COUNT),
                        score.as(SCORE_AVERAGE),
                        // 이미지 경로들 가져오기
                        Expressions.stringTemplate(
                                "GROUP_CONCAT(CASE WHEN {0} = 'THUMBNAIL' THEN {1} END)",
                                bookImagePath.imageType, bookImagePath.imagePath
                        ).as(THUMBNAIL_IMAGE),
                        Expressions.stringTemplate(
                                "GROUP_CONCAT(CASE WHEN {0} = 'DETAIL' THEN {1} END)",
                                bookImagePath.imageType, bookImagePath.imagePath
                        ).as(DETAIL_IMAGE)
                ))
                .from(book)
                .leftJoin(bookLike).on(book.bookId.eq(bookLike.book.bookId))
                .leftJoin(review).on(book.bookId.eq(review.book.bookId))
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
                        book.bookId.as(BOOK_ID),           // 책 ID
                        book.title.as(TITLE),             // 책 제목
                        book.bookStatus.as(BOOK_STATUS),   // 책 상태
                        book.quantity.as(QUANTITY),       // 책 재고
                        bookImagePath.imagePath.as(IMAGE_PATH),
                        isLiked.as(IS_LIKED),
                        book.publisher,
                        book.price,
                        book.saleprice
                ))
                .from(bookLike)
                .join(bookLike.book, book) // bookLike와 book 조인
                .innerJoin(bookImagePath).on(book.bookId.eq(bookImagePath.book.bookId)
                        .and(bookImagePath.imageType.eq(BookImagePath.ImageType.THUMBNAIL)))
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
                        book.bookId.as(BOOK_ID),
                        book.title.as(TITLE),
                        bookImagePath.imagePath.min().as(IMAGE_PATH), // 이미지 경로를 하나만 가져오도록 수정
                        book.publicationDate.as(PUBLICATION_DATE),
                        book.quantity.as(QUANTITY),
                        book.price.as(PRICE),
                        book.saleprice.as(SALE_PRICE),
                        book.bookStatus.as(BOOK_STATUS),
                        book.publisher.as(PUBLISHER)))
                .from(orderBook)
                .where(book.bookStatus.ne(BookStatus.DELETED))
                .innerJoin(orderBook.book, book)
                .innerJoin(bookImagePath).on(book.bookId.eq(bookImagePath.book.bookId)
                        .and(bookImagePath.imageType.eq(BookImagePath.ImageType.THUMBNAIL)))
                .groupBy(
                        book.bookId,
                        book.title,
                        book.publicationDate,
                        book.quantity,
                        book.price,
                        book.saleprice,
                        book.bookStatus,
                        book.publisher
                )
                .orderBy(orderBook.quantity.sum().desc())
                .limit(6)
                .fetch();
    }


    /**
     * 특정 도서를 제외한 동일 카테고리 내 인기 도서 추천 기능 5개
     *
     * @param categoryIdList
     * @param bookId
     * @return
     */
    @Override
    public List<BookListResponse> getRecommendBook(List<Long> categoryIdList, Long bookId) {
        return queryFactory.select(Projections.fields(BookListResponse.class
                        , book.bookId
                        , bookImagePath.imagePath.as(IMAGE_PATH)
                        , book.title
                        , book.quantity
                        , book.saleprice
                        , book.price
                        , book.publisher
                        , book.bookStatus
                        , book.viewCount))
                .from(book)
                .innerJoin(bookCategory).on(book.bookId.eq(bookCategory.book.bookId))
                .innerJoin(bookImagePath).on(book.bookId.eq(bookImagePath.book.bookId)
                        .and(bookImagePath.imageType.eq(BookImagePath.ImageType.THUMBNAIL)))
                .where(bookCategory.book.bookId.ne(bookId).and(bookCategory.catagory.categoryId.in(categoryIdList)).and(book.bookStatus.ne(BookStatus.DELETED)))
                .distinct()
                .orderBy(book.viewCount.desc())
                .limit(5)
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
