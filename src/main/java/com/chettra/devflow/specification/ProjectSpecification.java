package com.chettra.devflow.specification;

import com.chettra.devflow.entity.Project;
import com.chettra.devflow.entity.ProjectStatus;
import org.springframework.data.jpa.domain.Specification;

public final class ProjectSpecification {
    private ProjectSpecification() {
    }
    public static  Specification<Project> hasStatus( String status){
        return (root, query, cb)-> {
            if (status == null || status.isBlank()){
                return cb.conjunction();
            }
            return cb.equal(root.get("status").as(String.class),status);
        };
    }
    public static  Specification<Project> hasOwnerId(Long ownerId){
        return (root, query, cb) ->{
            if (ownerId == null){
                return cb.conjunction();
            }
            return cb.equal(root.get("owner").get("id"),ownerId);
        };
    }
}
