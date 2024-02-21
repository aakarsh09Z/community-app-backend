package com.aakarsh09z.communityappbackend.Payload.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageSeenPayload {
    private Long chatId;
    private String token;
}
