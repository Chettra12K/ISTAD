package com.chettra.devflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
	@Column(length = 1000)
    private String description;
	@Column(nullable = false)
	private Boolean active;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;
	@Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id" , nullable = false , foreignKey = @ForeignKey(name = "fk_project_owner"))
    private User owner;
	
	@PrePersist
	protected void onCreate(){
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
		if (active == null){
			active = true;
		}
	}
	@PreUpdate
	protected  void  onUpdate(){
		updatedAt = LocalDateTime.now();
	}

}
