package com.silentrobi.userservice.service;

import com.silentrobi.userservice.dto.UpsertUserDto;
import com.silentrobi.userservice.dto.UserDto;
import com.silentrobi.userservice.exception.AlreadyExistException;
import com.silentrobi.userservice.exception.NotFoundException;
import com.silentrobi.userservice.model.User;
import com.silentrobi.userservice.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this :: convertModelToDto).collect(Collectors.toList());
    }
    @Override
    public UserDto getUserById(UUID id){

        var user =  userRepository.findById(id).orElseThrow(() -> new NotFoundException());
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public User createUser(UpsertUserDto userDto) {
        var user = modelMapper.map(userDto, User.class);
        var oldUser = userRepository.findOneByEmail(user.getEmail());
        if(oldUser != null) throw new AlreadyExistException();

        return userRepository.save(user);
    }

    @Override
    public User updateUser(UUID id, UpsertUserDto userDto) {
        var user = modelMapper.map(userDto, User.class);
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

    private UserDto convertModelToDto(User user){
       return modelMapper.map(user, UserDto.class);
    }
    private User convertDtoToModel(UserDto userDto){
        return modelMapper.map(userDto, User.class);
    }
}
