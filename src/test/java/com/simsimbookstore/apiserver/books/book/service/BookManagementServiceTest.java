package com.simsimbookstore.apiserver.books.book.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.simsimbookstore.apiserver.books.book.dto.BookGiftResponse;
import com.simsimbookstore.apiserver.books.book.dto.BookRequestDto;
import com.simsimbookstore.apiserver.books.book.dto.BookResponseDto;
import com.simsimbookstore.apiserver.books.book.dto.BookStatusResponseDto;
import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookManagementServiceTest {

    @InjectMocks
    private BookManagementService bookManagementService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @Mock
    private BookTagRepository bookTagRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ContributorRepositroy contributorRepositroy;

    @Mock
    private BookContributorRepository bookContributorRepository;


    @Test
    void testRegisterBook() {
        // Arrange
        BookRequestDto requestDto = BookRequestDto.builder()
                .title("Test Book")
                .description("Test Description")
                .bookIndex("Index")
                .publisher("Test Publisher")
                .isbn("1234567890123")
                .quantity(10)
                .price(BigDecimal.valueOf(100))
                .saleprice(BigDecimal.valueOf(80))
                .publicationDate(LocalDate.now())
                .pages(200)
                .bookStatus(BookStatus.ONSALE)
                .categoryIdList(List.of(1L))
                .tagIdList(List.of(2L))
                .contributoridList(List.of(3L))
                .build();

        Book mockBook = mock(Book.class);
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);
        //when(bookRepository.getLowestCategoryId(anyList())).thenReturn(List.of(1L));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mock(Category.class)));
        when(tagRepository.findById(2L)).thenReturn(Optional.of(mock(Tag.class)));
        when(contributorRepositroy.findById(3L)).thenReturn(Optional.of(mock(Contributor.class)));

        // Act
        BookResponseDto responseDto = bookManagementService.registerBook(requestDto);

        // Assert
        assertNotNull(responseDto);
        verify(bookRepository).save(any(Book.class));
        verify(bookCategoryRepository).save(any(BookCategory.class));
        verify(bookTagRepository).save(any(BookTag.class));
        verify(bookContributorRepository).save(any(BookContributor.class));
    }

    @Test
    void testRegisterBookCategoryNotFound() {
        // Arrange
        BookRequestDto requestDto = BookRequestDto.builder()
                .title("Test Book")
                .description("Test Description")
                .categoryIdList(List.of(1L)) // 존재하지 않는 카테고리 ID
                .build();

        //when(bookRepository.getLowestCategoryId(anyList())).thenReturn(List.of(1L));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty()); // 카테고리 없음

        // Act & Assert
        assertThrows(NotFoundException.class, () -> bookManagementService.registerBook(requestDto));

        verify(bookRepository, times(1)).save(any(Book.class));
        verify(categoryRepository, times(1)).findById(1L);
    }


    @Test
    void testRegisterBookTagNotFound() {
        BookRequestDto requestDto = BookRequestDto.builder()
                .tagIdList(List.of(999L))
                .build();

        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookManagementService.registerBook(requestDto));

        verify(tagRepository).findById(999L);
    }

    @Test
    void testRegisterBookContributorNotFound() {
        BookRequestDto requestDto = BookRequestDto.builder()
                .contributoridList(List.of(999L))
                .build();

        when(contributorRepositroy.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookManagementService.registerBook(requestDto));

        verify(contributorRepositroy).findById(999L);
    }

    @Test
    void testUpdateBookWithoutConstructor() throws Exception {
        Long bookId = 1L;

        BookRequestDto requestDto = BookRequestDto.builder()
                .title("Updated Title")
                .description("Updated Description")
                .bookIndex("Updated Index")
                .publisher("Updated Publisher")
                .isbn("1234567890123")
                .quantity(20)
                .price(BigDecimal.valueOf(200))
                .saleprice(BigDecimal.valueOf(180))
                .publicationDate(LocalDate.now())
                .pages(300)
                .categoryIdList(List.of(2L))
                .tagIdList(List.of(3L))
                .contributoridList(List.of(4L))
                .build();

        // Book 생성
        Constructor<Book> bookConstructor = Book.class.getDeclaredConstructor();
        bookConstructor.setAccessible(true);
        Book existingBook = bookConstructor.newInstance();
        Field titleField = Book.class.getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(existingBook, "Old Title");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.getLowestCategoryId(anyList())).thenReturn(List.of(2L));

        // Category 생성
        Constructor<Category> categoryConstructor = Category.class.getDeclaredConstructor();
        categoryConstructor.setAccessible(true);
        Category category = categoryConstructor.newInstance();
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        // Contributor 생성
        Constructor<Contributor> contributorConstructor = Contributor.class.getDeclaredConstructor();
        contributorConstructor.setAccessible(true);
        Contributor contributor = contributorConstructor.newInstance();
        when(contributorRepositroy.findById(4L)).thenReturn(Optional.of(contributor));

        // Tag 설정
        Constructor<Tag> tagConstructor = Tag.class.getDeclaredConstructor();
        tagConstructor.setAccessible(true);
        Tag tag = tagConstructor.newInstance();
        when(tagRepository.findById(3L)).thenReturn(Optional.of(tag));

        BookResponseDto responseDto = bookManagementService.updateBook(bookId, requestDto);

        assertNotNull(responseDto);
        assertEquals("Updated Title", existingBook.getTitle());
        assertEquals("Updated Description", existingBook.getDescription());
        assertEquals("Updated Index", existingBook.getBookIndex());
        assertEquals("Updated Publisher", existingBook.getPublisher());
        assertEquals("1234567890123", existingBook.getIsbn());
        assertEquals(20, existingBook.getQuantity());
        assertEquals(BigDecimal.valueOf(200), existingBook.getPrice());
        assertEquals(BigDecimal.valueOf(180), existingBook.getSaleprice());
        assertEquals(LocalDate.now(), existingBook.getPublicationDate());
        assertEquals(300, existingBook.getPages());

        verify(bookRepository).findById(bookId);
        verify(bookCategoryRepository).deleteByBookId(bookId);
        verify(bookContributorRepository).deleteByBookId(bookId);
        verify(bookTagRepository).deleteByBookId(bookId);
        verify(bookCategoryRepository).save(any());
        verify(bookTagRepository).save(any());
        verify(bookContributorRepository).save(any());
    }

    @Test
    void testUpdateBookNotFound() {
        Long bookId = 1L;
        BookRequestDto requestDto = BookRequestDto.builder().build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookManagementService.updateBook(bookId, requestDto));

        verify(bookRepository).findById(bookId);
        verifyNoInteractions(bookCategoryRepository);
        verifyNoInteractions(bookTagRepository);
        verifyNoInteractions(bookContributorRepository);
    }

    @Test
    void testModifyQuantitySOLDOUT() {
        Long bookId = 1L;
        int quantity = -10;

        Book book = Book.builder()
                .quantity(10)
                .bookStatus(BookStatus.ONSALE)
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        int updatedQuantity = bookManagementService.modifyQuantity(bookId, quantity);

        assertEquals(0, updatedQuantity);
        assertEquals(BookStatus.SOLDOUT, book.getBookStatus());
        verify(bookRepository).save(book);
    }

    @Test
    void testModifyQuantityNotFound() {
        Long bookId = 1L;
        int quantity = 5;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookManagementService.modifyQuantity(bookId, quantity));

        verify(bookRepository).findById(bookId);
    }

    @Test
    void testModifyBookStatus() {
        Long bookId = 1L;

        BookRequestDto requestDto = BookRequestDto.builder()
                .bookStatus(BookStatus.SOLDOUT)
                .build();

        Book book = Book.builder()
                .bookStatus(BookStatus.ONSALE)
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookStatusResponseDto bookStatusResponseDto = bookManagementService.modifyBookStatus(bookId, requestDto);
        assertNotNull(bookStatusResponseDto);
        assertEquals(BookStatus.SOLDOUT, bookStatusResponseDto.getBookStatus());
        verify(bookRepository).findById(bookId);


    }

    @Test
    void testModifyBookStatusNotFound() {
        // Given
        Long bookId = 1L;
        BookRequestDto requestDto = BookRequestDto.builder()
                .bookStatus(BookStatus.SOLDOUT)
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Then
        assertThrows(NotFoundException.class, () -> bookManagementService.modifyBookStatus(bookId, requestDto));
        verify(bookRepository).findById(bookId);
    }

    @Test
    void testModifyBookGift() {
        // Given
        Long bookId = 1L;
        BookRequestDto requestDto = BookRequestDto.builder()
                .giftPackaging(true)
                .build();

        Book book = Book.builder()
                .giftPackaging(false)
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When
        BookGiftResponse bookGiftResponse = bookManagementService.modifyBookGift(bookId, requestDto);

        // Then
        assertNotNull(bookGiftResponse);
        assertTrue(bookGiftResponse.isGiftPackaging());
        verify(bookRepository).findById(bookId);
    }

    @Test
    void testModifyBookGiftNotFound() {
        // Given
        Long bookId = 1L;
        BookRequestDto requestDto = BookRequestDto.builder()
                .giftPackaging(true)
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Then
        assertThrows(NotFoundException.class, () -> bookManagementService.modifyBookGift(bookId, requestDto));
        verify(bookRepository).findById(bookId);
    }
}
