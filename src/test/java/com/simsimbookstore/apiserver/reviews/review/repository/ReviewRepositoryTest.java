package com.simsimbookstore.apiserver.reviews.review.repository;

import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.reviews.review.entity.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;



@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    BookRepository bookRepository;


    @Test
    void saveData(){



        //userRepository.save()

        //bookRepository.save()


        //reviewRepository.save()
    }

    @Test
    @Sql("review.sql")
    void readData(){

    }

    @Test
    @Sql
    void updateData(){

    }

    @Test
    void deleteData(){

    }

}