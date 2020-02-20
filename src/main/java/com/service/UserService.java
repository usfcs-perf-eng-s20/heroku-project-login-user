package com.service;

import com.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserServiceInterface {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean saveFiles(User user) {
        return false;
    }
}
