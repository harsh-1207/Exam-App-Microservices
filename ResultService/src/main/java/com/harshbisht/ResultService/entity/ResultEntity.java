package com.harshbisht.ResultService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "results",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"examId", "studentId"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID of exam from ExamService
    @Column(nullable = false)
    private Long examId;

    // ID of student from Auth/User service
    @Column(nullable = false)
    private Long studentId;

    // Total possible marks of exam
    @Column(nullable = false)
    private Long totalMarks;

    // Student's obtained marks
    @Column(nullable = false)
    private Long score;

    // Percentage (optional but useful for reporting)
    private Double percentage;

    // When the exam was submitted
    @Column(nullable = false)
    private LocalDateTime submittedAt;

    // One result → many answers
    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerEntity> answers;
}
