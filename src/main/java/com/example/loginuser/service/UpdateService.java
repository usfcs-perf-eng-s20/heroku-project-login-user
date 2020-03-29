package com.example.loginuser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;

@Component
public class UpdateService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public int loggedIn(boolean isLoggedIn, Date lastLoggin, int userID){
        return userRepository.loggedI(isLoggedIn, lastLoggin, userID);
    }
}
