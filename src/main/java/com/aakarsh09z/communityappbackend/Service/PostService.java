package com.aakarsh09z.communityappbackend.Service;

import com.aakarsh09z.communityappbackend.Payload.Request.CommentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    public ResponseEntity<?> createPost(String caption, Long communityId, MultipartFile file);
    public ResponseEntity<?> deletePost(Long postId);
    public ResponseEntity<?> getPost(Long postId);
    public ResponseEntity<?> getAllPostsByUser(Long userId);
    public ResponseEntity<?> getAllPostsByCommunity(Long communityId);
    public ResponseEntity<?> likePost(Long postId);
    public ResponseEntity<?> commentOnPost(CommentRequest commentRequest);
    public ResponseEntity<?> savePost(Long postId);
    public ResponseEntity<?> getLikesAmount(Long postId);
    public ResponseEntity<?> getCommentsAmount(Long postId);
    public ResponseEntity<?> getLikedPosts(Long id);
    public ResponseEntity<?> getSavedPosts(Long id);
    public ResponseEntity<?> getCommentsByUser(Long id);
    public ResponseEntity<?> deleteComment(Long commentId);
}
