package com.example.console.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValueConfiguration {
    @Value("${metrics.output:true}")
    public boolean outMetrics;
    @Value("${user-interaction.isUser:true}")
    public boolean isUser;
    @Value("${algorithm.error_threshold:0.3}")
    public double errorThreshold;
}
