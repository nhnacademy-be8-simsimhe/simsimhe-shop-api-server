package com.simsimbookstore.apiserver.orders.order.detail.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import com.simsimbookstore.apiserver.orders.packages.repository.PackagesRepository;
import com.simsimbookstore.apiserver.orders.packages.repository.WrapTypeRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import({OrderBookQueryRepository.class, QuerydslConfig.class})
@DataJpaTest
@ActiveProfiles("test2")
class OrderBookQueryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderBookQueryRepository orderBookQueryRepository;

    @Autowired
    private WrapTypeRepository wrapTypeRepository;

    @Autowired
    private PackagesRepository packagesRepository;

    @Autowired
    private OrderBookRepository orderBookRepository;

    @Autowired
    private BookRepository bookRepository;
    @Test
    void testFindByPackage() {

        Book book = Book.builder()
                .title("Sample Book Title")
                .description("This is a detailed description of the sample book.")
                .bookIndex("Index of the book.")
                .publisher("Sample Publisher")
                .isbn("1234567890123")
                .quantity(100)
                .price(BigDecimal.valueOf(50000))
                .saleprice(BigDecimal.valueOf(45000))
                .publicationDate(LocalDate.of(2025, 1, 1))
                .giftPackaging(true)
                .pages(300)
                .bookStatus(BookStatus.ONSALE)
                .viewCount(0L)
                .build();

        bookRepository.save(book);

        WrapType wrapType = WrapType.builder()
                .packageName("Gift Wrap")
                .packagePrice(BigDecimal.valueOf(10))
                .isAvailable(true)
                .build();
        wrapTypeRepository.save(wrapType);

        OrderBook orderBook = OrderBook.builder()
                .book(book)
                .salePrice(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.ZERO)
                .orderBookState(OrderBook.OrderBookState.PENDING)
                .quantity(1)
                .build();
        orderBookRepository.save(orderBook);

        Packages pkg = Packages.builder()
                .packageType("Gift")
                .orderBook(orderBook)
                .wrapType(wrapType)
                .build();
        packagesRepository.save(pkg);

        orderBook.getPackages().add(pkg);

        entityManager.flush();
        entityManager.clear();

        List<WrapType> result = orderBookQueryRepository.findByPackage(orderBook);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        WrapType resultWrapType = result.getFirst();
        assertEquals("Gift Wrap", resultWrapType.getPackageName());
        assertEquals(0, BigDecimal.valueOf(10.00).compareTo(resultWrapType.getPackagePrice()));
    }
}