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
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String icon;

    @Column(nullable = false)
    private boolean is_deleted;

    @Column(nullable = false, length = 50)
    private String name;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime created_at;

    @Column(nullable = false,updatable = false)
    private String created_by;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime last_modified_at;

    @Column(nullable = false,updatable = false)
    private String last_modified_by;

    @OneToMany(mappedBy = "category")
    private List<Course> course;
}
