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
import com.simsimbookstore.apiserver.storage.service.ObjectServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
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
    private final ObjectServiceImpl objectService;

    @Transactional
    public void fetchAndSaveBestsellerBooks() throws Exception {
        String baseUrl = "https://www.aladin.co.kr/ttb/api/ItemList.aspx"
                + "?ttbkey=ttbchlim201008001"
                + "&QueryType=Bestseller"
                + "&MaxResults=50"
                + "&output=xml"
                + "&Version=20131101"
                + "&Cover=Big"; // 큰 크기 이미지 요청


        // 다양한 SearchTarget 값을 정의
        String[] searchTargets = {"Book", "Foreign", "Music", "DVD", "Used", "eBook"};


        XmlMapper xmlMapper = new XmlMapper();

        for (String searchTarget : searchTargets) {
            for (int start = 1; start <= 4; start++) {
                String url = baseUrl + "&SearchTarget=" + searchTarget + "&start=" + start;
                System.out.println("Requesting: " + url);

                String response = restTemplate.getForObject(url, String.class);
                AladinApiXmlResponse apiResponse = xmlMapper.readValue(response, AladinApiXmlResponse.class);

                if (apiResponse.getItems().isEmpty()) {
                    System.out.println("No data returned for SearchTarget=" + searchTarget + ", start=" + start);
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
    }


    private void saveImagePath(Book book, String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }
        try {
            String uploadedImageUrl = objectService.uploadObjects(imagePath);
            BookImagePath bookImagePath = BookImagePath.builder()
                    .book(book)
                    .imagePath(uploadedImageUrl)
                    .imageType(BookImagePath.ImageType.THUMBNAIL)
                    .build();

            bookImageRepoisotry.save(bookImagePath);
        } catch (Exception e) {
            log.error("Failed to upload and save image");
        }

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
                .orElseGet(() -> tagRepository.save(Tag.builder().tagName(tagName).isActivated(true).build()));
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

