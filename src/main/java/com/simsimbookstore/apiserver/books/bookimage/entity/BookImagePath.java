package com.simsimbookstore.apiserver.books.bookimage.entity;


import com.simsimbookstore.apiserver.books.book.entity.Book;
import jakarta.persistence.*;
import lombok.*;


@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "book_image_paths")
public class BookImagePath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_image_path_id")
    private Long bookImagePathId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;


    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    public enum ImageType{
         THUMBNAIL, DETAIL;

    }

}
