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
@Table(name = "student_profiles")
public class StudentProfile {
    @Id
    @Column(nullable = false)
    private String user_id;


    private String facebook_link;

    private String github_link;

    @Column(nullable = false)
    private String major;

    @Column(nullable = false)
    private String Phone_number;

    @Column(nullable = false)
    private String university;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Column(nullable = false, updatable = false)
    private String created_by;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime last_modified_at;

    @Column(nullable = false, updatable = false)
    private String last_modified_by;

    private String profile_picture;

    @OneToMany(mappedBy = "studentProfile")
    private List<Enrollment> enrollment;
}
