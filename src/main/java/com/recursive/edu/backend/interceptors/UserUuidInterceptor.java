/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.interceptors;

import com.recursive.edu.backend.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

/**
 * @author PrantikGuha
 * CreatedAt: {12-11-2025}
 */
@Component
public class UserUuidInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod method)) return true;

        Map<String, String> pathVars =
                (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVars == null) return true;

        String userUuidFromPath = pathVars.get("uuid");
        if (userUuidFromPath == null) return true;

        String userUUID = SecurityUtils.getCurrentUserUuid();

        if (!userUuidFromPath.equals(userUUID)) {
            throw new AccessDeniedException("User mismatch between token and path");
        }

        return true;
    }
}
