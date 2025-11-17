/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.config;

import com.recursive.edu.backend.interceptors.UserUuidInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author PrantikGuha
 * CreatedAt: {12-11-2025}
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserUuidInterceptor userUuidInterceptor;

    public WebMvcConfig(UserUuidInterceptor userUuidInterceptor) {
        this.userUuidInterceptor = userUuidInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userUuidInterceptor)
                .addPathPatterns("/users/**");
    }
}

