package com.aakarsh09z.communityappbackend.Service;

import com.aakarsh09z.communityappbackend.Entity.Channel;
import com.aakarsh09z.communityappbackend.Entity.User;
import com.aakarsh09z.communityappbackend.Payload.Request.ChannelRequest;
import com.aakarsh09z.communityappbackend.Payload.Response.ChatDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ChannelService {
    public ResponseEntity<?> createChannel(ChannelRequest request);
    public ResponseEntity<?> getChannels(Long communityId);
    public ResponseEntity<?> getChats(Long channelId);
    public void saveChatForChannel(ChatDto chatDto, User currentUser,Long channelId);
}
