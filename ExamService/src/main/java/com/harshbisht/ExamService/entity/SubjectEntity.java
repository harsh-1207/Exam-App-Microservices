package com.harshbisht.ExamService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    /**
     * DESIGN NOTE: CascadeType.ALL + orphanRemoval = true means deleting a Subject
     * will cascade-delete ALL its Exams, Questions, and Options in one shot.
     * SubjectService.deleteSubject() guards against this by checking if exams exist
     * first — but that check is not atomic. Under concurrent load, two requests
     * could both pass the isEmpty() check before either deletes.
     *
     * If cascade deletion is intentional (deleting a subject wipes everything),
     * remove the isEmpty() guard and document the behaviour clearly.
     * If it should be blocked, change cascade to CascadeType.PERSIST, MERGE
     * so deletion is rejected by the FK constraint at the DB level.
     *
     * Left as-is for now since SubjectService provides an application-level guard.
     * Consider a DB-level unique constraint + no cascade as the safer long-term choice.
     */
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamEntity> exams = new ArrayList<>();
}