package co.istad.chettra.elearning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime enrolled_at;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime payment_at;

    @Column(nullable = false)
    private String payment_method;

    @Column(nullable = false)
    private Boolean payment_status;

    @Column(insertable=false, updatable=false)
    private int course_id;

    @Column(insertable=false, updatable=false)
    private String student_id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Column(nullable = false,updatable = false)
    private String created_by;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime last_modified_at;

    @Column(nullable = false, updatable = false)
    private String last_modified_by;

    @ManyToOne
    private Course course;
    @ManyToOne
    private StudentProfile studentProfile;
}
