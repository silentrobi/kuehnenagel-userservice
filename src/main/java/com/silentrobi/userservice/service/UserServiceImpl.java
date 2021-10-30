package com.silentrobi.userservice.service;

import com.silentrobi.userservice.dto.UpdateUserDto;
import com.silentrobi.userservice.dto.CreateUserDto;
import com.silentrobi.userservice.dto.UserDto;
import com.silentrobi.userservice.exception.AlreadyExistException;
import com.silentrobi.userservice.exception.NotFoundException;
import com.silentrobi.userservice.externalApi.response.ResponseUserInfo;
import com.silentrobi.userservice.model.User;
import com.silentrobi.userservice.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
    @Async
    public CompletableFuture<User> createUserAsync(CreateUserDto userDto) {

        var user = modelMapper.map(userDto, User.class);
        var oldUser = userRepository.findOneByEmail(user.getEmail());
        if(oldUser != null) throw new AlreadyExistException();

        final String uri = String.format("https://api.agify.io/?name=%s", userDto.getName());
        RestTemplate restTemplate = new RestTemplate();
        ResponseUserInfo userInfo = restTemplate.getForObject(uri, ResponseUserInfo.class);

        user.setAge(userInfo.getAge());

        return CompletableFuture.completedFuture(userRepository.save(user));
    }

    @Override
    public User updateUser(UUID id, UpdateUserDto userDto) {

        var currentUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException());

        //update email is disallowed
        currentUser.setName(userDto.getName());
        currentUser.setAge(userDto.getAge());
        currentUser.setPhoneNumber(userDto.getPhoneNumber());

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
}
