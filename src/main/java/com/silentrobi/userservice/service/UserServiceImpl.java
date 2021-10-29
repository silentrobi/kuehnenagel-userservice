package com.silentrobi.userservice.service;

import com.silentrobi.userservice.exception.AlreadyExistException;
import com.silentrobi.userservice.exception.NotFoundException;
import com.silentrobi.userservice.model.User;
import com.silentrobi.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @Override
    public User getUserById(UUID id){
            return userRepository.findById(id).orElseThrow(() -> new NotFoundException());
    }

    @Override
    public User createUser(User user) {
        var oldUser = userRepository.findOneByEmail(user.getEmail());
        if(oldUser != null) throw new AlreadyExistException();

        return userRepository.save(user);
    }

    @Override
    public User updateUser(UUID id, User user) {
        var currentUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException());

        currentUser.setEmail(user.getEmail());
        currentUser.setName(user.getName());
        currentUser.setAge(user.getAge());
        currentUser.setPhoneNumber(user.getPhoneNumber());

        return userRepository.save(currentUser);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException());
        userRepository.deleteById(id);
    }
}
