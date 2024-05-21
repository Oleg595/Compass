package com.example.console.configuration;

import org.example.DataContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
@ComponentScan("com.example.algorithm")
public class ContextConfiguration {
    @Bean
    public DataContext dataContext() {
        return new DataContext(
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
    }
}
