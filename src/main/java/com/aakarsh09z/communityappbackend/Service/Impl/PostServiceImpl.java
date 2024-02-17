package com.aakarsh09z.communityappbackend.Service.Impl;

import com.aakarsh09z.communityappbackend.Configuration.AppConstants;
import com.aakarsh09z.communityappbackend.Entity.Comment;
import com.aakarsh09z.communityappbackend.Entity.Community;
import com.aakarsh09z.communityappbackend.Entity.Post;
import com.aakarsh09z.communityappbackend.Entity.User;
import com.aakarsh09z.communityappbackend.Payload.Request.CommentRequest;
import com.aakarsh09z.communityappbackend.Payload.Response.ApiResponse;
import com.aakarsh09z.communityappbackend.Repository.CommentRepository;
import com.aakarsh09z.communityappbackend.Repository.CommunityRepository;
import com.aakarsh09z.communityappbackend.Repository.PostRepository;
import com.aakarsh09z.communityappbackend.Repository.UserRepository;
import com.aakarsh09z.communityappbackend.Service.PostService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.xray.model.Http;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    @Value("${application.bucket.name}")
    private String bucketName;
    private final AmazonS3 s3Client;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RestTemplate restTemplate;
    @Transactional
    public ResponseEntity<?> createPost(String caption, Long communityId, MultipartFile file){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if(communityRepository.findById(communityId).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Community not found with communityId: "+communityId,false),HttpStatus.BAD_REQUEST);
        }
        String content=uploadPostContent(file);
        Post post=new Post();
        post.setContent(content.trim());
        post.setCaption(caption.trim());
        post.setOwner(currentUser);
        Community community=communityRepository.findById(communityId).orElseThrow(()->new NoSuchElementException("Community id not found: "+communityId));
        post.setCommunity(community);
        post.setTimestamp(LocalDateTime.now());
        postRepository.save(post);
        return new ResponseEntity<>(new ApiResponse("Post created",true), HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> deletePost(Long postId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if(postRepository.findById(postId).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Post not found with postID: "+postId,false),HttpStatus.BAD_REQUEST);
        }
        Post post=postRepository.findById(postId).orElseThrow(()-> new NoSuchElementException("Invalid postId: "+postId));
        System.out.println(extractPathAfterResources(post.getContent()));
        if(!currentUser.equals(post.getOwner())){
            return new ResponseEntity<>(new ApiResponse("This user is not the owner of post",false),HttpStatus.CONFLICT);
        }
        //Delete from s3 bucket
        try {
            deleteS3Object(bucketName,extractPathAfterResources(post.getContent()));
        } catch (AmazonS3Exception e) {
            return new ResponseEntity<>(new ApiResponse("Failed to delete file from S3: " + e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        postRepository.delete(post);
        return new ResponseEntity<>(new ApiResponse("Post deleted",true),HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> getPost(Long postId){
        if(postRepository.findById(postId).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Post not found with postID: "+postId,false),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(postRepository.findById(postId).orElseThrow(()-> new NoSuchElementException("Invalid post id: "+postId)),HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> getAllPostsByUser(Long userId){
        if(userRepository.findById(userId).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("User not found with userID: "+userId,false),HttpStatus.BAD_REQUEST);
        }
        User user=userRepository.findById(userId).orElseThrow(()->new NoSuchElementException("Invalid user id: "+userId));
        return new ResponseEntity<>(postRepository.findAllByOwner(user),HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> getAllPostsByCommunity(Long communityId){
        if(communityRepository.findById(communityId).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Community not found with communtityID: "+communityId,false),HttpStatus.BAD_REQUEST);
        }
        Community community=communityRepository.findById(communityId).orElseThrow(()-> new NoSuchElementException("Invalid community id: "+communityId));
        return new ResponseEntity<>(postRepository.findAllByCommunity(community),HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> likePost(Long postId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if(postRepository.findById(postId).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Post not found with postID: "+postId,false),HttpStatus.BAD_REQUEST);
        }
        Post post=postRepository.findById(postId).orElseThrow(()-> new NoSuchElementException("Invalid post id : "+postId));
        List<User> likedByUsers=post.getLikes();
        boolean found=false;
        for(User user: likedByUsers){
            if (user.equals(currentUser)) {
                found = true;
                break;
            }
        }
        if(found){
            likedByUsers.remove(currentUser);
            postRepository.save(post);
            return new ResponseEntity<>(new ApiResponse("Like removed",true), HttpStatus.OK);
        }
        likedByUsers.add(currentUser);
        postRepository.save(post);
        return new ResponseEntity<>(new ApiResponse("Liked post",true),HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> commentOnPost(CommentRequest commentRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if(postRepository.findById(commentRequest.getPostId()).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Post not found with postID: "+commentRequest.getPostId(),false),HttpStatus.BAD_REQUEST);
        }
        Post post=postRepository.findById(commentRequest.getPostId()).orElseThrow(()-> new NoSuchElementException("Invalid post id : "+commentRequest.getPostId()));
        Comment comment=new Comment();
        comment.setContent(commentRequest.getContent().trim());
        comment.setPost(post);
        comment.setCommenter(currentUser);
        comment.setTimestamp(LocalDateTime.now());
        commentRepository.save(comment);
        return new ResponseEntity<>(new ApiResponse("Commented: "+comment.getContent(),true),HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> savePost(Long postId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if(postRepository.findById(postId).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Post not found with postID: "+postId,false),HttpStatus.BAD_REQUEST);
        }
        Post post=postRepository.findById(postId).orElseThrow(()-> new NoSuchElementException("Invalid post id : "+postId));
        List<User> saves=post.getSavedByUsers();
        boolean found=false;
        for(User user: saves){
            if (user.equals(currentUser)) {
                found = true;
                break;
            }
        }
        if(found){
            saves.remove(currentUser);
            postRepository.save(post);
            return new ResponseEntity<>(new ApiResponse("Removed from saved",true), HttpStatus.OK);
        }
        saves.add(currentUser);
        postRepository.save(post);
        return new ResponseEntity<>(new ApiResponse("Saved Post",true),HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> getLikesAmount(Long postId){
        if(postRepository.findById(postId).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Post not found with postID: "+postId,false),HttpStatus.BAD_REQUEST);
        }
        Post post=postRepository.findById(postId).orElseThrow(()-> new NoSuchElementException("Invalid post id : "+postId));
        List<User> likedByUsers=post.getLikes();
        return new ResponseEntity<>(new ApiResponse(Integer.toString(likedByUsers.size()),true),HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> getCommentsAmount(Long postId){
        if(postRepository.findById(postId).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Post not found with postID: "+postId,false),HttpStatus.BAD_REQUEST);
        }
        Post post=postRepository.findById(postId).orElseThrow(()-> new NoSuchElementException("Invalid post id : "+postId));
        List<Comment> comments=post.getComments();
        return new ResponseEntity<>(new ApiResponse(Integer.toString(comments.size()),true),HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> getLikedPosts(Long id){
        if(userRepository.findById(id).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("User not found with id: "+id,false),HttpStatus.BAD_REQUEST);
        }
        User user=userRepository.findById(id).orElseThrow(()->new NoSuchElementException("Invalid user id: "+id));
        return new ResponseEntity<>(user.getLikedPosts(),HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> getSavedPosts(Long id){
        if(userRepository.findById(id).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("User not found with id: "+id,false),HttpStatus.BAD_REQUEST);
        }
        User user=userRepository.findById(id).orElseThrow(()->new NoSuchElementException("Invalid user id: "+id));
        return new ResponseEntity<>(user.getSavedPosts(),HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> getCommentsByUser(Long id){
        if(userRepository.findById(id).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("User not found with id: "+id,false),HttpStatus.BAD_REQUEST);
        }
        User user=userRepository.findById(id).orElseThrow(()->new NoSuchElementException("Invalid user id: "+id));
        return new ResponseEntity<>(commentRepository.findAllByCommenter(user),HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> deleteComment(Long commentId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if(commentRepository.findById(commentId).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Comment not found with commentId: "+commentId,false),HttpStatus.BAD_REQUEST);
        }
        Comment comment=commentRepository.findById(commentId).orElseThrow(()-> new NoSuchElementException("Invalid Comment id: "+commentId));
        Post post=comment.getPost();
        if(!currentUser.equals(post.getOwner()) || !currentUser.equals(comment.getCommenter())){
            return new ResponseEntity<>(new ApiResponse("Only the post owner or the commenter can delete a comment",false),HttpStatus.CONFLICT);
        }
        commentRepository.delete(comment);
        return new ResponseEntity<>(new ApiResponse("Comment deleted",true),HttpStatus.OK);
    }

//================================================================================================================================================================================================================================
    public String uploadPostContent(MultipartFile file){
        String filePath=bucketName+"/resources/posts";
        File fileObj = convertMultiPartFileToFile(file);
        String filename= UUID.randomUUID()+getFileExtension(file.getOriginalFilename());
        s3Client.putObject(new PutObjectRequest(filePath,filename,fileObj));
        fileObj.delete();
        String path= AppConstants.path+"posts/"+filename;
        return path;
    }
    private File convertMultiPartFileToFile(MultipartFile file){
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try(FileOutputStream fos = new FileOutputStream((convertedFile))){
            fos.write(file.getBytes());
        }
        catch (IOException e){
            System.out.println("Error converting multipartFile to file");
        }
        return convertedFile;
    }
    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }
    private void deleteObjectsInFolder(String bucketName, String folderPath) {
        for (S3ObjectSummary file : s3Client.listObjects(bucketName, folderPath).getObjectSummaries()){
            s3Client.deleteObject(bucketName, file.getKey());
        }
    }
    public static String extractPathAfterResources(String s3ObjectUrl) {
        int index = s3ObjectUrl.indexOf("/resources/");
        if (index != -1) {
            return "resources/"+s3ObjectUrl.substring(index + "/resources/".length());
        }
        return null;
    }
    private void deleteS3Object(String bucketName, String key) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        String url = "https://" + bucketName + ".s3.amazonaws.com/" + key;
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Object deleted successfully");
        } else {
            System.out.println("Failed to delete object: " + response.getStatusCode());
        }
    }
}
