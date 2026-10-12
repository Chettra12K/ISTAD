package com.chettra.devflow.common.validation;

import java.util.Set;

public final class ProjectSortField {

    private ProjectSortField() {
    }
    private static final Set<String> ALLOWED_FIELDS = Set.of("createdAt", "updatedAt","name", "status");

    public static boolean isAllowed(String field) {
        return ALLOWED_FIELDS.contains(field);
    }
}
