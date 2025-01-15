package com.simsimbookstore.apiserver.books.book.service;


import com.simsimbookstore.apiserver.books.book.dto.BookGiftResponse;
import com.simsimbookstore.apiserver.books.book.dto.BookRequestDto;
import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.dto.BookStatusResponseDto;
import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.book.exception.BookOutOfStockException;
import com.simsimbookstore.apiserver.books.book.mapper.BookMapper;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.books.bookcategory.entity.BookCategory;
import com.simsimbookstore.apiserver.books.bookcategory.repository.BookCategoryRepository;
import com.simsimbookstore.apiserver.books.bookcontributor.entity.BookContributor;
import com.simsimbookstore.apiserver.books.bookcontributor.repository.BookContributorRepository;
import com.simsimbookstore.apiserver.books.booktag.entity.BookTag;
import com.simsimbookstore.apiserver.books.booktag.repositry.BookTagRepository;
import com.simsimbookstore.apiserver.books.category.entity.Category;
import com.simsimbookstore.apiserver.books.category.repository.CategoryRepository;
import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
import com.simsimbookstore.apiserver.books.contributor.repository.ContributorRepositroy;
import com.simsimbookstore.apiserver.books.tag.domain.Tag;
import com.simsimbookstore.apiserver.books.tag.repository.TagRepository;
import com.simsimbookstore.apiserver.exception.BadRequestException;
import com.simsimbookstore.apiserver.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookManagementService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final BookTagRepository bookTagRepository;
    private final TagRepository tagRepository;
    private final ContributorRepositroy contributorRepositroy;
    private final BookContributorRepository bookContributorRepository;


    /**
     * 도서등록
     *
     * @param bookRequestDto
     * @return
     */
    @Transactional
    public BookResponseDto registerBook(BookRequestDto bookRequestDto) {
        //요청dto를 엔티티로 변경
        Book book = BookMapper.toBook(bookRequestDto);

        // book이 null인지 확인
        if (book == null) {
            throw new BadRequestException("Book 객체 생성에 실패했습니다.");
        }

        Book saveBook = bookRepository.save(book);

        //도서 카테고리 연관관계 매핑(가장 하위 카테고리만 가져옴)
        //List<Long> lowestCategoryId = bookRepository.getLowestCategoryId(bookRequestDto.getCategoryIdList());
        this.saveBookCategory(bookRequestDto.getCategoryIdList(), book);

        //도서 태그 연관관계 매핑
        this.saveBookTag(book, bookRequestDto.getTagIdList());

        //도서기여자 연관관계 매핑
        this.saveBookContributor(book, bookRequestDto.getContributoridList());


        return BookMapper.toResponseDto(saveBook);
    }


    /**
     * 도서수정(도서의 상태는 변경x)
     *
     * @param bookId
     * @param requestDto
     * @return
     */
    @Transactional
    public BookResponseDto updateBook(Long bookId, BookRequestDto requestDto) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();

            Optional.ofNullable(requestDto.getTitle()).ifPresent(book::setTitle);
            Optional.ofNullable(requestDto.getDescription()).ifPresent(book::setDescription);
            Optional.ofNullable(requestDto.getBookIndex()).ifPresent(book::setBookIndex);
            Optional.ofNullable(requestDto.getPublisher()).ifPresent(book::setPublisher);
            Optional.ofNullable(requestDto.getIsbn()).ifPresent(book::setIsbn);
            Optional.of(requestDto.getQuantity()).ifPresent(book::setQuantity);
            Optional.of(requestDto.getPrice()).ifPresent(book::setPrice);
            Optional.of(requestDto.getSaleprice()).ifPresent(book::setSaleprice);
            Optional.ofNullable(requestDto.getPublicationDate()).ifPresent(book::setPublicationDate);
            Optional.of(requestDto.getPages()).ifPresent(book::setPages);

            if (!requestDto.getContributoridList().isEmpty()) {
                Optional.of(requestDto.getContributoridList()).ifPresent(contributoridList -> {
                    bookContributorRepository.deleteByBookId(bookId);
                    this.saveBookContributor(book, contributoridList);
                });
            }

            if (!requestDto.getCategoryIdList().isEmpty()) {
                Optional.of(requestDto.getCategoryIdList()).ifPresent(categoryIdList -> {
                    bookCategoryRepository.deleteByBookId(bookId);
                    //List<Long> lowestCategoryId = bookRepository.getLowestCategoryId(categoryIdList);
                    this.saveBookCategory(categoryIdList, book);
                });
            }

            if (!requestDto.getTagIdList().isEmpty()) {
                Optional.of(requestDto.getTagIdList()).ifPresent(tagIdList -> {
                    bookTagRepository.deleteByBookId(bookId);
                    this.saveBookTag(book, tagIdList);
                });
            }

            return BookMapper.toResponseDto(book);


        } else {
            throw new NotFoundException("도서 정보가 없습니다");
        }
    }

    /**
     * 책 등록 시 카테고리 연관관계를 설정하는 메서드 입니다.
     *
     * @param categoryIdList 카테고리 이름 목록
     * @param book           책 객체
     */
    @Transactional
    public void saveBookCategory(List<Long> categoryIdList, Book book) {

        if (book == null) {
            throw new BadRequestException("Book 객체가 null입니다.");
        }
        if (categoryIdList == null || categoryIdList.isEmpty()) {
            return;
        }
        for (Long categoryId : categoryIdList) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("카테고리가 존재하지 않습니다."));

            BookCategory bookCategory = BookCategory.builder()
                    .book(book)
                    .catagory(category)
                    .build();
            bookCategoryRepository.save(bookCategory);
        }
    }

    /**
     * 책 등록 시 태그에 대한 설정을 하는 메서드 입니다.
     *
     * @param book      책 객체
     * @param tagIdList 태그 이름 목록
     */
    @Transactional
    public void saveBookTag(Book book, List<Long> tagIdList) {
        // tagIdList가 null이거나 비어 있으면 작업하지 않습니다.
        if (tagIdList == null || tagIdList.isEmpty()) {
            return;
        }

        for (Long tagId : tagIdList) {
            Tag tag = tagRepository.findById(tagId).orElseThrow(
                    () -> new NotFoundException("태그 정보가 없습니다."));
            BookTag bookTag = BookTag.builder()
                    .book(book)
                    .tag(tag)
                    .build();
            bookTagRepository.save(bookTag);
        }
    }


    /**
     * 책 등록 시 기여자와 역할에 대한 설정을 하는 메서드 입니다.
     *
     * @param book              책 객체
     * @param contributorIdList 기여자 아이디 리스트
     */
    @Transactional
    public void saveBookContributor(Book book, List<Long> contributorIdList) {

        // contributorIdList가 null이거나 비어 있으면 작업하지 않습니다.
        if (contributorIdList == null || contributorIdList.isEmpty()) {
            return;
        }
        for (Long contributorId : contributorIdList) {

            Contributor contributor = contributorRepositroy.findById(contributorId)
                    .orElseThrow(() -> new NotFoundException("기여자 정보가 없습니다."));


            BookContributor bookContributor = BookContributor.builder()
                    .book(book)
                    .contributor(contributor)
                    .build();
            bookContributorRepository.save(bookContributor);
        }
    }


    /**
     * 도서의 상태만  변경하는 메서드
     *
     * @param bookId
     * @param bookRequestDto
     * @return
     */
    @Transactional
    public BookStatusResponseDto modifyBookStatus(Long bookId, BookRequestDto bookRequestDto) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isPresent()) {

            Book book = optionalBook.get();
            book.setBookStatus(bookRequestDto.getBookStatus());

            return BookStatusResponseDto.builder()
                    .bookStatus(book.getBookStatus())
                    .build();
        } else {
            throw new NotFoundException("도서 정보가없습니다");
        }
    }

    @Transactional
    public BookGiftResponse modifyBookGift(Long bookId, BookRequestDto bookRequestDto) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setGiftPackaging(bookRequestDto.isGiftPackaging());

            return BookGiftResponse.builder()
                    .giftPackaging(book.isGiftPackaging())
                    .build();
        } else {
            throw new NotFoundException("도서 정보가 없습니다");
        }
    }


    /**
     * 도서의 수량을 변경
     *
     * @param bookId
     * @param quantity
     * @return
     */
    @Transactional
    public int modifyQuantity(Long bookId, int quantity) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();

            synchronized (this) {
                int resultQuantity = book.getQuantity() + quantity;

                if (resultQuantity < 0) {
                    throw new BookOutOfStockException("도서의 수량은 음수가 될 수 없습니다");
                } else if (resultQuantity == 0) { //재고가 0 이면 매진으로 도서 상태 변경
                    book.setBookStatus(BookStatus.SOLDOUT);
                }

                book.setQuantity(resultQuantity);
                bookRepository.save(book);
            }

            return book.getQuantity();
        } else {
            throw new NotFoundException("책 정보가 없습니다");
        }
    }


}
