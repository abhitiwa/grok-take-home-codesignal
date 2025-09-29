package com.xai.sdr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for the Grok-powered SDR system.
 * 
 * This system provides AI-powered sales development capabilities including:
 */
@SpringBootApplication
@EnableAsync
public class GrokSdrApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrokSdrApplication.class, args);
    }
}
