package com.pollapp.pollapp.repository;

import com.pollapp.pollapp.model.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {
    Optional<Poll> findById(Long userId);

    List<Poll> findAllByOrderByCreatedAtDesc();

    List<Poll> findByCreatedByOrderByCreatedAtDesc(Long userId);

    long countByCreatedBy(Long userId);

    List<Poll> findByIdIn(List<Long> pollIds);

    List<Poll> findByIdIn(List<Long> pollIds, Sort sort);
}
