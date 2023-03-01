package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserTest;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.CartRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest
public class CartControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private CartController cartController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);
    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
    }

    @Test
    public void testAddToCart() throws Exception {
        // create user
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        userRepository.save(user);

        // create item
        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setPrice(BigDecimal.valueOf(1.99));
        itemRepository.save(item);

        // add item to cart
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("testUser");
        cartRequest.setItemId(1L);
        cartRequest.setQuantity(1);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/addToCart")
                        .content(TestUtils.asJsonString(cartRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // verify cart has item
        User foundUser = userRepository.findByUsername("testUser");
        assertNotNull(foundUser);
        Cart cart = foundUser.getCart();
        assertNotNull(cart);
        List<Item> items = cart.getItems();
        assertNotNull(items);
        assertTrue(items.size() == 1);
        assertEquals("testItem", items.get(0).getName());
    }

    @Test
    public void testAddToCart_invalidUser() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("invalid_user");
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        log.info("Invalid user adding to cart");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddToCart_invalidItem() {
        User user = new User();
        user.setUsername("test_user");
        user.setPassword("password");

        userRepository.save(user);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(100L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveFromCart() {
        User user = new User();
        user.setUsername("test_user");
        user.setPassword("password");

        userRepository.save(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(9.99));
        item.setDescription("This is a test item");

        itemRepository.save(item);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(item);

        cartRepository.save(cart);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(item.getId());
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Cart updatedCart = response.getBody();
        assertNotNull(updatedCart);
        assertEquals(0, updatedCart.getItems().size());
        assertEquals(user, updatedCart.getUser());
    }

    @Test
    public void testRemoveFromCart_invalidUser() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("invalid_user");
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveFromCart_invalidItem() {
        User user = new User();
        user.setUsername("test_user");
        user.setPassword("password");

        userRepository.save(user);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(100L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
