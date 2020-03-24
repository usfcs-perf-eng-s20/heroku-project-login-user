package com.example.loginuser.service;
//import com.example.loginuser.model.User;
import com.example.loginuser.model.Users;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<Users, Integer> {

    @Query("select u from Users u where u.email = ?1")
    List<Users> findByEmail(String email);

    @Query("select userName from Users u where u.email = ?1")
    String findUserNameByEmail(String email);

    @Query("select u from Users u where u.userId = ?1")
    Users findUserByID(int userId);

    @Query("select u from Users u where u.userId in ?1")
    List<Users> findUserByIDs(Integer[] userIds);
    
    @Query("select u from Users u where u.email= :email AND u.password = :password")
    List<Users> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Query("select isLoggedIn from Users u where u.userId = ?1")
    boolean isLoggedIn(int userId);

    @Modifying
    @Query("update Users u set u.isLoggedIn = ?1 where u.userId = ?2")
    int loggedI(boolean isLoggedIn, int userId);
}
