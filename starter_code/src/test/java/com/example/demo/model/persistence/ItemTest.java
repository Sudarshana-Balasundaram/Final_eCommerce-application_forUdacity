package com.example.demo.model.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

public class ItemTest {

    private Item item;

    @Before
    public void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(new BigDecimal(10));
        item.setDescription("A test item");
    }

    @Test
    public void testGetters() {
        assertEquals(1L, item.getId().longValue());
        assertEquals("Test Item", item.getName());
        assertEquals(new BigDecimal(10), item.getPrice());
        assertEquals("A test item", item.getDescription());
    }

    @Test
    public void testEqualsAndHashCode() {
        Item sameItem = new Item();
        sameItem.setId(1L);
        sameItem.setName("Test Item");
        sameItem.setPrice(new BigDecimal(10));
        sameItem.setDescription("A test item");

        assertTrue(item.equals(sameItem));
        assertTrue(item.hashCode() == sameItem.hashCode());

        Item differentItem = new Item();
        differentItem.setId(2L);
        differentItem.setName("Different Item");
        differentItem.setPrice(new BigDecimal(5));
        differentItem.setDescription("A different item");

        assertFalse(item.equals(differentItem));
        assertFalse(item.hashCode() == differentItem.hashCode());
    }

}
