package com.HTT.backend.security;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.HTT.backend.entities.User;
import com.HTT.backend.services.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private UserService userService;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;

    public OAuthAuthenticationSuccessHandler(JwtUtil jwtUtil,@Lazy PasswordEncoder passwwEncoder, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordEncoder = passwwEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        try {
            DefaultOAuth2User oAuthUser = (DefaultOAuth2User) authentication.getPrincipal();
            String name = oAuthUser.getName();
            String email = oAuthUser.getAttribute("email").toString();
            System.out.println(name + "  " + email);

            User user;
            try {
                user = (User) userService.loadUserByUsername(email);
            } catch (UsernameNotFoundException e) {
                user = null;
            }
            if (user == null) {
                System.out.println("new user");
                String encodedPassword = passwordEncoder.encode("gw(8ehnbeiub*(*7766hspoiaw)(^6sa5&s*%78iofgwskl23gs");
                user = new User(name, email, encodedPassword, email.equals("achirjain11@gmail.com"));
                user = userService.saveUser(user);
                System.out.println("saved user: " + user);
            }
            String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getAuthorities(), 15L);
            System.out.println("token: " + jwtToken);
            boolean isAdmin = user.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:8082/loginsuccess")
                    .queryParam("token", jwtToken)
                    .queryParam("isAdmin", isAdmin)
                    .build().toUriString();
            System.out.println("redirect to " + redirectUrl);

            new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
            // --- END OF EXISTING LOGIC ---

        } catch (Exception e) {
            // THIS WILL CATCH ANY UNEXPECTED ERROR
            System.err.println("!!!!!!!!!! CRITICAL OAUTH FAILURE !!!!!!!!!!");
            e.printStackTrace(); // This prints the full error stack trace to the log

            // Redirect to a generic error page so the user isn't stuck
            String errorUrl = UriComponentsBuilder.fromUriString("http://localhost:8082/loginfailed")
                    .queryParam("error", "LoginProcessingFailed")
                    .build().toUriString();
            new DefaultRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }

}
