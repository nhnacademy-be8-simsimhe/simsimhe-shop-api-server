package com.simsimbookstore.apiserver.books.book.service;


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
import com.simsimbookstore.apiserver.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookManagementService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final BookTagRepository bookTagRepository;
    private final TagRepository tagRepository;
    private final ContributorRepositroy contributorRepositroy;
    private final BookContributorRepository bookContributorRepository;

    public BookManagementService(BookCategoryRepository bookCategoryRepository, BookRepository bookRepository, CategoryRepository categoryRepository
            , BookTagRepository bookTagRepository, TagRepository tagRepository, ContributorRepositroy contributorRepositroy
            , BookContributorRepository bookContributorRepository) {
        this.bookCategoryRepository = bookCategoryRepository;
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.bookTagRepository = bookTagRepository;
        this.tagRepository = tagRepository;
        this.contributorRepositroy = contributorRepositroy;
        this.bookContributorRepository = bookContributorRepository;
    }

    @Transactional
    public BookResponseDto registerBook(BookRequestDto bookRequestDto) {
        //요청dto를 엔티티로 변경
        Book book = BookMapper.toBook(bookRequestDto);

        Book saveBook = bookRepository.save(book);

        //도서 카테고리 연관관계 매핑
        List<Long> lowestCategoryId = bookRepository.getLowestCategoryId(bookRequestDto.getCategoryIdList());
        this.saveBookCategory(lowestCategoryId, book);

        //도서 태그 연관관계 매핑
        this.saveBookTag(book, bookRequestDto.getTagIdList());

        //도서기여자 연관관계 매핑
        this.saveBookContributor(book, bookRequestDto.getContributoridList());

        return BookMapper.toResponseDto(saveBook);
    }


    /**
     * 책 등록 시 카테고리 연관관계를 설정하는 메서드 입니다.
     *
     * @param categoryIdList 카테고리 이름 목록
     * @param book           책 객체
     */
    @Transactional
    public void saveBookCategory(List<Long> categoryIdList, Book book) {
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

        // 태그가 없으면 설정하지 않습니다.
        if (!tagIdList.isEmpty()) {
            for (Long tagId : tagIdList) {
                Tag findTag =
                        tagRepository.findById(tagId).orElseThrow(() -> new NotFoundException("태그 정보가 없습니다."));
                BookTag bookTag = BookTag.builder()
                        .book(book)
                        .tag(findTag)
                        .build();
                bookTagRepository.save(bookTag);
            }
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
        for (int i = 0; i < contributorIdList.size(); i++) {

            Long contributorId = contributorIdList.get(i);

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
     * 도서의 상테를 변경하는 메서드
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
            throw new NotFoundException("책 정보가없습니다");
        }
    }


    /**
     * 도서의 수량을 추가하는 메서드
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
                } else if (resultQuantity == 0) {
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
