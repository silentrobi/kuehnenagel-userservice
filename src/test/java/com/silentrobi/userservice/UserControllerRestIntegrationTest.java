package com.silentrobi.userservice;

import com.google.gson.Gson;
import com.silentrobi.userservice.dto.CreateUserDto;
import com.silentrobi.userservice.dto.UpdateUserDto;
import com.silentrobi.userservice.exception.NotFoundException;
import com.silentrobi.userservice.model.User;
import com.silentrobi.userservice.repository.UserRepository;
import org.assertj.core.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.yml")
public class UserControllerRestIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @After
    public void resetDb() {
        userRepository.deleteAll();
    }

    private User createTestUser(String name, String email, int age, String phoneNumber) {
        User user = User.builder()
                .name(name).email(email).age(age).phoneNumber(phoneNumber).build();
        return userRepository.saveAndFlush(user);
    }

    @Test
    public void whenInvalidInput_thenReturnError_forCreateUser() throws Exception {
        CreateUserDto userDto1 = CreateUserDto.builder()
                .email("test.com") // invalid email
                .phoneNumber("+905061796696")
                .build();
        Gson gson = new Gson();

        // @formatter:off
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(userDto1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("name is required")))
                .andExpect(jsonPath("$.email", is("invalid email")));
        // @formatter:on

        CreateUserDto userDto2 = CreateUserDto.builder().build();

        // @formatter:off
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(userDto2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("name is required")))
                .andExpect(jsonPath("$.email", is("email is required")))
                .andExpect(jsonPath("$.phoneNumber", is("phoneNumber is required")));
        // @formatter:on
    }

    @Test
    public void whenAlreadyExistEmailInput_thenReturnError_forCreateUser() throws Exception {
        createTestUser("alex", "alex@test.com", 45, "5061118970");
        CreateUserDto userDto = CreateUserDto.builder()
                .name("test")
                .email("alex@test.com") // email already exist
                .phoneNumber("+905061796696")
                .build();

        Gson gson = new Gson();

        // @formatter:off
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(1000)))
                .andExpect(jsonPath("$.description", is("email already exist")));
        // @formatter:on
    }

    @Test
    public void whenValidInput_thenCreateUser() throws Exception {
        CreateUserDto userDto = CreateUserDto.builder()
                .name("test")
                .email("test@gmail.com").
                phoneNumber("+905061796696")
                .build();

        Gson gson = new Gson();

        // @formatter:off
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("test")));
        // @formatter:on

        List<User> found = userRepository.findAll();
        Assertions.assertThat(found).hasSize(1);
        Assertions.assertThat(found).extracting(User::getName).contains("test");
    }

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        createTestUser("alex", "alex@test.com", 45, "5061118970");
        createTestUser("john", "jhon@test.com", 50, "1923457821");
        createTestUser("bob", "bob@test.com", 37, "5561238901");

        // @formatter:off
        mvc.perform(get("/api/v1/users").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[0].name", is("alex")))
                .andExpect(jsonPath("$[1].name", is("john")));
        // @formatter:on
    }

    @Test
    public void givenUsers_whenGetUserById_thenReturnJson() throws Exception {
        var user = createTestUser("alex", "alex@test.com", 45, "5061118970");

        // @formatter:off
        mvc.perform(get(String.format("/api/v1/users/%s", user.getId())).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("alex")))
                .andExpect(jsonPath("$.email", is("alex@test.com")));
        // @formatter:on

        User found = userRepository.findById(user.getId()).orElseThrow();
        Assertions.assertThat(found.getId()).isEqualByComparingTo(user.getId());
        Assertions.assertThat(found.getName()).isEqualTo(user.getName());
        Assertions.assertThat(found.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void whenGetUserByInvalidId_thenReturnError() throws Exception {
        var user = createTestUser("alex", "alex@test.com", 45, "5061118970");

        UpdateUserDto userDto = UpdateUserDto.builder().name("test").age(34).phoneNumber("6755219230").build();
        Gson gson = new Gson();

        // @formatter:off
        mvc.perform(put(String.format("/api/v1/users/%s", UUID.randomUUID())).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(userDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(1001)))
                .andExpect(jsonPath("$.description", is("record not found")));
        // @formatter:on
    }

    @Test
    public void whenInvalidInput_thenReturnError_forUpdateUser() throws Exception {
        UpdateUserDto userDto = UpdateUserDto.builder().build();
        Gson gson = new Gson();

        // @formatter:off
        mvc.perform(put(String.format("/api/v1/users/%s", UUID.randomUUID())).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("name is required")))
                .andExpect(jsonPath("$.age", is("age is required")))
                .andExpect(jsonPath("$.phoneNumber", is("phoneNumber is required")));
        // @formatter:on

        userDto = UpdateUserDto.builder().age(-1).build();

        // @formatter:off
        mvc.perform(put(String.format("/api/v1/users/%s", UUID.randomUUID())).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("name is required")))
                .andExpect(jsonPath("$.age", is("must be greater than or equal to 0")))
                .andExpect(jsonPath("$.phoneNumber", is("phoneNumber is required")));
        // @formatter:on
    }

    @Test
    public void whenValidInput_thenUpdateUser() throws Exception {
        var user = createTestUser("alex", "alex@test.com", 45, "5061118970");

        UpdateUserDto userDto = UpdateUserDto.builder().name("alex Cooper").age(34).phoneNumber("6755219230").build();
        Gson gson = new Gson();

        // @formatter:off
        mvc.perform(put(String.format("/api/v1/users/%s", user.getId())).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("alex Cooper")))
                .andExpect(jsonPath("$.age", is(34)))
                .andExpect(jsonPath("$.phoneNumber", is("6755219230")));
        // @formatter:on

        User found = userRepository.findById(user.getId()).orElseThrow();
        Assertions.assertThat(found.getName()).isEqualTo(userDto.getName());
        Assertions.assertThat(found.getAge()).isEqualTo(userDto.getAge());
        Assertions.assertThat(found.getPhoneNumber()).isEqualTo(userDto.getPhoneNumber());
    }

    @Test
    public void whenInvalidId_thenReturnError_forDeleteUser() throws Exception {
        var user = createTestUser("alex", "alex@test.com", 45, "5061118970");

        // @formatter:off
        mvc.perform(delete(String.format("/api/v1/users/%s", UUID.randomUUID())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(1001)))
                .andExpect(jsonPath("$.description", is("record not found")));
        // @formatter:on
    }

    @Test
    public void whenValidId_thenDeleteUser() throws Exception {
        var user = createTestUser("alex", "alex@test.com", 45, "5061118970");

        // @formatter:off
        mvc.perform(delete(String.format("/api/v1/users/%s", user.getId())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // @formatter:on

        assertThrows(NoSuchElementException.class,()->{
            userRepository.findById(user.getId()).orElseThrow();
        });
    }
}
