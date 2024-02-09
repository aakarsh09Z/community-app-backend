package com.aakarsh09z.communityappbackend.Controller;

import com.aakarsh09z.communityappbackend.Service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/communities")
public class CommunityController {
    private final CommunityService communityService;

    @PostMapping("/create")
    public ResponseEntity<?> createCommunity(@RequestParam String name,
                                             @RequestParam String description,
                                             @RequestParam MultipartFile file) {
        return this.communityService.createCommunity(name, description, file);
    }

    @GetMapping("/{communityId}/members")
    public ResponseEntity<?> getAllMembersInCommunity(@PathVariable Long communityId,
                                                      @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return this.communityService.getAllMembersInCommunity(communityId, pageable);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCommunitiesWithPagination(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return this.communityService.getAllCommunitiesWithPagination(pageable);
    }

    @PostMapping("/join/{communityId}")
    public ResponseEntity<?> joinCommunity(@PathVariable Long communityId) {
        return this.communityService.joinCommunity(communityId);
    }
}
