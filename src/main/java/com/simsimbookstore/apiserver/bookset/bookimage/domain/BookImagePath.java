package com.simsimbookstore.apiserver.bookset.bookimage.domain;


import com.simsimbookstore.apiserver.bookset.book.domain.Book;
import jakarta.persistence.*;
import lombok.*;


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


}
