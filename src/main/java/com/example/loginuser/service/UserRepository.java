package com.example.loginuser.service;

        import com.example.loginuser.model.Users;
        import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Users, Integer> {
}
