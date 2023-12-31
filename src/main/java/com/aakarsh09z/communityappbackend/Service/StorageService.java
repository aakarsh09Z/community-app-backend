package com.aakarsh09z.communityappbackend.Service;

import com.aakarsh09z.communityappbackend.Entity.User;
import com.aakarsh09z.communityappbackend.Payload.Response.ApiResponse;
import com.aakarsh09z.communityappbackend.Repository.UserRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StorageService {
    @Value("${application.bucket.name}")
    private String bucketName;
    private final AmazonS3 s3Client;
    private final UserRepository userRepository;
    //upload file
    public String uploadFile(String email,MultipartFile file){
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException(("user not found in database")+email));
        File fileObj = convertMultiPartFileToFile(file);
        String filename=user.getUserId()+getFileExtension(file.getOriginalFilename());
        s3Client.putObject(new PutObjectRequest(bucketName,filename,fileObj));
        fileObj.delete();
        user.setProfileImage(filename);
        userRepository.save(user);
        return "File uploaded: "+filename;
    }
    //download file
    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
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
        if (fileExists(filename)) {
            s3Client.deleteObject(bucketName, filename);
            return new ResponseEntity<>(new ApiResponse(filename + " has been deleted",true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse(filename + " does not exist",false),HttpStatus.CONFLICT);
        }
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
