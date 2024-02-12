package com.example.console.configuration;

import com.example.algorithm.context.DataContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.example.algorithm")
public class ContextConfiguration {
    @Bean
    public DataContext dataContext() {
        return new DataContext();
    }
}
