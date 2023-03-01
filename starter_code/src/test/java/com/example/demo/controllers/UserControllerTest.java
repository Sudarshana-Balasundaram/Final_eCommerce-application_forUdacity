package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.example.demo.model.persistence.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.model.persistence.UserTest;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.TestUtils;

@PropertySource("classpath:logback.xml")
public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);
    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
    }

    @Test
    public void testFindById() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        log.info("User found by id");

        ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User responseUser = response.getBody();
        assertNotNull(responseUser);
        assertEquals(1L, responseUser.getId());
        assertEquals("testuser", responseUser.getUsername());
    }

    @Test
    public void testFindByUserName() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        log.info("User found with username");

        ResponseEntity<User> response = userController.findByUserName("testuser");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User responseUser = response.getBody();
        assertNotNull(responseUser);
        assertEquals(1L, responseUser.getId());
        assertEquals("testuser", responseUser.getUsername());
    }

    @Test
    public void testCreateUser() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("Visualization");
        request.setPassword("PAssword!");
        log.info("Test username is set with value: ", request.getUsername());
        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User responseUser = response.getBody();
        assertNotNull(responseUser);
        assertEquals(0, responseUser.getId());
        assertEquals("Visualization", responseUser.getUsername());
    }

    @Test
    public void testCreateUserWithExistingUsername() {
        User user = new User();
        user.setId(1L);
        user.setUsername("more logs");
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("more logs");
        request.setPassword("testpassword");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        log.error("Username already exists: ",request.getUsername());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testFindByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.findById(1L);
        log.info("User not found with userId");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testFindByUserNameNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);

        ResponseEntity<User> response = userController.findByUserName("testuser");
        log.info("User not found with username");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
