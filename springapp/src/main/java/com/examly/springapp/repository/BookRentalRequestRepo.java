package com.examly.springapp.repository;

import com.examly.springapp.model.BookRentalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRentalRequestRepo extends JpaRepository<BookRentalRequest, Long> {
    List<BookRentalRequest> findByUserUserId(Long userId);
    List<BookRentalRequest> findByBookBookId(Long bookId);
    boolean existsByUserUserIdAndBookBookIdAndStatusIn(Long userId, Long bookId, List<String> statuses);
    boolean existsByBookBookIdAndStatusIn(Long bookId, List<String> statuses);
    List<BookRentalRequest> findByBookBookIdAndStatus(Long bookId, String status);
}
