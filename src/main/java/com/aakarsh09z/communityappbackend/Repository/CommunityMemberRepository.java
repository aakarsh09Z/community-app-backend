package com.aakarsh09z.communityappbackend.Repository;

import com.aakarsh09z.communityappbackend.Entity.CommunityMember;
import com.aakarsh09z.communityappbackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember,Long> {
    List<CommunityMember> findAllByUser(User user);
}
