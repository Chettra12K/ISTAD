package co.istad.chettra.elearning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 4)
    private Integer count_rating;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    private String description;

    @Column(nullable = false)
    private Float discount_percent;

    @Column(nullable = false)
    private Boolean is_deleted;

    @Column(nullable = false)
    private Boolean is_published;

    private String keyword;

    @Column(nullable = false)
    private String level;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private Float star_rating;

    @Column(nullable = false)
    private String thumbnail;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Float total_hours;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime updated_at;

    @Column(insertable=false, updatable=false)
    private int category_id;

    @Column(insertable=false, updatable=false)
    private String instructor_id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime last_modified_at;

    @Column(nullable = false,updatable = false)
    private String last_modified_by;

    @Column(nullable = false,updatable = false)
    private String created_by;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private InstructorProfile instructorProfile;
    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollment;
    @OneToMany(mappedBy = "course")
    private List<Video> video;
}
