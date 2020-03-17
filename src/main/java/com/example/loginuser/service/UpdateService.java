package com.example.loginuser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
@Component
public class UpdateService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public int loggedIn(boolean isLoggedIn, int userID){
        return userRepository.loggedI(isLoggedIn, userID);
    }
}
