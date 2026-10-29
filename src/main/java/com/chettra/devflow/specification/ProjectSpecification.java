
package com.chettra.devflow.specification;

import com.chettra.devflow.entity.Project;
import com.chettra.devflow.entity.ProjectMember;
import com.chettra.devflow.entity.ProjectStatus;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public final class ProjectSpecification {

    private ProjectSpecification() {
    }

    public static Specification<Project> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank()) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("status").as(String.class),
                    status
            );
        };
    }

    public static Specification<Project> hasOwnerId(Long ownerId) {
        return (root, query, cb) -> {
            if (ownerId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("owner").get("id"), ownerId);
        };
    }

    public static Specification<Project> isActive() {
        return (root, query, cb) ->
                cb.isTrue(root.get("active"));
    }

    /**
     * Restricts projects to those owned by the user or
     * associated with the user through an active membership.
     */
    public static Specification<Project> visibleToUser(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) {
                return cb.disjunction();
            }

            Subquery<Long> membershipQuery =
                    query.subquery(Long.class);

            var membership = membershipQuery.from(ProjectMember.class);

            membershipQuery.select(
                    membership.get("project").get("id")
            );

            membershipQuery.where(
                    cb.equal(
                            membership.get("project").get("id"),
                            root.get("id")
                    ),
                    cb.equal(
                            membership.get("user").get("id"),
                            userId
                    ),
                    cb.isTrue(membership.get("active"))
            );

            return cb.or(
                    cb.equal(root.get("owner").get("id"), userId),
                    cb.exists(membershipQuery)
            );
        };
    }
}
