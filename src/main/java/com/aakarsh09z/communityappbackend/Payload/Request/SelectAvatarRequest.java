package com.aakarsh09z.communityappbackend.Payload.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SelectAvatarRequest {
    @NotEmpty
    @Email(regexp="[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",flags = Pattern.Flag.CASE_INSENSITIVE,message = "Invalid email format")
    private String email;
    @NotEmpty(message = "Username field is required")
    private String userId;
    @NotEmpty(message = "profileImageUrl cannot be empty")
    private String profileImageUrl;
}
