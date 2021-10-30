package com.silentrobi.userservice;
import com.google.gson.Gson;
import com.silentrobi.userservice.dto.CreateUserDto;
import com.silentrobi.userservice.model.User;
import com.silentrobi.userservice.repository.UserRepository;
import org.assertj.core.api.Assertions;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.io.IOException;
import java.util.List;

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

    @Test
    public void whenValidInput_thenCreateEmployee() throws IOException, Exception {
        CreateUserDto userDto = new CreateUserDto();
        userDto.setName("test");
        userDto.setEmail("test@gmail.com");
        userDto.setPhoneNumber("+905061796696");

        Gson gson = new Gson();

        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(userDto)));

        List<User> found = userRepository.findAll();
        Assertions.assertThat(found).hasSize(1);
        Assertions.assertThat(found).extracting(User::getName).contains("test");
    }
}
