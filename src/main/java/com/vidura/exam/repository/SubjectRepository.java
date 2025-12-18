package com.vidura.exam.repository;

import com.vidura.exam.entities.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    long countSubjectsByName(String name);
    Page<Subject> findAllByIsActive(boolean isActive, Pageable pageable);
}
