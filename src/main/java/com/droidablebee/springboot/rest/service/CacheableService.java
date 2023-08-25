package com.droidablebee.springboot.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class CacheableService {

    @Autowired
    private CacheableServiceClient cacheableServiceClient;

    @Cacheable("CacheableClient")
    public Page<Result> retrieve(Pageable pageable) {

        return cacheableServiceClient.retrieve(pageable);
    }

    interface Result {
    }

    @Component
    public static class CacheableServiceClient {

        public Page<Result> retrieve(Pageable pageable) {
            return Page.empty(pageable);
        }
    }
}
