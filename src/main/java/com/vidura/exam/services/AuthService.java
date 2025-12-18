package com.vidura.exam.services;

import com.vidura.exam.config.security.JwtService;
import com.vidura.exam.dto.DTO;
import com.vidura.exam.dto.request.LoginRequest;
import com.vidura.exam.dto.response.LoginResponse;
import com.vidura.exam.dto.response.ServerResponse;
import com.vidura.exam.dto.response.ServerStatus;
import com.vidura.exam.entities.Role;
import com.vidura.exam.entities.User;
import com.vidura.exam.entities.VerificationCode;
import com.vidura.exam.myUtils.MyUtils;
import com.vidura.exam.repository.UserRepository;
import com.vidura.exam.repository.VerificationCodeRepository;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final VerificationCodeRepository verificationCodeRepository;


    public LoginResponse signup(@Valid LoginRequest loginRequest){
        LoginResponse response = new LoginResponse();
        if(userRepository.findByEmail(loginRequest.getEmail()).isPresent()){
            logger.warn("User with email "+loginRequest.getEmail()+" already exists");
            response.setMessage("User with email "+loginRequest.getEmail()+" already exists");
            response.setStatus(ServerStatus.ERROR);
            return response;
        }
        User user = new User();
        user.setEmail(loginRequest.getEmail());
        user.setPassword(passwordEncoder.encode(loginRequest.getPassword()));

        user.setRole(loginRequest.getRole()); // Default role
        user.setIsEmailVerified(false); // Requires verification flow
        user.setIsProfileCompleted(false);
        user.setIsProfileActive(true);
        user.setIsProfileBanned(false);

        userRepository.save(user);
        logger.info("user "+user.getEmail()+" successfully created");

        try {
            final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            final String jwt = jwtService.generateToken(userDetails);
            logger.info("jwt token created successfully");
            response.setStatus(ServerStatus.SUCCESS);
            response.setToken(jwt);
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setIsEmailVerified(false);
            response.setIsProfileCompleted(false);
        } catch (Exception e) {
            logger.error("error while creating jwt token {}", e.getMessage());
            response.setStatus(ServerStatus.ERROR);
            response.setMessage("user registration success but not created a token please try to login or contact admin");

        }

        return  response;
    }

    public LoginResponse signin(@Valid LoginRequest request){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            final String jwt = jwtService.generateToken(userDetails);
            logger.info("jwt token created successfully");

            Optional<User> user = userRepository.findByEmail(request.getEmail());
            if(user.isPresent()){
                logger.info("user "+user.get().getEmail()+" successfully getted");
                return  new LoginResponse(user.get().getEmail(),jwt,user.get().getRole(),user.get().getIsEmailVerified(),user.get().getIsProfileCompleted());
            }else{
                logger.warn("user "+request.getEmail()+" not found");
                throw  new RuntimeException("user not found");
            }

        }catch (AuthenticationException ex){
            ex.printStackTrace();
            logger.info("user login failed {}",ex.getMessage());
            throw  new RuntimeException("Invalid email or password");
        }

    }

    private Optional<User> getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email);
        return user;
    }


    public LoginResponse whoami() {
        //how to get current user
        LoginResponse response = new LoginResponse();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            response.setStatus(ServerStatus.ERROR);
            response.setMessage("user not logged in");
            return response;
        }

        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            response.setStatus(ServerStatus.ERROR);
            response.setMessage("user not found");
            return response ;
        }

        response.setEmail(email);
        response.setRole(user.get().getRole());
        response.setIsEmailVerified(user.get().getIsEmailVerified());
        response.setIsProfileCompleted(user.get().getIsProfileCompleted());
        response.setStatus(ServerStatus.SUCCESS);
        return  response;


    }

    public DTO sendVerificationCode() {
        Optional<User> user = getCurrentUser();
        DTO dto = new DTO();

        if(user.isEmpty()){
            dto.setStatus(ServerStatus.ERROR);
            dto.setMessage("user not found");
            return dto;
        }
        logger.warn(user.get().getIsEmailVerified() == true ? "VERIFIED":"NOTVERIFIED");
        if(user.get().getIsEmailVerified()){
            dto.setStatus(ServerStatus.ERROR);
            dto.setMessage("email already verified");
            return dto;
        }
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(2);

        List<VerificationCode> previousList = verificationCodeRepository
                .findByUserAndGenerateTimeAfter(user.get(), fiveMinutesAgo);
        if(previousList.isEmpty()){
            verificationCodeRepository.deleteAllByUser(user.get());
            VerificationCode code = new VerificationCode();
            code.setUser(user.get());
            code.setGenerateTime(LocalDateTime.now());
            code.setCode(MyUtils.generateCode());
            verificationCodeRepository.save(code);
            dto.setStatus(ServerStatus.SUCCESS);
            dto.setMessage("verification code created successfully, check the inbox");
        }else{
            dto.setStatus(ServerStatus.ERROR);
            dto.setMessage("verification code was sent! you can try again after 2 minutes");
        }


        return dto;
    }

    public DTO verifyEmail(String code) {

        Optional<User> user = getCurrentUser();

        DTO dto = new DTO();
        if(user.isEmpty()){
            dto.setStatus(ServerStatus.ERROR);
            dto.setMessage("user not found");
            return dto;
        }
        if(user.get().getIsEmailVerified()){
            dto.setStatus(ServerStatus.ERROR);
            dto.setMessage("email already verified");
            return dto;
        }
        if(code.isEmpty()){
            dto.setStatus(ServerStatus.ERROR);
            dto.setMessage("verification code not found");
            return dto;
        }

        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        Optional<VerificationCode> verificationCode = verificationCodeRepository.findByUserAndGenerateTimeAfterAndCode(user.get(),fiveMinutesAgo,code);
        if(verificationCode.isEmpty()){
            dto.setStatus(ServerStatus.ERROR);
            dto.setMessage("verification code is invalid or code is expired ");
            return dto;
        }else{
            user.get().setIsEmailVerified(true);
            userRepository.save(user.get());
            dto.setStatus(ServerStatus.SUCCESS);
            dto.setMessage("verification  success");
            return  dto;
        }

    }
}
