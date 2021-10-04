package com.example.demo.Service;

import com.example.demo.Repo.UserRepo;
import com.example.demo.domain.CustomUserDetails;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) { return userRepo.findByPhoneNumber(phoneNumber); }

    @Override
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public User getUserById(int id) {
        return userRepo.findById(id);
    }

    @Override
    public boolean authenticate(String username, String password) {
        User user = userRepo.findByUsername(username);

        return (user != null && !user.getDeleted() && passwordEncoder.matches(password, user.getPassword()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);

        if(user == null) throw new UsernameNotFoundException(username);

        return new CustomUserDetails(user);
    }

    public UserDetails loadUserById(int id){
        User user = userRepo.findById(id);

        return new CustomUserDetails(user);
    }
}
