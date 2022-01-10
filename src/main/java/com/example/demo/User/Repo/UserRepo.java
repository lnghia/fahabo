package com.example.demo.User.Repo;

import com.example.demo.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    User findByEmail(String email);

    User findByPhoneNumber(String phoneNumber);

    User findById(int id);
}
