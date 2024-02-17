package com.aakarsh09z.communityappbackend.Service;

import com.aakarsh09z.communityappbackend.Configuration.AppConstants;
import com.aakarsh09z.communityappbackend.Entity.OtpEntity;
import com.aakarsh09z.communityappbackend.Entity.User;
import com.aakarsh09z.communityappbackend.Payload.Response.ApiResponse;
import com.aakarsh09z.communityappbackend.Repository.OtpRepository;
import com.aakarsh09z.communityappbackend.Repository.UserRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StorageService {
    @Value("${application.bucket.name}")
    private String bucketName;
    private final AmazonS3 s3Client;
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    //upload file
    public ResponseEntity<?> uploadFile(String email,String userId,MultipartFile file){
        if(userRepository.findByEmail(email).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Email not registered",false),HttpStatus.CONFLICT);
        }
        String imagePath=bucketName+"/resources/images";
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException(("user not found in database")+email));
        File fileObj = convertMultiPartFileToFile(file);
        String filename=userId+getFileExtension(file.getOriginalFilename());
        s3Client.putObject(new PutObjectRequest(imagePath,filename,fileObj));
        fileObj.delete();
        String path= AppConstants.path+"images/"+filename;
        user.setUserId(userId);
        user.setProfileImageUrl(path);
        userRepository.save(user);

        String OTP= GenerateOtp.generateOtp();
        LocalDateTime expirationTime=LocalDateTime.now().plusMinutes(AppConstants.OTP_EXPIRATION_MINUTE);


        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setOtp(OTP);
        otpEntity.setEmail(email);
        otpEntity.setExpirationTime(expirationTime);
        otpRepository.save(otpEntity);
        emailService.sendOtpEmail(email,OTP);

        return new ResponseEntity<>(new ApiResponse("Check your email for OTP",true),HttpStatus.OK);
    }
    //download file
    public byte[] downloadFile(String fileName) {
        String imagePath=bucketName+"/resources/images";
        S3Object s3Object = s3Client.getObject(imagePath, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        byte[] content;
        try {
            content = IOUtils.toByteArray(inputStream);
            System.out.println(content.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content;
    }
    //delete file from bucket
    public ResponseEntity<?> deleteFile(String filename){
        String imagePath=bucketName+"/resources/images";
        if (fileExists(filename)) {
            s3Client.deleteObject(imagePath, filename);
            return new ResponseEntity<>(new ApiResponse(filename + " has been deleted",true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse(filename + " does not exist",false),HttpStatus.CONFLICT);
        }
    }
    public ResponseEntity<?> getAvatars(){
//        String avatarPath=bucketName+"/resources/avatars/";
//        ListObjectsV2Request request = new ListObjectsV2Request()
//                .withBucketName(avatarPath);
//        ListObjectsV2Result response = s3Client.listObjectsV2(request);
//        List<String> imageNames = new ArrayList<>();
//        for (S3ObjectSummary objectSummary : response.getObjectSummaries()) {
//            if (objectSummary.getKey()!=null) {
//                String imageName = objectSummary.getKey().substring(avatarPath.length());
//                imageNames.add(imageName);
//            }
//        }
        List<String> imageNames=new ArrayList<>();
        imageNames.add("https://connectifystorage.s3.ap-south-1.amazonaws.com/resources/avatars/None.png");
        imageNames.add("https://connectifystorage.s3.ap-south-1.amazonaws.com/resources/avatars/avatar+1.png");
        imageNames.add("https://connectifystorage.s3.ap-south-1.amazonaws.com/resources/avatars/avatar+2.png");
        imageNames.add("https://connectifystorage.s3.ap-south-1.amazonaws.com/resources/avatars/avatar+3.png");
        imageNames.add("https://connectifystorage.s3.ap-south-1.amazonaws.com/resources/avatars/avatar+4.png");
        imageNames.add("https://connectifystorage.s3.ap-south-1.amazonaws.com/resources/avatars/avatar+5.png");
        imageNames.add("https://connectifystorage.s3.ap-south-1.amazonaws.com/resources/avatars/avatar+6.png");
        return new ResponseEntity<>(imageNames,HttpStatus.OK);
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
    public boolean fileExists(String filename) {
        try {
            ObjectMetadata metadata = s3Client.getObjectMetadata(bucketName, filename);
            return metadata != null;
        } catch (Exception e) {
            return false;
        }
    }
    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}
