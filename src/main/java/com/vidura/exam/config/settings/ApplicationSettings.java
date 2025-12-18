package com.vidura.exam.config.settings;

import com.vidura.exam.entities.Role;
import com.vidura.exam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationSettings {
    private  final UserRepository userRepository;

    private static int MAXIMUM_ADMIN_COUNT = 1;
    public   boolean canSignupAdmin(){
        int count = userRepository.countUsersByRole(Role.ADMIN);
        if(count < MAXIMUM_ADMIN_COUNT){
            return  true;
        }else{
            return false;
        }
    }
}
