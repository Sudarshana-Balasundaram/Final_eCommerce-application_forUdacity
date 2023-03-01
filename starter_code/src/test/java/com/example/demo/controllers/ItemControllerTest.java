package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testGetItems() {
        List<Item> items = new ArrayList<>();
        items.add(createItem(1L, "Item 1", "Description 1", BigDecimal.valueOf(10)));
        items.add(createItem(2L, "Item 2", "Description 2", BigDecimal.valueOf(20)));
        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();
        log.info("All items fetched");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(2, responseItems.size());
        assertTrue(responseItems.containsAll(items));
    }

    @Test
    public void testGetItemById() {
        Item item = createItem(1L, "Item 1", "Description 1", BigDecimal.valueOf(10));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        log.info("Fetched Items");

        Item responseItem = response.getBody();
        assertNotNull(responseItem);
        assertEquals(item, responseItem);
    }

    @Test
    public void testGetItemByIdNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        log.info("Item not found");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetItemsByName() {
        List<Item> items = new ArrayList<>();
        items.add(createItem(1L, "Item 1", "Description 1", BigDecimal.valueOf(10)));
        items.add(createItem(2L, "Item 1", "Description 2", BigDecimal.valueOf(20)));
        when(itemRepository.findByName("Item 1")).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Item 1");

        assertNotNull(response);
        log.info("Items fetch for name");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(2, responseItems.size());
        assertTrue(responseItems.containsAll(items));
    }

    @Test
    public void testGetItemsByNameNotFound() {
        when(itemRepository.findByName("Item 1")).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Item 1");

        assertNotNull(response);
        log.info("Items not fount with name");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private Item createItem(Long id, String name, String description, BigDecimal price) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        return item;
    }

}
