package com.chettra.devflow.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public record ProjectPageRequest(
        @PositiveOrZero
        Integer page,

        @Min(1)
        @Max(100)
        Integer size,

        @Pattern(
                regexp = "name|createdAt|status",
                message = "sortBy must be name, createdAt, or status"
        )
        String sortBy,
        String direction,
        String status,

        @PositiveOrZero
        Long ownerId
)
 {
     public int pageOrDefault() {
         return page == null ? 0 : page;
     }
     public int sizeOrDefault() {
         return size == null ? 10 : size;
     }
     public String sortByOrDefault() {
        return sortBy == null ? "createdAt" : sortBy;
     }
     public String directionOrDefault() {
        return direction == null ? "DESC" : direction;
     }
}
