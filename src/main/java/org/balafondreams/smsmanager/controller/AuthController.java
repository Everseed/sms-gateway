package org.balafondreams.smsmanager.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.models.user.JwtResponseDTO;
import org.balafondreams.smsmanager.domain.models.user.LoginDTO;
import org.balafondreams.smsmanager.domain.models.user.UserDTO;
import org.balafondreams.smsmanager.domain.models.user.UserRegistrationDTO;
import org.balafondreams.smsmanager.repository.TokenRepository;
import org.balafondreams.smsmanager.security.CurrentUser;
import org.balafondreams.smsmanager.security.UserPrincipal;
import org.balafondreams.smsmanager.service.security.LogoutService;
import org.balafondreams.smsmanager.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final LogoutService logoutService;


    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        return ResponseEntity.ok(userService.registerUser(registrationDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        String jwt = userService.authenticateAndGetToken(
                loginDTO.getUsername(),
                loginDTO.getPassword()
        );

        return ResponseEntity.ok(new JwtResponseDTO(jwt));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            @CurrentUser UserPrincipal currentUser) {
        logoutService.logout(request, response, null);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutFromAllDevices(@CurrentUser UserPrincipal currentUser) {
        tokenRepository.revokeAllUserTokens(currentUser.getId());
        return ResponseEntity.ok().build();
    }
}