package com.aakarsh09z.communityappbackend.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface CommunityService {
    public ResponseEntity<?> createCommunity(String name, String description, MultipartFile file);
    public ResponseEntity<?> getAllMembersInCommunity(Long communityId, Pageable pageable);
    public ResponseEntity<?> getAllCommunitiesWithPagination(Pageable pageable);
    public ResponseEntity<?> joinCommunity(Long communityId);

}
