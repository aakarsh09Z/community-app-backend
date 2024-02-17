package com.aakarsh09z.communityappbackend.Service.Impl;

import com.aakarsh09z.communityappbackend.Entity.Channel;
import com.aakarsh09z.communityappbackend.Entity.Chat;
import com.aakarsh09z.communityappbackend.Entity.Community;
import com.aakarsh09z.communityappbackend.Entity.User;
import com.aakarsh09z.communityappbackend.Payload.Request.ChannelRequest;
import com.aakarsh09z.communityappbackend.Payload.Response.ApiResponse;
import com.aakarsh09z.communityappbackend.Payload.Response.ChatDto;
import com.aakarsh09z.communityappbackend.Repository.ChannelRepository;
import com.aakarsh09z.communityappbackend.Repository.ChatRepository;
import com.aakarsh09z.communityappbackend.Repository.CommunityRepository;
import com.aakarsh09z.communityappbackend.Service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;
    private final CommunityRepository communityRepository;
    private final ChatRepository chatRepository;
    @Transactional
    public ResponseEntity<?> createChannel(ChannelRequest request){
        //add check for admin role later
        //check whether community is present
        if(communityRepository.findById(request.getCommunityId()).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Community does not exist",false), HttpStatus.CONFLICT);
        }
        Community community=communityRepository.findById(request.getCommunityId()).orElseThrow(()-> new NoSuchElementException("Community does not exist: "+request.getCommunityId()));
        //create new channel
        Channel channel=new Channel();
        channel.setName(request.getName().trim());
        channel.setType(request.getType());
        channel.setCommunity(community);
        channelRepository.save(channel);
        return new ResponseEntity<>(new ApiResponse(request.getName()+" channel created.",true),HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> getChannels(Long communityId){
        if(communityRepository.findById(communityId).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Community not found with id: "+communityId,false),HttpStatus.CONFLICT);
        }
        Community community=communityRepository.findById(communityId).orElseThrow();
        List<Channel> channels=channelRepository.findAllByCommunity(community);
        System.out.println(channels);
        return new ResponseEntity<>(channels,HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> getChats(Long channelId){
        if(channelRepository.findById(channelId).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Channel not found",false),HttpStatus.CONFLICT);
        }
        Channel channel=channelRepository.findById(channelId).orElseThrow(()->new NoSuchElementException("Channel not found with id: "+channelId));
        return new ResponseEntity<>(chatRepository.findAllByChannel(channel),HttpStatus.OK);
    }
    public void saveChatForChannel(ChatDto chatDto,User currentUser,Long channelId) {
        Chat chat=new Chat();
        chat.setUser(currentUser);
        chat.setType(chatDto.getType());
        chat.setContent(chatDto.getContent());
        chat.setSender(currentUser.getFullname());
        chat.setTime(LocalDateTime.now());
        chat.setChannel(channelRepository.findById(channelId).orElseThrow(()->new NoSuchElementException("Channel not found with id: "+channelId)));
        chatRepository.save(chat);
    }
}
