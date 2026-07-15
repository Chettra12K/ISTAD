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
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String duration;

    @Column(nullable = false)
    private Boolean is_deleted;

    @Column(nullable = false)
    private Boolean is_published;

    private String slug;

    private String thumbnail;

    @Column(nullable = false)
    private String title;

    private String youtube;

    @Column(insertable=false, updatable=false)
    private int course_id;

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

    @ManyToOne
    private Course course;

    @OneToMany(mappedBy = "video")
    private List<Comment> comments;
}
