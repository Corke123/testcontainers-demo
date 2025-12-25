package com.github.corke123.limiterservice.limiter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/limits")
public class LimiterController {

    private final LimiterService limiterService;

    public LimiterController(LimiterService limiterService) {
        this.limiterService = limiterService;
    }

    @PostMapping("/{ipAddress}")
    ResponseEntity<Void> checkLimit(@PathVariable String ipAddress) {
        boolean isAllowed = limiterService.checkAndIncrement(ipAddress);
        if (isAllowed) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }
}
