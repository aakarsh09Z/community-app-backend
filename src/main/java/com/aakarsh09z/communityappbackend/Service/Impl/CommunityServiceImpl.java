package com.aakarsh09z.communityappbackend.Service.Impl;

import com.aakarsh09z.communityappbackend.Configuration.AppConstants;
import com.aakarsh09z.communityappbackend.Entity.Community;
import com.aakarsh09z.communityappbackend.Entity.CommunityMember;
import com.aakarsh09z.communityappbackend.Entity.User;
import com.aakarsh09z.communityappbackend.Payload.Response.ApiResponse;
import com.aakarsh09z.communityappbackend.Repository.CommunityMemberRepository;
import com.aakarsh09z.communityappbackend.Repository.CommunityRepository;
import com.aakarsh09z.communityappbackend.Repository.UserRepository;
import com.aakarsh09z.communityappbackend.Service.CommunityService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {
    @Value("${application.bucket.name}")
    private String bucketName;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final AmazonS3 s3Client;
    private final CommunityMemberRepository communityMemberRepository;
    @Transactional
    public ResponseEntity<?> createCommunity(String name, String description, MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Long adminId= currentUser.getId();
        Community community = new Community();
        community.setName(name);
        community.setDescription(description);
        community.setAdminId(adminId);
        community.setMembersNumber(1);

        String imagePath=bucketName+"/resources/community/images";
        File fileObj = convertMultiPartFileToFile(file);
        String filename=name+getFileExtension(file.getOriginalFilename());
        s3Client.putObject(new PutObjectRequest(imagePath,filename,fileObj));
        fileObj.delete();
        String path= AppConstants.path+"community/images/"+filename;
        community.setIcon(path);
        communityRepository.save(community);

        CommunityMember communityMember = new CommunityMember();
        communityMember.setCommunity(community);
        communityMember.setUser(currentUser);
        communityMember.setRole("ADMIN");
        communityMemberRepository.save(communityMember);
        return new ResponseEntity<>(new ApiResponse(name+" Created",true), HttpStatus.CREATED);
    }
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllMembersInCommunity(Long communityId, Pageable pageable) {
        Page<User> members=communityRepository.findAllMembersById(communityId, pageable);
        return new ResponseEntity<>(members,HttpStatus.OK);
    }
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllCommunitiesWithPagination(Pageable pageable) {
        Page<Community> communities=communityRepository.findAll(pageable);
        return new ResponseEntity(communities,HttpStatus.OK);
    }

    public ResponseEntity<?> joinCommunity(Long communityId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if(communityRepository.findById(communityId).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Community not found: "+communityId,false),HttpStatus.BAD_REQUEST);
        }
        Community community=communityRepository.findById(communityId).orElseThrow(()->new NoSuchElementException("Community not found: "+communityId));
        List<CommunityMember> listOfMembers=communityMemberRepository.findAllByUser(currentUser);
        for(CommunityMember member: listOfMembers){
            if(member.getCommunity().equals(community)){
                return new ResponseEntity<>(new ApiResponse("User already in the community",false), HttpStatus.CONFLICT);
            }
        }
        community.getMembers().add(currentUser);
        community.setMembersNumber(community.getMembersNumber()+1);
        communityRepository.save(community);
        CommunityMember communityMember = new CommunityMember();
        communityMember.setCommunity(community);
        communityMember.setUser(currentUser);
        communityMember.setRole("MEMBER");
        communityMemberRepository.save(communityMember);
        return new ResponseEntity<>(new ApiResponse("Joined "+community.getName(),true),HttpStatus.OK);
    }
    //==================================================================================================================================================
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
}