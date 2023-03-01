package com.example.demo.model.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class UserTest {

    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("testpassword");
        Cart cart = new Cart();
        user.setCart(cart);
    }

    @Test
    public void testGetId() {
        assertEquals(1L, user.getId());
    }

    @Test
    public void testSetId() {
        user.setId(2L);
        assertEquals(2L, user.getId());
    }

    @Test
    public void testGetUsername() {
        assertEquals("testuser", user.getUsername());
    }

    @Test
    public void testSetUsername() {
        user.setUsername("newuser");
        assertEquals("newuser", user.getUsername());
    }

    @Test
    public void testGetPassword() {
        assertEquals("testpassword", user.getPassword());
    }

    @Test
    public void testSetPassword() {
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }

    @Test
    public void testGetCart() {
        assertNotNull(user.getCart());
    }

    @Test
    public void testSetCart() {
        Cart newCart = new Cart();
        user.setCart(newCart);
        assertEquals(newCart, user.getCart());
    }

    @Test
    public void testNullCart() {
        user.setCart(null);
        assertNull(user.getCart());
    }
}
