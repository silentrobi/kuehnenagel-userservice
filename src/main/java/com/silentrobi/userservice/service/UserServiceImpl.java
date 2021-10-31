package com.silentrobi.userservice.service;

import com.silentrobi.userservice.controller.UserController;
import com.silentrobi.userservice.dto.UpdateUserDto;
import com.silentrobi.userservice.dto.CreateUserDto;
import com.silentrobi.userservice.dto.UserDto;
import com.silentrobi.userservice.exception.EmailAlreadyExistException;
import com.silentrobi.userservice.exception.NotFoundException;
import com.silentrobi.userservice.externalApi.response.ResponseUserInfo;
import com.silentrobi.userservice.model.User;
import com.silentrobi.userservice.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    @Cacheable("users")
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertModelToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(UUID id) {

        var user = userRepository.findById(id).orElseThrow(() -> new NotFoundException());
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public User createUser(CreateUserDto userDto) {

        var user = modelMapper.map(userDto, User.class);
        var oldUser = userRepository.findOneByEmail(user.getEmail());
        if (oldUser != null) throw new EmailAlreadyExistException();

        final String uri = String.format("https://api.agify.io/?name=%s", userDto.getName());
        RestTemplate restTemplate = new RestTemplate();
        ResponseUserInfo userInfo = restTemplate.getForObject(uri, ResponseUserInfo.class);

        user.setAge(userInfo.getAge());

        return userRepository.save(user);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public User updateUser(UUID id, UpdateUserDto userDto) {

        var currentUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException());

        //update email is disallowed
        currentUser.setName(userDto.getName());
        currentUser.setAge(userDto.getAge());
        currentUser.setPhoneNumber(userDto.getPhoneNumber());

        return userRepository.save(currentUser);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(UUID id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException());
        userRepository.deleteById(id);
    }

    private UserDto convertModelToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
