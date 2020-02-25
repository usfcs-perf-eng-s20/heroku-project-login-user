package com.example.loginuser.service;
import com.example.loginuser.model.User;
import com.example.loginuser.model.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<Users, Integer> {
//    Users findUserByID(String userID);
    @Query("select u from Users u where u.email = ?1")
    List<Users> findByEmailAndPassword(String email);

    @Query("select u from Users u where u.userId = ?1")
    List<Users> findUserByID(int userId);
}
