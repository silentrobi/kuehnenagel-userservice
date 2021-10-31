package com.silentrobi.userservice;

import com.silentrobi.userservice.dto.CreateUserDto;
import com.silentrobi.userservice.dto.UpdateUserDto;
import com.silentrobi.userservice.dto.UserDto;
import com.silentrobi.userservice.exception.EmailAlreadyExistException;
import com.silentrobi.userservice.exception.NotFoundException;
import com.silentrobi.userservice.model.User;
import com.silentrobi.userservice.repository.UserRepository;
import com.silentrobi.userservice.service.*;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;

@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    @TestConfiguration
    static class UserServiceImplTestContextConfiguration {
        @Bean
        public UserService userService() {
            return new UserServiceImpl();
        }
    }

    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ModelMapper mockModelMapper;

    @Before
    public void setup() {
        User alex = User.builder()
                .id(UUID.fromString("741262a3-2985-40e9-af57-c7694134b45e"))
                .name("alex")
                .age(40)
                .email("alex@test.com")
                .phoneNumber("4355553212")
                .build();
        User john = User.builder()
                .id(UUID.fromString("1f1e8b94-0c86-4ae3-a291-3ba372aafdcc"))
                .name("john")
                .age(29)
                .email("john@test.com")
                .phoneNumber("5675534445")
                .build();

        User mariaDto = User.builder()
                .name("maria")
                .email("alex@test.com")
                .phoneNumber("8127121233")
                .build();

        List<User> allUsers = Arrays.asList(alex, john);

        Mockito.when(userRepository.findAll()).thenReturn(allUsers);
        Mockito.when(userRepository.findOneByEmail(alex.getEmail())).thenReturn(alex);
        Mockito.when(userRepository.findById(alex.getId())).thenReturn(Optional.of(alex));
        Mockito.when(mockModelMapper.map(alex, UserDto.class)).thenReturn(UserDto
                .builder()
                .name(alex.getName())
                .age(alex.getAge())
                .email(alex.getEmail())
                .phoneNumber(alex.getPhoneNumber())
                .build());

        Mockito.when(mockModelMapper.map(john, UserDto.class)).thenReturn(UserDto
                .builder()
                .name(john.getName())
                .age(john.getAge())
                .email(john.getEmail())
                .phoneNumber(john.getPhoneNumber())
                .build());
    }

    @Test
    public void whenInvoked_getAllUsers_shouldWork() {

        List<UserDto> found = userService.getAllUsers();
        Assertions.assertThat(found.size()).isEqualTo(2);
        Assertions.assertThat(found).extracting(UserDto::getName).contains("alex", "john");
    }

    @Test
    public void whenValidUserId_getUser() {
        UserDto found = userService.getUserById(UUID.fromString("741262a3-2985-40e9-af57-c7694134b45e"));
        Assertions.assertThat(found.getName()).isEqualTo("alex");
        Assertions.assertThat(found.getAge()).isEqualTo(40);
    }

    @Test
    public void whenInValidUserId_getUserNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(UUID.randomUUID());
        });
    }

    @Test
    public void whenEmailIdNotUnique_throwEmailAlreadyExistException() {
        CreateUserDto mariaDto = CreateUserDto.builder()
                .name("maria")
                .email("alex@test.com")
                .phoneNumber("8127121233")
                .build();
        Mockito.when(mockModelMapper.map(mariaDto, User.class)).thenReturn(User
                .builder()
                .name(mariaDto.getName())
                .email(mariaDto.getEmail())
                .phoneNumber(mariaDto.getPhoneNumber())
                .build());
        assertThrows(EmailAlreadyExistException.class, () -> {
            userService.createUser(mariaDto);
        });
    }

    @Test
    public void whenValidInput_createUser() {
        CreateUserDto mariaDto = CreateUserDto.builder()
                .name("maria")
                .email("maria@test.com")
                .phoneNumber("8127121233")
                .build();
        User maria = User
                .builder()
                .id(UUID.randomUUID())
                .name(mariaDto.getName())
                .email(mariaDto.getEmail())
                .phoneNumber(mariaDto.getPhoneNumber())
                .build();
        Mockito.when(mockModelMapper.map(mariaDto, User.class)).thenReturn(maria);
        Mockito.when(userRepository.save(maria)).thenReturn(maria);
        var added = userService.createUser(mariaDto);

        Assertions.assertThat(added.getName()).isEqualTo(mariaDto.getName());
        Assertions.assertThat(added.getEmail()).isEqualTo(mariaDto.getEmail());
        Assertions.assertThat(added.getAge()).isNotNull(); //check age value from external api
    }

    @Test
    public void whenInvalidUserId_throwNotFoundException_forUpdateUser() {
        UpdateUserDto mariaDto = UpdateUserDto.builder()
                .name("maria")
                .phoneNumber("2123712123")
                .age(27)
                .build();

        assertThrows(NotFoundException.class, () -> {
            userService.updateUser(UUID.randomUUID(), mariaDto);
        });
    }

    @Test
    public void whenValidUserId_thenUpdateUser() {
        UpdateUserDto mariaDto = UpdateUserDto.builder()
                .name("maria jons")
                .age(20)
                .phoneNumber("1245121233")
                .build();
        User maria = User
                .builder()
                .id(UUID.randomUUID())
                .name(mariaDto.getName())
                .email("maria@test.com")
                .age(mariaDto.getAge())
                .phoneNumber(mariaDto.getPhoneNumber())
                .build();

        Mockito.when(userRepository.findById(maria.getId())).thenReturn(Optional.of(maria));

        Mockito.when(userRepository.save(maria)).thenReturn(maria);

        var updated = userService.updateUser(maria.getId(), mariaDto);

        Assertions.assertThat(updated.getName()).isEqualTo(mariaDto.getName());
        Assertions.assertThat(updated.getAge()).isEqualTo(mariaDto.getAge());
        Assertions.assertThat(updated.getPhoneNumber()).isEqualTo(mariaDto.getPhoneNumber());
    }

    @Test
    public void whenInValidUserId_throwNotFoundExecption_forDeleteUser() {
        assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(UUID.randomUUID());
        });
    }

    @Test
    public void whenValidUserId_thenDeleteUser() {
        UUID alexUUID = UUID.fromString("741262a3-2985-40e9-af57-c7694134b45e");

        doNothing().when(userRepository).deleteById(alexUUID);
        userService.deleteUser(alexUUID);

    }
}
