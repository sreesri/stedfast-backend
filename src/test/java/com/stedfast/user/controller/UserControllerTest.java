package com.stedfast.user.controller;

import com.stedfast.user.dto.UserCreateRequest;
import com.stedfast.user.models.User;
import com.stedfast.user.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsers_ShouldReturnPageOfUsers() throws Exception {
        User user = new User();
        user.setId("user_123");
        user.setName("Test User");
        user.setEmail("test@example.com");

        List<User> userList = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(userList, PageRequest.of(0, 10), 1);

        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("user_123"))
                .andExpect(jsonPath("$.content[0].name").value("Test User"));
    }

    @Test
    void createUser_ShouldReturnCreated() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("New User");
        request.setEmail("new@example.com");

        User createdUser = new User();
        createdUser.setId("user_456");
        createdUser.setName(request.getName());
        createdUser.setEmail(request.getEmail());

        when(userService.createUser(any(UserCreateRequest.class))).thenReturn(createdUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("user_456"))
                .andExpect(jsonPath("$.name").value("New User"));
    }
}
