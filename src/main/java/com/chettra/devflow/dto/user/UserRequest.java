package com.chettra.devflow.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    private String email;

    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstname;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastname;

    @Size(max = 500, message = "Profile image URL must not exceed 500 characters")
    private String profileImage;
}