package co.istad.chettra.elearning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "instructor_profiles")
public class InstructorProfile {
    @Id
    @Column(nullable = false)
    private String user_id;

    @Column(nullable = false)
    private String biography;

    private String facebook_link;

    private String github_link;

    @Column(nullable = false)
    private String job_title;

    @Column(nullable = false)
    private String Phone_number;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Column(nullable = false,updatable = false)
    private String created_by;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime last_modified_at;

    @Column(nullable = false,updatable = false)
    private String last_modified_by;

    @OneToMany(mappedBy = "instructorProfile")
    private List<Course> course;
}
