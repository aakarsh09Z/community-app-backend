package com.aakarsh09z.communityappbackend.Payload.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private String sender;
    private String type;
    private String content;
    private LocalDateTime time;
    private String token;
}
