package com.aakarsh09z.communityappbackend.Repository;

import com.aakarsh09z.communityappbackend.Entity.Channel;
import com.aakarsh09z.communityappbackend.Entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findAllByChannel(Channel channel);
}

