package com.aakarsh09z.communityappbackend.Controller;

import com.aakarsh09z.communityappbackend.Payload.Response.ApiResponse;
import com.aakarsh09z.communityappbackend.Service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class StorageController {
    private final StorageService storageService;
    @PostMapping("/auth/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestParam String email,@RequestParam String userId, @RequestParam MultipartFile file) {
        if (!file.getContentType().equals("image/png") && !file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")) {
            return new ResponseEntity<>(new ApiResponse("File should be of type JPG/JPEG/PNG", false), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        return new ResponseEntity<>(new ApiResponse(this.storageService.uploadFile(email,userId,file),true),HttpStatus.OK);
    }
    @GetMapping("/auth/downloadImage/{filename}")
    public ResponseEntity<?> downloadImage(@PathVariable String filename){
        if (!storageService.fileExists(filename)) {
            return new ResponseEntity<>(new ApiResponse(filename + " does not exist",false), HttpStatus.OK);
        } else {
            byte[] data = storageService.downloadFile(filename);
            ByteArrayResource resource = new ByteArrayResource(data);
            return ResponseEntity.ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-disposition", "attachment; filename\"" + filename + "\"")
                    .body(resource);
        }
    }
    @DeleteMapping("/auth/deleteImage/{filename}")
    public ResponseEntity<?> deleteImage(@PathVariable String filename){
        return this.storageService.deleteFile(filename);
    }
}
