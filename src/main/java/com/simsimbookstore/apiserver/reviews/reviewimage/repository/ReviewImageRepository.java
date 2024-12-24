package com.simsimbookstore.apiserver.reviews.reviewimage.repository;

import com.simsimbookstore.apiserver.reviews.reviewimage.entity.ReviewImagePath;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageRepository extends JpaRepository<ReviewImagePath,Long> {

}
