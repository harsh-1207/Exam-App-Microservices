package com.harshbisht.ResultService.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionId;

    private Long selectedOptionId;

    private Boolean correct;

    @ManyToOne
    @JoinColumn(name = "result_id")
    private ResultEntity result;
}
