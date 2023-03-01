package com.example.demo.model.persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserOrderTest {

    @Autowired
    private TestEntityManager entityManager;

    @Mock
    private User user;

    private Item item;

    private UserOrder userOrder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("This is a test item");
        item.setPrice(new BigDecimal("9.99"));

        entityManager.persist(item);

        userOrder = new UserOrder();
        userOrder.setId(1L);
        userOrder.setUser(user);
        userOrder.setItems(Arrays.asList(item));
        userOrder.setTotal(item.getPrice());
        entityManager.persist(userOrder);
    }

    @Test
    public void testFindById() {
        UserOrder foundUserOrder = entityManager.find(UserOrder.class, userOrder.getId());

        assertNotNull(foundUserOrder);
        assertEquals(userOrder, foundUserOrder);
    }

    @Test
    public void testSetTotal() {
        userOrder.setTotal(new BigDecimal("19.99"));
        entityManager.persistAndFlush(userOrder);

        UserOrder updatedUserOrder = entityManager.find(UserOrder.class, userOrder.getId());

        assertNotNull(updatedUserOrder);
        assertEquals(new BigDecimal("19.99"), updatedUserOrder.getTotal());
    }

}
