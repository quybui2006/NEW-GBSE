package demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class DebugController {

    @GetMapping("/debug/ip")
    public Map<String, String> debugIp(HttpServletRequest request) {

        Map<String, String> result = new HashMap<>();

        result.put("remoteAddr", request.getRemoteAddr());
        result.put("xForwardedFor", request.getHeader("X-Forwarded-For"));
        result.put("xRealIp", request.getHeader("X-Real-IP"));

        return result;
    }
}