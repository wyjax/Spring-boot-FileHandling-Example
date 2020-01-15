package com.example.filehandle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class FilehandleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilehandleApplication.class, args);
    }
}