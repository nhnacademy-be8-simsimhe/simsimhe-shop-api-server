//package com.simsimbookstore.apiserver.books.book.aladin;
//
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//import com.simsimbookstore.apiserver.books.book.entity.Book;
//import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
//import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
//import com.simsimbookstore.apiserver.books.bookcategory.entity.BookCategory;
//import com.simsimbookstore.apiserver.books.bookcategory.repository.BookCategoryRepository;
//import com.simsimbookstore.apiserver.books.bookcontributor.entity.BookContributor;
//import com.simsimbookstore.apiserver.books.bookcontributor.repository.BookContributorRepository;
//import com.simsimbookstore.apiserver.books.booktag.entity.BookTag;
//import com.simsimbookstore.apiserver.books.booktag.repositry.BookTagRepository;
//import com.simsimbookstore.apiserver.books.category.entity.Category;
//import com.simsimbookstore.apiserver.books.category.repository.CategoryRepository;
//import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
//import com.simsimbookstore.apiserver.books.contributor.repository.ContributorRepositroy;
//import com.simsimbookstore.apiserver.books.tag.domain.Tag;
//import com.simsimbookstore.apiserver.books.tag.repository.TagRepository;
//import org.apache.commons.text.StringEscapeUtils;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//@Service
//public class AladinApiService {
//
//    private final RestTemplate restTemplate;
//    private final BookRepository bookRepository;
//    private final TagRepository tagRepository;
//    private final BookTagRepository bookTagRepository;
//    private final ContributorRepositroy contributorRepositroy;
//    private final BookContributorRepository bookContributorRepository;
//    private final CategoryRepository categoryRepository;
//    private final BookCategoryRepository bookCategoryRepository;
//
//    public AladinApiService(RestTemplate restTemplate, BookRepository bookRepository,
//                            TagRepository tagRepository, BookTagRepository bookTagRepository, ContributorRepositroy contributorRepositroy, BookContributorRepository bookContributorRepository, CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository) {
//        this.restTemplate = restTemplate;
//        this.bookRepository = bookRepository;
//        this.tagRepository = tagRepository;
//        this.bookTagRepository = bookTagRepository;
//        this.contributorRepositroy = contributorRepositroy;
//        this.bookContributorRepository = bookContributorRepository;
//        this.categoryRepository = categoryRepository;
//        this.bookCategoryRepository = bookCategoryRepository;
//    }
//
////    @Transactional
////    public void fetchAndSaveBooks(String query) throws Exception {
//////        String url = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx?ttbkey=ttbchlim201008001"
//////                + "&Query=" + query
//////                + "&QueryType=Name"
//////                + "&MaxResults=10"
//////                + "&start=1"
//////                + "&SearchTarget=Book"
//////                + "&output=xml"
//////                + "&Version=20131101";
////        String url = "http://www.aladin.co.kr/ttb/api/ItemList.aspx"
////        + "?ttbkey=ttbchlim201008001"
////        + "&QueryType=Bestseller"
////        + "&MaxResults=50"
////        + "&start=1"
////        + "&SearchTarget=Book"
////        + "&output=xml"
////        + "&Version=20131101";
////
////        // XML 데이터를 가져오기
////        String response = restTemplate.getForObject(url, String.class);
////
////        // XML 데이터를 파싱
////        XmlMapper xmlMapper = new XmlMapper();
////        AladinApiXmlResponse apiResponse = xmlMapper.readValue(response, AladinApiXmlResponse.class);
////
////        List<Book> books = new ArrayList<>();
////        List<BookTag> bookTags = new ArrayList<>();
////        List<BookContributor> bookContributors = new ArrayList<>();
////        List<BookCategory> bookCategories = new ArrayList<>();
////
////        for (AladinBookXmlResponse item : apiResponse.getItems()) {
////            LocalDate publicationDate = LocalDate.parse(item.getPubDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
////
////            // 1. Book 생성
////            Book book = Book.builder()
////                    .title(item.getTitle())
////                    .index("index test")
////                    .description(item.getDescription())
////                    .quantity(100)
////                    .publisher(item.getPublisher())
////                    .isbn(item.getIsbn13())
////                    .price(item.getPriceStandard())
////                    .saleprice(item.getPriceSales())
////                    .publicationDate(publicationDate)
////                    .pages(100) // 기본값
////                    .bookStatus(BookStatus.ONSALE)
////                    .giftPackaging(true)
////                    .viewCount(0L)
////                    .build();
////            books.add(book);
////
////            String[] authors = item.getAuthor().split(", "); // 쉼표로 기여자 분리
////
////            for (String author : authors) {
////                // 이름과 역할 분리
////                String[] nameAndRole = author.split(" \\("); // " 이름 (역할" 형태로 분리
////                String contributorName = nameAndRole[0].trim();
////                String roleName = nameAndRole.length > 1 ? nameAndRole[1].replace(")", "").trim() : "지은이"; // 역할 없으면 기본값
////
////                // Contributor 저장
////                Contributor contributor = contributorRepositroy.findByContributorNameAndRoleName(contributorName, roleName)
////                        .orElseGet(() -> contributorRepositroy.save(
////                                Contributor.builder()
////                                        .contributorName(contributorName)
////                                        .contributorRole(roleName)
////                                        .build()));
////
////                // BookContributor 생성
////                BookContributor bookContributor = BookContributor.builder()
////                        .book(book)
////                        .contributor(contributor)
////                        .build();
////                bookContributors.add(bookContributor);
////            }
////
////            // 4. Tag 처리: 데이터베이스에서 중복 확인
////            String tagName = item.getMallType();
////            Tag tag = tagRepository.findByTagName(tagName)
////                    .orElseGet(() -> tagRepository.save(
////                            Tag.builder()
////                                    .tagName(tagName)
////                                    .build())
////                    ); // 중복 확인 후 저장
////
////            // 5. BookTag 생성
////            BookTag bookTag = BookTag.builder()
////                    .book(book)
////                    .tag(tag)
////                    .build();
////            bookTags.add(bookTag);
////
////            // 6. Category 처리
////            String categoryName = item.getCategoryName();
////            if (categoryName != null) {
////                Category category = getOrCreateCategory(categoryName);
////                BookCategory bookCategory = BookCategory.builder()
////                        .book(book)
////                        .catagory(category)
////                        .build();
////                bookCategories.add(bookCategory);
////            }
////        }
////
////        // 저장
////        bookRepository.saveAll(books);        // 책 저장
////        bookTagRepository.saveAll(bookTags); // BookTag 저장
////        bookContributorRepository.saveAll(bookContributors); // BookContributor 저장
////        bookCategoryRepository.saveAll(bookCategories); // BookCategory 저장
////    }
////
////   private Category getOrCreateCategory(String categoryName) {
////    // HTML 엔티티 디코딩
////    String decodedCategoryName = StringEscapeUtils.unescapeHtml4(categoryName);
////
////    // 계층 분리
////    String[] categoryLevels = decodedCategoryName.split(">");
////    Category parent = null;
////
////    for (String level : categoryLevels) {
////        String trimmedName = level.trim();
////
////        // 최상위 카테고리 중복 체크
////        if (parent == null) {
////            parent = categoryRepository.findByCategoryNameAndParentIsNull(trimmedName)
////                    .orElseGet(() -> categoryRepository.save(
////                            Category.builder()
////                                    .categoryName(trimmedName)
////                                    .parent(null)
////                                    .build()));
////        } else {
////            // 하위 카테고리 중복 체크
////            Category finalParent = parent;
////            parent = categoryRepository.findByCategoryNameAndParent(trimmedName, parent)
////                    .orElseGet(() -> categoryRepository.save(
////                            Category.builder()
////                                    .categoryName(trimmedName)
////                                    .parent(finalParent)
////                                    .build()));
////        }
////    }
////
////    return parent; // 최하위 카테고리를 반환
////}
//
//    @Transactional
//    public void fetchAndSaveBestsellerBooks() throws Exception {
//        String baseUrl = "http://www.aladin.co.kr/ttb/api/ItemList.aspx"
//                + "?ttbkey=ttbchlim201008001"
//                + "&QueryType=Bestseller"
//                + "&MaxResults=50"
//                + "&SearchTarget=Book"
//                + "&output=xml"
//                + "&Version=20131101";
//
//        XmlMapper xmlMapper = new XmlMapper();
//
//        List<Book> books = new ArrayList<>();
//        List<BookTag> bookTags = new ArrayList<>();
//        List<BookContributor> bookContributors = new ArrayList<>();
//        List<BookCategory> bookCategories = new ArrayList<>();
//
//        // 4번 반복해서 요청
//        for (int i = 1; i <= 200; i += 50) { // start = 1, 51, 101, 151
//            String url = baseUrl + "&start=" + i;
//            String response = restTemplate.getForObject(url, String.class);
//
//            AladinApiXmlResponse apiResponse = xmlMapper.readValue(response, AladinApiXmlResponse.class);
//
//            for (AladinBookXmlResponse item : apiResponse.getItems()) {
//                // 중복 ISBN 체크
//                if (bookRepository.existsByIsbn(item.getIsbn13())) {
//                    continue; // 중복된 책은 무시
//                }
//
//                // Book 생성
//                Book book = Book.builder()
//                        .title(item.getTitle())
//                        .index("index test")
//                        .description(item.getDescription())
//                        .quantity(100)
//                        .publisher(item.getPublisher())
//                        .isbn(item.getIsbn13())
//                        .price(item.getPriceStandard())
//                        .saleprice(item.getPriceSales())
//                        .publicationDate(LocalDate.parse(item.getPubDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
//                        .pages(100)
//                        .bookStatus(BookStatus.ONSALE)
//                        .giftPackaging(true)
//                        .viewCount(0L)
//                        .build();
//                books.add(book);
//
//                // Contributor 처리
//                String[] authors = item.getAuthor().split(", ");
//                for (String author : authors) {
//                    String[] nameAndRole = author.split(" \\(");
//                    String contributorName = nameAndRole[0].trim();
//                    String roleName = nameAndRole.length > 1 ? nameAndRole[1].replace(")", "").trim() : "지은이";
//
//                    Contributor contributor = contributorRepositroy.findByContributorNameAndRoleName(contributorName, roleName)
//                            .orElseGet(() -> contributorRepositroy.save(
//                                    Contributor.builder()
//                                            .contributorName(contributorName)
//                                            .contributorRole(roleName)
//                                            .build()));
//
//                    bookContributors.add(BookContributor.builder().book(book).contributor(contributor).build());
//                }
//
//                // Tag 처리
//                String tagName = item.getMallType();
//                Tag tag = tagRepository.findByTagName(tagName)
//                        .orElseGet(() -> tagRepository.save(Tag.builder().tagName(tagName).build()));
//
//                bookTags.add(BookTag.builder().book(book).tag(tag).build());
//
//                // Category 처리
//                String categoryName = item.getCategoryName();
//                if (categoryName != null) {
//                    Category category = getOrCreateCategory(categoryName);
//                    bookCategories.add(BookCategory.builder().book(book).catagory(category).build());
//                }
//            }
//        }
//
//        // 저장
//        bookRepository.saveAll(books);
//        bookTagRepository.saveAll(bookTags);
//        bookContributorRepository.saveAll(bookContributors);
//        bookCategoryRepository.saveAll(bookCategories);
//    }
//
//    private Category getOrCreateCategory(String categoryName) {
//        String decodedCategoryName = StringEscapeUtils.unescapeHtml4(categoryName);
//        String[] categoryLevels = decodedCategoryName.split(">");
//        Category parent = null;
//
//        for (String level : categoryLevels) {
//            String trimmedName = level.trim();
//
//            // 최상위 카테고리
//            if (parent == null) {
//                parent = categoryRepository.findByCategoryNameAndParentIsNull(trimmedName)
//                        .orElseGet(() -> categoryRepository.save(
//                                Category.builder()
//                                        .categoryName(trimmedName)
//                                        .parent(null)
//                                        .build()));
//            } else {
//                // 하위 카테고리
//                Category finalParent = parent;
//                parent = categoryRepository.findByCategoryNameAndParent(trimmedName, finalParent)
//                        .orElseGet(() -> categoryRepository.save(
//                                Category.builder()
//                                        .categoryName(trimmedName)
//                                        .parent(finalParent)
//                                        .build()));
//            }
//        }
//
//        return parent;
//    }
//
//
//}

package com.simsimbookstore.apiserver.books.book.aladin;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.books.bookcategory.entity.BookCategory;
import com.simsimbookstore.apiserver.books.bookcategory.repository.BookCategoryRepository;
import com.simsimbookstore.apiserver.books.bookcontributor.entity.BookContributor;
import com.simsimbookstore.apiserver.books.bookcontributor.repository.BookContributorRepository;
import com.simsimbookstore.apiserver.books.bookimage.entity.BookImagePath;
import com.simsimbookstore.apiserver.books.bookimage.repoitory.BookImageRepoisotry;
import com.simsimbookstore.apiserver.books.booktag.entity.BookTag;
import com.simsimbookstore.apiserver.books.booktag.repositry.BookTagRepository;
import com.simsimbookstore.apiserver.books.category.entity.Category;
import com.simsimbookstore.apiserver.books.category.repository.CategoryRepository;
import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
import com.simsimbookstore.apiserver.books.contributor.repository.ContributorRepositroy;
import com.simsimbookstore.apiserver.books.tag.domain.Tag;
import com.simsimbookstore.apiserver.books.tag.repository.TagRepository;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AladinApiService {

    private final RestTemplate restTemplate;
    private final BookRepository bookRepository;
    private final TagRepository tagRepository;
    private final BookTagRepository bookTagRepository;
    private final ContributorRepositroy contributorRepositroy;
    private final BookContributorRepository bookContributorRepository;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final BookImageRepoisotry bookImageRepoisotry;

    public AladinApiService(RestTemplate restTemplate, BookRepository bookRepository,
                            TagRepository tagRepository, BookTagRepository bookTagRepository,
                            ContributorRepositroy contributorRepositroy, BookContributorRepository bookContributorRepository,
                            CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository, BookImageRepoisotry bookImageRepoisotry) {
        this.restTemplate = restTemplate;
        this.bookRepository = bookRepository;
        this.tagRepository = tagRepository;
        this.bookTagRepository = bookTagRepository;
        this.contributorRepositroy = contributorRepositroy;
        this.bookContributorRepository = bookContributorRepository;
        this.categoryRepository = categoryRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.bookImageRepoisotry = bookImageRepoisotry;
    }

    @Transactional
    public void fetchAndSaveBestsellerBooks() throws Exception {
        String baseUrl = "https://www.aladin.co.kr/ttb/api/ItemList.aspx"
                + "?ttbkey=ttbchlim201008001"
                + "&QueryType=Bestseller"
                + "&MaxResults=50"
                + "&SearchTarget=Book"
                + "&output=xml"
                + "&Version=20131101";

        XmlMapper xmlMapper = new XmlMapper();

        for (int start = 1; start <= 2; start += 1) {
            String url = baseUrl + "&start=" + start;
            System.out.println("Requesting: " + url);

            String response = restTemplate.getForObject(url, String.class);
            AladinApiXmlResponse apiResponse = xmlMapper.readValue(response, AladinApiXmlResponse.class);

            if (apiResponse.getItems().isEmpty()) {
                System.out.println("No data returned for start=" + start);
                continue;
            }

            for (AladinBookXmlResponse item : apiResponse.getItems()) {
                if (item.getIsbn13() == null || bookRepository.existsByIsbn(item.getIsbn13())) {
                    System.out.println("Skipping duplicate or missing ISBN: " + item.getIsbn13());
                    continue;
                }

                Book book = createBookEntity(item);
                bookRepository.save(book);

                saveContributors(book, item.getAuthor());
                saveTags(book, item.getMallType());
                saveCategories(book, item.getCategoryName());
                saveImagePath(book, item.getCover());
            }
        }
    }

    private void saveImagePath(Book book, String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }

        BookImagePath bookImagePath = BookImagePath.builder()
                .book(book)
                .imagePath(imagePath)
                .thumbnail(false)
                .build();

        bookImageRepoisotry.save(bookImagePath);

    }


    private Book createBookEntity(AladinBookXmlResponse item) {
        LocalDate publicationDate = LocalDate.parse(item.getPubDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return Book.builder()
                .title(item.getTitle())
                .bookIndex("index test")
                .description(item.getDescription())
                .quantity(100)
                .publisher(item.getPublisher())
                .isbn(item.getIsbn13())
                .price(item.getPriceStandard())
                .saleprice(item.getPriceSales())
                .publicationDate(publicationDate)
                .pages(100)
                .bookStatus(BookStatus.ONSALE)
                .giftPackaging(true)
                .viewCount(0L)
                .build();
    }

    private void saveContributors(Book book, String authorString) {
        String[] authors = authorString.split(", ");
        for (String author : authors) {
            String[] nameAndRole = author.split(" \\(");
            String contributorName = nameAndRole[0].trim();
            String roleName = nameAndRole.length > 1 ? nameAndRole[1].replace(")", "").trim() : "지은이";

            Contributor contributor = contributorRepositroy.findByContributorNameAndRoleName(contributorName, roleName)
                    .orElseGet(() -> contributorRepositroy.save(
                            Contributor.builder()
                                    .contributorName(contributorName)
                                    .contributorRole(roleName)
                                    .build()));

            bookContributorRepository.save(BookContributor.builder().book(book).contributor(contributor).build());
        }
    }

    private void saveTags(Book book, String tagName) {
        Tag tag = tagRepository.findByTagName(tagName)
                .orElseGet(() -> tagRepository.save(Tag.builder().tagName(tagName).build()));
        bookTagRepository.save(BookTag.builder().book(book).tag(tag).build());
    }

    private void saveCategories(Book book, String categoryName) {
        if (categoryName == null) return;
        Category category = getOrCreateCategory(categoryName);
        bookCategoryRepository.save(BookCategory.builder().book(book).catagory(category).build());
    }

    private Category getOrCreateCategory(String categoryName) {
        String decodedCategoryName = StringEscapeUtils.unescapeHtml4(categoryName);
        String[] categoryLevels = decodedCategoryName.split(">");
        Category parent = null;

        for (String level : categoryLevels) {
            String trimmedName = level.trim();
            if (parent == null) {
                parent = categoryRepository.findByCategoryNameAndParentIsNull(trimmedName)
                        .orElseGet(() -> categoryRepository.save(
                                Category.builder().categoryName(trimmedName).parent(null).build()));
            } else {
                Category finalParent = parent;
                parent = categoryRepository.findByCategoryNameAndParent(trimmedName, finalParent)
                        .orElseGet(() -> categoryRepository.save(
                                Category.builder().categoryName(trimmedName).parent(finalParent).build()));
            }
        }
        return parent;
    }
}

