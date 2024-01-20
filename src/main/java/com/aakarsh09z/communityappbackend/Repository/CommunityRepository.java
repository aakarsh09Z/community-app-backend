package com.aakarsh09z.communityappbackend.Repository;

import com.aakarsh09z.communityappbackend.Entity.Community;
import com.aakarsh09z.communityappbackend.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community,Long> {
    @Query("SELECT cm.user FROM CommunityMember cm WHERE cm.community.id = :communityId")
    Page<User> findAllMembersById(Long communityId, Pageable pageable);

    Page<Community> findAll(Pageable pageable);
}