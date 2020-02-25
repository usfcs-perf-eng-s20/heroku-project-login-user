package com.example.loginuser.service;

import com.example.loginuser.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserServiceInterface{
    //mention methods u want to write and implement here
    //write code here

    @Autowired
    UserRepository userRepository;

    //this is dummy and not working
    @Override
    public boolean saveUsers(Users user) {
//        userRepository.save(new Users("dharti"));
        if(userRepository.count() > 1)
            return true;
        return false;
    }


}
