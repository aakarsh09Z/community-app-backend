package com.aakarsh09z.communityappbackend.Payload.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelRequest {
    private String name;
    private String type;
    private Long communityId;
}
