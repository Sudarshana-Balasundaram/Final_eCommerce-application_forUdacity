package com.example.demo.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.math.BigDecimal;
import java.util.List;

import com.example.demo.model.persistence.*;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);
    @Test
    public void testSubmitOrder() throws Exception {
        // create user and cart
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        // add item to cart
        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setPrice(BigDecimal.valueOf(1.99));
        itemRepository.save(item);

        cart.addItem(item);
        cartRepository.save(cart);

        MvcResult result = mockMvc.perform((RequestBuilder) post("/api/order/submit/testUser"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        UserOrder order = objectMapper.readValue(json, UserOrder.class);

        assertNotNull(order);
        assertEquals(user, order.getUser());
        assertEquals(1, order.getItems().size());
        assertEquals("testItem", order.getItems().get(0).getName());
        assertEquals(BigDecimal.valueOf(1.99), order.getItems().get(0).getPrice());
        assertEquals(BigDecimal.valueOf(1.99), order.getTotal());

    }

    @Test
    public void testSubmitOrderWithInvalidUser() throws Exception {
        mockMvc.perform((RequestBuilder) post("/api/order/submit/invalidUser"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetOrdersForUser() throws Exception {
        // create user and orders
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setPrice(BigDecimal.valueOf(1.99));
        itemRepository.save(item);

        cart.addItem(item);
        cartRepository.save(cart);

        UserOrder order1 = UserOrder.createFromCart(cart);
        order1.setUser(user);
        orderRepository.save(order1);

        UserOrder order2 = UserOrder.createFromCart(cart);
        order2.setUser(user);
        orderRepository.save(order2);

        MvcResult result = mockMvc.perform(get("/api/order/history/testUser"))
                .andExpect(status().isOk())
                .andReturn();
        List<UserOrder> orders = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<UserOrder>>() {});
        log.info("Orders for specific user fetched");

        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertEquals("testUser", orders.get(0).getUser().getUsername());
        assertEquals("testItem", orders.get(0).getItems().get(0).getName());
        assertEquals(BigDecimal.valueOf(1.99), orders.get(0).getItems().get(0).getPrice());
        assertEquals(BigDecimal.valueOf(1.99), orders.get(0).getTotal());
        assertEquals("testUser", orders.get(1).getUser().getUsername());
        assertEquals("testItem", orders.get(1).getItems().get(0).getName());
        assertEquals(BigDecimal.valueOf(1.99), orders.get(1).getItems().get(0).getPrice());
        assertEquals(BigDecimal.valueOf(1.99), orders.get(1).getTotal());

    }
}