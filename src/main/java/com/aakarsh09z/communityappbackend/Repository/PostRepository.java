package com.aakarsh09z.communityappbackend.Repository;

import com.aakarsh09z.communityappbackend.Entity.Community;
import com.aakarsh09z.communityappbackend.Entity.Post;
import com.aakarsh09z.communityappbackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByOwner(User owner);
    List<Post> findAllByCommunity(Community Community);
}
