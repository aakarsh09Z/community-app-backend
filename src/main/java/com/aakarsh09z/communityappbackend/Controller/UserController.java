package com.aakarsh09z.communityappbackend.Controller;

import com.aakarsh09z.communityappbackend.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final PostService postService;
    @GetMapping("/likes/{id}")
    public ResponseEntity<?> getLikedPosts(@PathVariable Long id){
        return this.postService.getLikedPosts(id);
    }
    @GetMapping("/saved/{id}")
    public ResponseEntity<?> getSavedPosts(@PathVariable Long id){
        return this.postService.getSavedPosts(id);
    }
    @GetMapping("/comments/{id}")
    public ResponseEntity<?> getCommentsByUser(@PathVariable Long id){
        return this.postService.getCommentsByUser(id);
    }
}
