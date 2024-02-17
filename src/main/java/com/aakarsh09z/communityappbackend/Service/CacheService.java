//package com.aakarsh09z.communityappbackend.Service;
//
//import lombok.AllArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import org.springframework.cache.Cache;
//import org.springframework.cache.CacheManager;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class CacheService {
//    private static final String CACHE_NAME = "yourCacheName";
//    private final CacheManager cacheManager;
//    public boolean isDataInCache(String key) {
//        Cache cache = cacheManager.getCache(CACHE_NAME);
//
//        if (cache != null) {
//            Cache.ValueWrapper valueWrapper = cache.get(key);
//            return valueWrapper != null && valueWrapper.get() != null;
//        }
//
//        return false;
//    }
//}
