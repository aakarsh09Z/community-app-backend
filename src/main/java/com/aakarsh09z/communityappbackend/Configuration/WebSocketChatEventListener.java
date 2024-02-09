package com.aakarsh09z.communityappbackend.Configuration;

import com.aakarsh09z.communityappbackend.Entity.Chat;
import com.aakarsh09z.communityappbackend.Payload.Response.ChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class WebSocketChatEventListener {
    private final SimpMessageSendingOperations messagingTemplate;
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Received a new web socket connection");
    }
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        Long id = (Long) headerAccessor.getSessionAttributes().get("id");
        if(username != null) {
            ChatDto chatDto = new ChatDto();
            chatDto.setType("Leave");
            chatDto.setSender(username);
            chatDto.setTime(LocalDateTime.now());
            messagingTemplate.convertAndSend("/topic/"+id, chatDto);
        }
    }
}
