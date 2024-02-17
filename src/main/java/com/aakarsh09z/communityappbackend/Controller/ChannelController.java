package com.aakarsh09z.communityappbackend.Controller;

import com.aakarsh09z.communityappbackend.Entity.Channel;
import com.aakarsh09z.communityappbackend.Entity.Chat;
import com.aakarsh09z.communityappbackend.Entity.User;
import com.aakarsh09z.communityappbackend.Payload.Request.ChannelRequest;
import com.aakarsh09z.communityappbackend.Payload.Request.MessageSeenPayload;
import com.aakarsh09z.communityappbackend.Payload.Response.ChatDto;
import com.aakarsh09z.communityappbackend.Repository.ChannelRepository;
import com.aakarsh09z.communityappbackend.Repository.ChatRepository;
import com.aakarsh09z.communityappbackend.Repository.UserRepository;
import com.aakarsh09z.communityappbackend.Security.JwtHelper;
import com.aakarsh09z.communityappbackend.Service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/channels")
public class ChannelController {
    private final ChannelService channelService;
    private final ChannelRepository channelRepository;
    private final ChatRepository chatRepository;
    private final JwtHelper jwtHelper;
    private final UserRepository userRepository;
    @PostMapping("/create")
    public ResponseEntity<?> createChannel(@RequestBody ChannelRequest request){
        return this.channelService.createChannel(request);
    }
    @GetMapping("/community/{communityId}")
    public ResponseEntity<?> getChannels(@PathVariable Long communityId){
        return this.channelService.getChannels(communityId);
    }
    @GetMapping("/chats/{channelId}")
    public ResponseEntity<?> getChatsByChannels(@PathVariable Long channelId){
        return this.channelService.getChats(channelId);
    }
    @MessageMapping("/chat.sendMessage/{channelId}")
    @SendTo("/topic/{channelId}")
    public ChatDto sendMessage(@Payload ChatDto chatDto, @DestinationVariable Long channelId) {
        System.out.println(channelId);
        User currentUser=userRepository.findByEmail(this.jwtHelper.getUsernameFromToken(chatDto.getToken())).orElseThrow(()-> new NoSuchElementException("User is not registered"));
        channelService.saveChatForChannel(chatDto,currentUser,channelId);
        chatDto.setSender(currentUser.getFullname());
        return chatDto;
    }
    @MessageMapping("/chat.newUser/{channelId}")
    @SendTo("/topic/{channelId}")
    public ChatDto newUser(@Payload ChatDto chatDto, @DestinationVariable Long channelId,
                        SimpMessageHeaderAccessor headerAccessor) {
            System.out.println(channelId);
            String token = chatDto.getToken();
            if (this.jwtHelper.isTokenExpired(token)) {
                headerAccessor.getSessionAttributes().put("errorMessage", "Token is expired");
            }
            User currentUser = userRepository.findByEmail(this.jwtHelper.getUsernameFromToken(token)).orElseThrow(() -> new NoSuchElementException("User is not registered"));
            chatDto.setSender(currentUser.getFullname());
            channelService.saveChatForChannel(chatDto, currentUser, channelId);
            headerAccessor.getSessionAttributes().put("username", chatDto.getSender());
            headerAccessor.getSessionAttributes().put("id", channelId);
        return chatDto;
    }
    @MessageMapping("/messageSeen")
    @SendTo("/topic/messageSeen")
    public List<User> handleMessageSeen(MessageSeenPayload payload) {
        User currentUser=userRepository.findByEmail(this.jwtHelper.getUsernameFromToken(payload.getToken())).orElseThrow(()-> new NoSuchElementException("User is not registered"));
        Chat chat=chatRepository.findById(payload.getChatId()).orElseThrow(()->new NoSuchElementException("Invalid Chat id: "+payload.getChatId()));
        chat.getSeenByUsers().add(currentUser);
        chatRepository.save(chat);
        return chat.getSeenByUsers();
    }
}
