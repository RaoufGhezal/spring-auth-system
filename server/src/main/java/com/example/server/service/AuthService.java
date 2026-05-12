package com.example.server.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.server.dto.request.LoginRequest;
import com.example.server.dto.request.RegisterRequest;
import com.example.server.dto.response.AuthResponse;
import com.example.server.dto.response.UserResponse;
import com.example.server.entity.Session;
import com.example.server.entity.User;
import com.example.server.repository.SessionRepository;
import com.example.server.repository.UserRepository;
import com.example.server.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            SessionRepository sessionRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ){

        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

//    REGISTER

    public AuthResponse register(
            RegisterRequest request,
            HttpServletResponse response
    ){
        Optional<User> data = userRepository.findByEmail(request.getEmail());
        if(data.isPresent()){
            return new AuthResponse("Email already exists", null);
        }

        String hPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(hPassword);
        userRepository.save(user);

        Session session = new Session();
        session.setUser(user);
        sessionRepository.save(session);

        Map<String, String> tokens = jwtUtil.generateToken(user.getId(), session.getId());
        String aToken = tokens.get("accessToken");
        String rToken = tokens.get("refreshToken");

        //refresh token cookie
        Cookie rCookie =
                new Cookie("refreshToken", rToken);
        rCookie.setHttpOnly(true);
        rCookie.setSecure(false);
        rCookie.setPath("/api/auth/refresh-token");
        rCookie.setMaxAge(30 * 24 * 60 * 60); //30d

        //access token cookie
        Cookie aCookie =
                new Cookie("accessToken", aToken);
        aCookie.setHttpOnly(true);
        aCookie.setSecure(false);
        aCookie.setPath("/");
        aCookie.setMaxAge(15 * 60);//15min

        response.addCookie(aCookie);
        response.addCookie(rCookie);

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        return new AuthResponse("User registered successfully", userResponse);
    }

//    LOGIN

    public AuthResponse login(
            LoginRequest request,
            HttpServletResponse response
    ){
        Optional<User> data = userRepository.findByEmail(request.getEmail());
        if(data.isEmpty()){
            return new AuthResponse("Invalid email or password", null);
        }
//        User user = data.get();
        boolean isValid = passwordEncoder.matches(request.getPassword(), data.get().getPassword());
        if(!isValid){
            return new AuthResponse("Invalid email or password", null);
        }

        Session session = new Session();
        session.setUser(data.get());
        sessionRepository.save(session);

        Map<String, String> tokens = jwtUtil.generateToken(data.get().getId(), session.getId());
        String aToken = tokens.get("accessToken");
        String rToken = tokens.get("refreshToken");

        //refresh token cookie
        Cookie rCookie =
                new Cookie("refreshToken", rToken);
        rCookie.setHttpOnly(true);
        rCookie.setSecure(false);
        rCookie.setPath("/api/auth/refresh-token");
        rCookie.setMaxAge(30 * 24 * 60 * 60); //30d

        //access token cookie
        Cookie aCookie =
                new Cookie("accessToken", aToken);
        aCookie.setHttpOnly(true);
        aCookie.setSecure(false);
        aCookie.setPath("/");
        aCookie.setMaxAge(15 * 60);//15min

        response.addCookie(aCookie);
        response.addCookie(rCookie);

        UserResponse userResponse = new UserResponse(
                data.get().getId(),
                data.get().getName(),
                data.get().getEmail()
        );

        return new AuthResponse("User logged in successfully", userResponse);
    }

//    LOGOUT

    public String logout(
            HttpServletRequest request,
            HttpServletResponse response){
        Long sessionId = (Long) request.getAttribute("sessionId");
        if(sessionId !=null){
            sessionRepository.deleteById(sessionId);
        }

//        clear cookies
        Cookie aCookie = new Cookie("accessToken", "");
        aCookie.setPath("/");
        aCookie.setMaxAge(0);

        Cookie rCookie = new Cookie("refreshToken", "");
        rCookie.setPath("/api/auth/refresh-token");
        rCookie.setMaxAge(0);

        response.addCookie(aCookie);
        response.addCookie(rCookie);

        return "User logged out successfully";
    }

//    REFRESH TOKEN

    public String refreshToken(
        HttpServletRequest request,
        HttpServletResponse response
    ){
        Cookie refreshCookie = getCookie(request, "refreshToken");
        if (refreshCookie == null || refreshCookie.getValue() == null || refreshCookie.getValue().isBlank()) {
            return "Unauthorized";
        }

        try {
            var claims = jwtUtil.parseToken(refreshCookie.getValue());
            Number sessionIdClaim = claims.get("sessionId", Number.class);
            if (sessionIdClaim == null) {
                return "Unauthorized";
            }

            Long sessionId = sessionIdClaim.longValue();
            Optional<Session> ses = sessionRepository.findById(sessionId);
            if (ses.isEmpty()) {
                return "Session not found or expired ";
            }

            Session session = ses.get();
            Long userId = session.getUser().getId();

            Map<String, String> tokens = jwtUtil.generateToken(userId, sessionId);
            String aToken = tokens.get("accessToken");

            Cookie aCookie = new Cookie("accessToken", aToken);
            aCookie.setHttpOnly(true);
            aCookie.setSecure(false);
            aCookie.setPath("/");
            aCookie.setMaxAge(15 * 60); // 15min

            response.addCookie(aCookie);
            return "Token refreshed successfully";
        } catch (Exception e) {
            return "Unauthorized";
        }
    }

    private Cookie getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }


//    GET USER
    public Object getUser(
            HttpServletRequest request,
            HttpServletResponse response){
        Long userId =(Long) request.getAttribute("userId");
        if(userId== null) {
            return "Unauthorized";
        }


        Optional<User> data =
                userRepository.findById(userId);

        if (data.isEmpty()) {
            return "User not found";
        }

        return new UserResponse(
                data.get().getId(),
                data.get().getName(),
                data.get().getEmail()
        );

    }
}

