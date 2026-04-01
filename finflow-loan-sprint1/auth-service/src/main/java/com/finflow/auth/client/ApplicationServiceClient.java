package com.finflow.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "application-service")
public interface ApplicationServiceClient {

    @GetMapping("/applications/products")
    List<Map<String, Object>> getProducts();
}
