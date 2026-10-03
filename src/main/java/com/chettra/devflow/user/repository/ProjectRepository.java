package com.chettra.devflow.user.repository;

import com.chettra.devflow.user.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository <Project, Long>{

}
