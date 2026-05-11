package com.awp.mgw.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebResourceConfig implements WebMvcConfigurer {

    private static final String UPLOAD_RESOURCE_PATTERN = "/mgw/uploads/**";
    private static final String UPLOAD_RESOURCE_LOCATION = "file:///C:/mgw/uploads/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(UPLOAD_RESOURCE_PATTERN)
            .addResourceLocations(UPLOAD_RESOURCE_LOCATION);
    }
}
