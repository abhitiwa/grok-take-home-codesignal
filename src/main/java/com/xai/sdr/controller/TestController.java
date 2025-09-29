package com.xai.sdr.controller;

import com.xai.sdr.service.GrokApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private GrokApiService grokApiService;
    
    @GetMapping("/grok-connection")
    public String testGrokConnection() {
        try {
            boolean connected = grokApiService.testConnection();
            return connected ? "Grok API: Connected ✅" : "Grok API: Connection Failed ❌";
        } catch (Exception e) {
            return "Grok API Error: " + e.getMessage();
        }
    }
    
    @PostMapping("/grok-message")
    public String testGrokMessage(@RequestBody String message) {
        try {
            return "Grok Response: " + grokApiService.sendChatCompletion(message);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}