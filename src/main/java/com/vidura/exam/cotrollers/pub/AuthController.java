package com.vidura.exam.cotrollers.pub;
import com.vidura.exam.config.security.JwtService;
import com.vidura.exam.config.settings.ApplicationSettings;
import com.vidura.exam.dto.DTO;
import com.vidura.exam.dto.request.LoginRequest;
import com.vidura.exam.dto.response.LoginResponse;
import com.vidura.exam.dto.response.ServerResponse;
import com.vidura.exam.dto.response.ServerStatus;
import com.vidura.exam.entities.Role;
import com.vidura.exam.services.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/public/auth") // Public endpoint for login
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ApplicationSettings applicationSettings;

    Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try{
            logger.info("user login {}",request.getEmail());
            LoginResponse loginResponse = authService.signin(request);
            loginResponse.setStatus(ServerStatus.SUCCESS);
            return ResponseEntity.ok(loginResponse);
        }catch (Exception e){
            logger.warn(e.getMessage());
            e.printStackTrace();
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setMessage(e.getMessage());
            loginResponse.setStatus(ServerStatus.ERROR);
            return ResponseEntity.ok(loginResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody LoginRequest request) {

        try{
            if(request.getRole() == null){
                throw new Exception("Please Select a role");
            }
            if(request.getRole() == Role.ADMIN){
                if(!applicationSettings.canSignupAdmin()){
                    logger.warn("User try to create admin account but maximum limit is reached");
                    throw new Exception("Please Select a Valid role");
                }
            }
            logger.debug("/register endpoint called with Email = "+request.getEmail() + "Password = "+request.getPassword());
            return ResponseEntity.ok(authService.signup(request));
        }catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setStatus(ServerStatus.ERROR);
            loginResponse.setMessage(e.getMessage());
            return  ResponseEntity.ok(loginResponse);
        }
    }

    @GetMapping("/whoami")
    public ResponseEntity<LoginResponse> whoami(){
        try{
            LoginResponse loginResponse  = authService.whoami();
            return ResponseEntity.ok(loginResponse);
        }catch (Exception e){
            logger.warn(e.getMessage());
            e.printStackTrace();
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setStatus(ServerStatus.ERROR);
            loginResponse.setMessage(e.getMessage());
            return  ResponseEntity.ok(loginResponse);
        }
    }

    @GetMapping("/email-verification")
    public ResponseEntity<DTO> sendVerificationCode(){
        try {
            DTO response = authService.sendVerificationCode();
            return ResponseEntity.ok(response);

        }catch (Exception e){
            logger.warn(e.getMessage());
            e.printStackTrace();
            DTO response = new DTO();
            response.setStatus(ServerStatus.ERROR);
            response.setMessage(e.getMessage());
            return  ResponseEntity.ok(response);
        }
    }
    @PostMapping("/email-verification")
    public ResponseEntity<DTO> verifyCode(@RequestBody VcodeRequest code){
        logger.warn(code.code);
        try{
            DTO dto = authService.verifyEmail(code.code);
            return ResponseEntity.ok(dto);

        }catch (Exception e){
            logger.warn(e.getMessage());
            e.printStackTrace();
            DTO response = new DTO();
            response.setStatus(ServerStatus.ERROR);
            response.setMessage(e.getMessage());
            return  ResponseEntity.ok(response);

        }
    }

    record VcodeRequest(@NotNull @NotEmpty String code){}



}