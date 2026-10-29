
package com.chettra.devflow.service;

import com.chettra.devflow.dto.request.ProjectPageRequest;
import com.chettra.devflow.entity.Project;
import com.chettra.devflow.entity.ProjectMember;
import com.chettra.devflow.repository.ProjectRepository;
import com.chettra.devflow.specification.ProjectSpecification;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectQueryService {

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public Page<Project> findVisible(
            Pageable pageable,
            Long currentUserId,
            boolean administrator) {

        Specification<Project> specification =
                Specification.where(ProjectSpecification.isActive());

        if (!administrator) {
            specification = specification.and(
                    ProjectSpecification.visibleToUser(currentUserId)
            );
        }

        return projectRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Project> search(
            ProjectPageRequest request,
            Long currentUserId,
            boolean administrator) {

        Sort.Direction direction =
                Sort.Direction.valueOf(
                        request.directionOrDefault().toUpperCase()
                );

        PageRequest pageable = PageRequest.of(
                request.pageOrDefault(),
                request.sizeOrDefault(),
                Sort.by(direction, request.sortByOrDefault())
        );

        Specification<Project> specification = Specification
                .where(ProjectSpecification.isActive())
                .and(ProjectSpecification.hasStatus(request.status()))
                .and(ProjectSpecification.hasOwnerId(request.ownerId()));

        if (!administrator) {
            specification = specification.and(
                    ProjectSpecification.visibleToUser(currentUserId)
            );
        }

        return projectRepository.findAll(specification, pageable);
    }

    public static Specification<Project> visibleToUser(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) {
                return cb.disjunction();
            }

            Subquery<Long> membershipQuery = query.subquery(Long.class);
            var membership = membershipQuery.from(ProjectMember.class);

            membershipQuery.select(membership.get("project").get("id"));
            membershipQuery.where(
                    cb.equal(membership.get("project").get("id"), root.get("id")),
                    cb.equal(membership.get("user").get("id"), userId),
                    cb.isTrue(membership.get("active"))
            );

            return cb.or(
                    cb.equal(root.get("owner").get("id"), userId),
                    cb.exists(membershipQuery)
            );
        };
    }
}
