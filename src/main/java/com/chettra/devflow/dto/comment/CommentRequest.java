package com.chettra.devflow.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequest {

    @NotBlank(message = "Comment content is required")
    @Size(max = 5000, message = "Comment must not exceed 5000 characters")
    private String content;
}
