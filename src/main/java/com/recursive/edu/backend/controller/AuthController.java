/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller;

import com.recursive.edu.backend.controller.request.AuthRefreshRequest;
import com.recursive.edu.backend.controller.request.LoginRequest;
import com.recursive.edu.backend.helper.ControllerHelper;
import com.recursive.edu.backend.service.AuthService;
import com.recursive.edu.backend.service.JwtService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final Validator loginRequestValidator, refreshRequestValidator;
    private final ControllerHelper controllerHelper;

    public AuthController(AuthService authService, JwtService jwtService,
                          @Qualifier("loginRequestValidator") Validator loginRequestValidator,
                          ControllerHelper controllerHelper,
                          Validator refreshRequestValidator) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.loginRequestValidator = loginRequestValidator;
        this.controllerHelper = controllerHelper;
        this.refreshRequestValidator = refreshRequestValidator;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody final LoginRequest loginRequest, BindingResult bindingResult) {
        return controllerHelper.validateAndExecute(loginRequestValidator, bindingResult,
                loginRequest, () -> authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody AuthRefreshRequest authRefreshRequest, BindingResult bindingResult) {
        return controllerHelper.validateAndExecute(refreshRequestValidator, bindingResult,
                authRefreshRequest, () -> authService.refresh(authRefreshRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        return controllerHelper.execute(() -> authService.getCurrentUser(authHeader));
    }
}
