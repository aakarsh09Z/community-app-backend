package com.aakarsh09z.communityappbackend.Payload.Request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters")
    private String content;
    private Long postId;
}
