package com.xai.sdr.service;

import com.xai.sdr.config.GrokApiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GrokApiService.
 * 
 * Tests the service layer logic without making actual API calls.
 */
@ExtendWith(MockitoExtension.class)
class GrokApiServiceTest {
    
    @Mock
    private GrokApiConfig grokConfig;
    
    private GrokApiService grokApiService;
    
    @BeforeEach
    void setUp() {
        when(grokConfig.getBaseUrl()).thenReturn("https://api.x.ai/v1");
        when(grokConfig.getModel()).thenReturn("grok-4");
        when(grokConfig.getTemperature()).thenReturn(0.7);
        when(grokConfig.getMaxTokens()).thenReturn(1000);
        when(grokConfig.getTimeout()).thenReturn(30000);
        
        grokApiService = new GrokApiService(grokConfig);
    }
    
    @Test
    void testServiceInitialization() {
        assertNotNull(grokApiService);
    }
    
    @Test
    void testConfigurationInjection() {
        // Test that config values are properly injected
        assertDoesNotThrow(() -> {
            new GrokApiService(grokConfig);
        });
    }
}
