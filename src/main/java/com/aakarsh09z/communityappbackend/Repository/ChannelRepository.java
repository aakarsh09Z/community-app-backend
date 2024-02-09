package com.aakarsh09z.communityappbackend.Repository;

import com.aakarsh09z.communityappbackend.Entity.Channel;
import com.aakarsh09z.communityappbackend.Entity.Community;
import org.springframework.beans.factory.ListableBeanFactoryExtensionsKt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel,Long> {
    Optional<Channel> findById(Long id);
    List<Channel> findAllByCommunity(Community community);
}
