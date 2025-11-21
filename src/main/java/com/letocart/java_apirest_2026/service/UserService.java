package com.letocart.java_apirest_2026.service;

import com.letocart.java_apirest_2026.entity.User;
import com.letocart.java_apirest_2026.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username, String password, String email) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User(username, password, email);
        // DB requires address NOT NULL — provide a default empty string
        user.setAddress("");
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
