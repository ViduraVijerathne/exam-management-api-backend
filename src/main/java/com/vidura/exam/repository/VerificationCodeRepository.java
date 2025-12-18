package com.vidura.exam.repository;

import com.vidura.exam.entities.User;
import com.vidura.exam.entities.VerificationCode;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    List<VerificationCode> getVerificationCodesByUser(User user);

    List<VerificationCode> findByUserAndGenerateTimeBefore(User user, LocalDateTime fiveMinutesAgo);

    List<VerificationCode> findByUserAndGenerateTimeAfter(User user, LocalDateTime generateTimeAfter);
    @Modifying
    @Transactional
    void deleteAllByUser(User user);
    Optional<VerificationCode> findByUserAndGenerateTimeAfterAndCode(User user, LocalDateTime generateTimeAfter, String code);
}
