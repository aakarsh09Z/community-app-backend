package com.aakarsh09z.communityappbackend.Repository;

import com.aakarsh09z.communityappbackend.Entity.Comment;
import com.aakarsh09z.communityappbackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByCommenter(User user);
}
