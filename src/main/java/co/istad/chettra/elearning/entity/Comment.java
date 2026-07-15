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
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private boolean is_deleted;

    private String text;

    @Column(nullable = false)
    private int parent_id;

    @Column(insertable=false, updatable=false)
    private int video_id;

    @Column(nullable = false, updatable = false)
    private String created_by;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime last_modified_at;

    @Column(nullable = false, updatable = false)
    private String last_modified_by;

    @ManyToOne
    private Video video;

    @ManyToOne
    private Comment comment;
}
