package com.silentrobi.userservice;

import com.google.gson.Gson;

import com.silentrobi.userservice.controller.UserController;
import com.silentrobi.userservice.dto.UserDto;
import com.silentrobi.userservice.exception.EmailAlreadyExistException;
import com.silentrobi.userservice.exception.NotFoundException;
import com.silentrobi.userservice.model.User;
import com.silentrobi.userservice.repository.UserRepository;
import com.silentrobi.userservice.service.UserService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;

import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService service;

    @MockBean
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void whenPostUserWithAlreadyExistEmailId_thenThrowException_forCreateUser() throws Exception {
        User alex = User
                .builder()
                .name("alex")
                .email("alex@test.com")
                .phoneNumber("5061796688")
                .build();

        Gson gson = new Gson();

        given(service.createUser(Mockito.any())).willThrow(EmailAlreadyExistException.class);

        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(alex)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(1000)))
                .andExpect(jsonPath("$.description", is("email already exist")));
        verify(service, VerificationModeFactory.times(1)).createUser(Mockito.any());
        reset(service);
    }

    @Test
    public void whenPostUser_thenCreateUser() throws Exception {
        User alex = User
                .builder()
                .id(UUID.randomUUID())
                .name("alex")
                .email("alex@test.com")
                .phoneNumber("5061796688")
                .build();

        Gson gson = new Gson();

        given(service.createUser(Mockito.any())).willReturn(alex);

        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(alex))).andExpect(status().isCreated()).andExpect(jsonPath("$.name", is("alex")));
        verify(service, VerificationModeFactory.times(1)).createUser(Mockito.any());
        reset(service);
    }

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        UserDto alex = UserDto.builder()
                .name("alex")
                .age(40)
                .email("alex@test.com")
                .phoneNumber("4355553212")
                .build();
        UserDto john = UserDto.builder()
                .name("john")
                .age(29)
                .email("john@test.com")
                .phoneNumber("5675534445")
                .build();
        UserDto bob = UserDto.builder()
                .name("bob")
                .age(32)
                .email("bob@test.com")
                .phoneNumber("5555553431")
                .build();

        List<UserDto> allEmployees = Arrays.asList(alex, john, bob);

        given(service.getAllUsers()).willReturn(allEmployees);

        mvc.perform(get("/api/v1/users").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3))).andExpect(jsonPath("$[0].name", is(alex.getName()))).andExpect(jsonPath("$[1].name", is(john.getName())))
                .andExpect(jsonPath("$[2].name", is(bob.getName())));
        verify(service, VerificationModeFactory.times(1)).getAllUsers();
        reset(service);
    }

    @Test
    public void givenUsers_whenGetUserByInvalidId_thenReturnError() throws Exception {

        given(service.getUserById(Mockito.any())).willThrow(NotFoundException.class);

        mvc.perform(get(String.format("/api/v1/users/%s", UUID.randomUUID())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(1001)))
                .andExpect(jsonPath("$.description", is("record not found")));
        verify(service, VerificationModeFactory.times(1)).getUserById(Mockito.any());
        reset(service);
    }

    @Test
    public void givenUsers_whenGetUserById_thenReturnThatUser() throws Exception {
        UserDto alex = UserDto.builder()
                .name("alex")
                .age(40)
                .email("alex@test.com")
                .phoneNumber("4355553212")
                .build();
        UserDto john = UserDto.builder()
                .name("john")
                .age(29)
                .email("john@test.com")
                .phoneNumber("5675534445")
                .build();
        UserDto bob = UserDto.builder()
                .name("bob")
                .age(32)
                .email("bob@test.com")
                .phoneNumber("5555553431")
                .build();

        List<UserDto> allUsers = Arrays.asList(alex, john, bob);

        given(service.getUserById(Mockito.any())).willReturn(allUsers.get(0));

        mvc.perform(get(String.format("/api/v1/users/%s", UUID.randomUUID())).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.name", is(alex.getName()))).andExpect(jsonPath("$.email", is(alex.getEmail())))
                .andExpect(jsonPath("$.phoneNumber", is(alex.getPhoneNumber())));
        verify(service, VerificationModeFactory.times(1)).getUserById(Mockito.any());
        reset(service);
    }

    @Test
    public void whenPutUserWithInvalidUserId_thenThrowException_forUpdateUser() throws Exception {
        User alex = User
                .builder()
                .name("alex")
                .age(37)
                .email("alex@test.com")
                .phoneNumber("5061796688")
                .build();

        Gson gson = new Gson();

        given(service.updateUser(Mockito.any(), Mockito.any())).willThrow(NotFoundException.class);

        mvc.perform(put(String.format("/api/v1/users/%s", UUID.randomUUID())).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(alex)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(1001)))
                .andExpect(jsonPath("$.description", is("record not found")));
        verify(service, VerificationModeFactory.times(1)).updateUser(Mockito.any(), Mockito.any());
        reset(service);
    }

    @Test
    public void whenPutUser_thenUpdateUser() throws Exception {
        User alex = User
                .builder()
                .id(UUID.randomUUID())
                .name("alex")
                .age(40)
                .email("alex@test.com")
                .phoneNumber("5061796688")
                .build();

        Gson gson = new Gson();

        given(service.updateUser(Mockito.any(), Mockito.any())).willReturn(alex);

        mvc.perform(put(String.format("/api/v1/users/%s", alex.getId())).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(alex)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("alex")))
                .andExpect(jsonPath("$.email", is("alex@test.com")));
        verify(service, VerificationModeFactory.times(1)).updateUser(Mockito.any(), Mockito.any());
        reset(service);
    }

    @Test
    public void givenUsers_whenDeleteUserByInvalidId_thenReturnError() throws Exception {

        doThrow(NotFoundException.class).when(service).deleteUser(Mockito.any());

        mvc.perform(delete(String.format("/api/v1/users/%s", UUID.randomUUID())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(1001)))
                .andExpect(jsonPath("$.description", is("record not found")));
        verify(service, VerificationModeFactory.times(1)).deleteUser(Mockito.any());
        reset(service);
    }

    @Test
    public void givenUsers_whenDeleteUserById_thenReturnNothing() throws Exception {
        UserDto alex = UserDto.builder()
                .name("alex")
                .age(40)
                .email("alex@test.com")
                .phoneNumber("4355553212")
                .build();
        UserDto john = UserDto.builder()
                .name("john")
                .age(29)
                .email("john@test.com")
                .phoneNumber("5675534445")
                .build();
        UserDto bob = UserDto.builder()
                .name("bob")
                .age(32)
                .email("bob@test.com")
                .phoneNumber("5555553431")
                .build();

        List<UserDto> allUsers = Arrays.asList(alex, john, bob);

        doNothing().when(service).deleteUser(Mockito.any());

        mvc.perform(delete(String.format("/api/v1/users/%s", UUID.randomUUID())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(service, VerificationModeFactory.times(1)).deleteUser(Mockito.any());
        reset(service);
    }
}
