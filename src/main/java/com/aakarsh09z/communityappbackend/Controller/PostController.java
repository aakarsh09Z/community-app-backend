package com.aakarsh09z.communityappbackend.Controller;

import com.aakarsh09z.communityappbackend.Payload.Request.CommentRequest;
import com.aakarsh09z.communityappbackend.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/post")
public class PostController {
    private final PostService postService;

    @PostMapping("create")
    public ResponseEntity<?> createPost(String caption, Long communityId, MultipartFile file){
        return this.postService.createPost(caption,communityId,file);
    }
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId){
        return this.postService.deletePost(postId);
    }
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId){
        return this.postService.getPost(postId);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllPostsByUser(@PathVariable Long userId){
        return this.postService.getAllPostsByUser(userId);
    }
    @GetMapping("/community/{communityId}")
    public ResponseEntity<?> getAllPostsByCommunity(@PathVariable Long communityId){
        return this.postService.getAllPostsByCommunity(communityId);
    }
    @PostMapping("/like/{postId}")
    public ResponseEntity<?> likePost(@PathVariable Long postId){
        return this.postService.likePost(postId);
    }
    @PostMapping("/comment")
    public ResponseEntity<?> commentOnPost(@RequestBody CommentRequest commentRequest){
        return this.postService.commentOnPost(commentRequest);
    }
    @PostMapping("/save/{postId}")
    public ResponseEntity<?> savePost(@PathVariable Long postId){
        return this.postService.savePost(postId);
    }
    @GetMapping("/likesAmount/{postId}")
    public ResponseEntity<?> getLikesAmount(@PathVariable Long postId){
        return this.postService.getLikesAmount(postId);
    }
    @GetMapping("/commentsAmount/{postId}")
    public ResponseEntity<?> getCommentsAmount(@PathVariable Long postId){
        return this.postService.getCommentsAmount(postId);
    }
    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId){
        return this.postService.deleteComment(commentId);
    }
}
