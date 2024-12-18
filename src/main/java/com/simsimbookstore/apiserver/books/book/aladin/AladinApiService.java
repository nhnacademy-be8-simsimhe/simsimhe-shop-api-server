package com.simsimbookstore.apiserver.books.book.aladin;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.entity.BookStatus;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.books.bookcontributor.entity.BookContributor;
import com.simsimbookstore.apiserver.books.bookcontributor.repository.BookContributorRepository;
import com.simsimbookstore.apiserver.books.booktag.entity.BookTag;
import com.simsimbookstore.apiserver.books.booktag.repositry.BookTagRepository;
import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
import com.simsimbookstore.apiserver.books.contributor.repository.ContributorRepositroy;
import com.simsimbookstore.apiserver.books.tag.domain.Tag;
import com.simsimbookstore.apiserver.books.tag.repository.TagRepository;
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

    public AladinApiService(RestTemplate restTemplate, BookRepository bookRepository,
                            TagRepository tagRepository, BookTagRepository bookTagRepository, ContributorRepositroy contributorRepositroy, BookContributorRepository bookContributorRepository) {
        this.restTemplate = restTemplate;
        this.bookRepository = bookRepository;
        this.tagRepository = tagRepository;
        this.bookTagRepository = bookTagRepository;
        this.contributorRepositroy = contributorRepositroy;
        this.bookContributorRepository = bookContributorRepository;
    }

    @Transactional
    public void fetchAndSaveBooks(String query) throws Exception {
        String url = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx?ttbkey=ttbchlim201008001"
                + "&Query=" + query
                + "&QueryType=Name"
                + "&MaxResults=10"
                + "&start=1"
                + "&SearchTarget=Book"
                + "&output=xml"
                + "&Version=20131101";

        // XML 데이터를 가져오기
        String response = restTemplate.getForObject(url, String.class);

        // XML 데이터를 파싱
        XmlMapper xmlMapper = new XmlMapper();
        AladinApiXmlResponse apiResponse = xmlMapper.readValue(response, AladinApiXmlResponse.class);

        List<Book> books = new ArrayList<>();
        List<BookTag> bookTags = new ArrayList<>();
        List<BookContributor> bookContributors = new ArrayList<>();

        for (AladinBookXmlResponse item : apiResponse.getItems()) {
            LocalDate publicationDate = LocalDate.parse(item.getPubDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 1. Book 생성
            Book book = Book.builder()
                    .title(item.getTitle())
                    .index("index test")
                    .description(item.getDescription())
                    .quantity(100)
                    .publisher(item.getPublisher())
                    .isbn(item.getIsbn13())
                    .price(item.getPriceStandard())
                    .saleprice(item.getPriceSales())
                    .publicationDate(publicationDate)
                    .pages(100) // 기본값
                    .bookStatus(BookStatus.ONSALE)
                    .giftPackaging(true)
                    .viewCount(0L)
                    .build();
            books.add(book);

            String[] authors = item.getAuthor().split(", "); // 쉼표로 기여자 분리

            for (String author : authors) {
                // 이름과 역할 분리
                String[] nameAndRole = author.split(" \\("); // " 이름 (역할" 형태로 분리
                String contributorName = nameAndRole[0].trim();
                String roleName = nameAndRole.length > 1 ? nameAndRole[1].replace(")", "").trim() : "지은이"; // 역할 없으면 기본값

                // Contributor 저장
                Contributor contributor = contributorRepositroy.findByContributorNameAndRoleName(contributorName, roleName)
                        .orElseGet(() -> contributorRepositroy.save(
                                Contributor.builder()
                                        .contributorName(contributorName)
                                        .contributorRole(roleName)
                                        .build()));

                // BookContributor 생성
                BookContributor bookContributor = BookContributor.builder()
                        .book(book)
                        .contributor(contributor)
                        .build();
                bookContributors.add(bookContributor);
            }


            // 4. Tag 처리: 데이터베이스에서 중복 확인
            String tagName = item.getMallType();
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> tagRepository.save(
                            Tag.builder()
                                    .tagName(tagName)
                                    .build())
                    ); // 중복 확인 후 저장

            // 5. BookTag 생성
            BookTag bookTag = BookTag.builder()
                    .book(book)
                    .tag(tag)
                    .build();
            bookTags.add(bookTag);
        }

        // 6. 저장
        bookRepository.saveAll(books);        // 책 저장
        bookTagRepository.saveAll(bookTags); // BookTag 저장
        bookContributorRepository.saveAll(bookContributors); // BookContributor 저장
    }
}
