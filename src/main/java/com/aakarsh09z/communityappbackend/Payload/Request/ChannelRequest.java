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
public class ChannelRequest {
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    private String name;
    private String type;
    private Long communityId;
}
