package com.msb.stp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // map URL /reports/** đến thư mục "reports/" trong project root
        registry.addResourceHandler("/reports/**")
                .addResourceLocations("file:reports/");
    }
}
