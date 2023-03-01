package com.example.demo.model.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CartTest {

    private Cart cart;

    @Before
    public void setUp() {
        cart = new Cart();
    }

    @Test
    public void testGetSetId() {
        Long id = 1L;
        cart.setId(id);
        assertNotNull(cart.getId());
        assertEquals(id, cart.getId());
    }

    @Test
    public void testGetSetItems() {
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(10.00));
        items.add(item);
        cart.setItems(items);
        assertNotNull(cart.getItems());
        assertEquals(1, cart.getItems().size());
        assertEquals(item, cart.getItems().get(0));
    }

    @Test
    public void testAddItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(10.00));
        cart.addItem(item);
        assertNotNull(cart.getItems());
        assertEquals(1, cart.getItems().size());
        assertEquals(item, cart.getItems().get(0));
        assertNotNull(cart.getTotal());
        assertEquals(item.getPrice(), cart.getTotal());
    }

    @Test
    public void testRemoveItem() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setPrice(BigDecimal.valueOf(10.00));
        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Test Item 2");
        item2.setPrice(BigDecimal.valueOf(5.00));
        cart.addItem(item1);
        cart.addItem(item2);
        assertNotNull(cart.getItems());
        assertEquals(2, cart.getItems().size());
        assertEquals(item1, cart.getItems().get(0));
        assertEquals(item2, cart.getItems().get(1));
        assertNotNull(cart.getTotal());
        assertEquals(item1.getPrice().add(item2.getPrice()), cart.getTotal());
        cart.removeItem(item2);
        assertNotNull(cart.getItems());
        assertEquals(1, cart.getItems().size());
        assertEquals(item1, cart.getItems().get(0));
        assertNotNull(cart.getTotal());
        assertEquals(item1.getPrice(), cart.getTotal());
    }

}
