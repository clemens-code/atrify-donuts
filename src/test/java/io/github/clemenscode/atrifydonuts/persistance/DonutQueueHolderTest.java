package io.github.clemenscode.atrifydonuts.persistance;

import io.github.clemenscode.atrifydonuts.model.Order;
import io.github.clemenscode.atrifydonuts.utils.DuplicatedOrderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DonutQueueHolderTest {

    @Autowired
    private DonutQueueHolder queueHolder = new DonutQueueHolder();

    @BeforeEach
    public void init() {
        queueHolder.allOrders().forEach(it -> queueHolder.removeOrder(it.getClientId()));
    }

    @Test
    public void testAddNewOrder() throws DuplicatedOrderException {
        int clientId = 123;
        int quantity = 10;
        Order newOrder = new Order(clientId, quantity);
        queueHolder.addNewOrder(newOrder);
        List<Order> result = queueHolder.allOrders();
        assertThat(result).hasSize(1);
        assertThat(result.stream().findFirst().isPresent()).isTrue();
        assertThat(result.stream().findFirst().get().getClientId()).isEqualTo(clientId);
        assertThat(result.stream().findFirst().get().getQuantity()).isEqualTo(quantity);
    }

    @Test
    public void newOrderAlreadyExists() throws DuplicatedOrderException {
        int clientId = 123;
        int quantity = 10;
        Order newOrder = new Order(clientId, quantity);
        queueHolder.addNewOrder(newOrder);
        assertThrows(DuplicatedOrderException.class, () -> {
            queueHolder.addNewOrder(newOrder);
        });
    }

    @Test
    public void retrieveCartTest() {
        Order order1 = new Order(123, 23);
        Order order2 = new Order(234, 27);
        try {
            queueHolder.addNewOrder(order1);
            queueHolder.addNewOrder(order2);
        } catch (Exception e) {
            //should not happen in this test
        }
        List<Order> result = queueHolder.retrieveNextCart();
        assertThat(result).hasSize(2);
        assertThat(result.stream().mapToInt(Order::getQuantity).sum()).isEqualTo(50);
        List<Order> resultAfterCart = queueHolder.allOrders();
        assertThat(resultAfterCart).hasSize(0);
    }

    @Test
    public void retrieveCartTest_notEnoughOrders() {
        Order order1 = new Order(123, 23);
        Order order2 = new Order(234, 5);
        try {
            queueHolder.addNewOrder(order1);
            queueHolder.addNewOrder(order2);
        } catch (Exception e) {
            //should not happen in this test
        }
        List<Order> result = queueHolder.retrieveNextCart();
        assertThat(result).hasSize(2);
        assertThat(result.stream().mapToInt(Order::getQuantity).sum()).isEqualTo(28);
        List<Order> resultAfterCart = queueHolder.allOrders();
        assertThat(resultAfterCart).hasSize(0);
    }

    @Test
    public void retrieveCartTest_cutOffOrderExactly() {
        Order order1 = new Order(123, 23);
        Order order2 = new Order(234, 27);
        Order order3 = new Order(345, 27);
        try {
            queueHolder.addNewOrder(order1);
            queueHolder.addNewOrder(order2);
            queueHolder.addNewOrder(order3);
        } catch (Exception e) {
            //should not happen in this test
        }
        List<Order> result = queueHolder.retrieveNextCart();
        assertThat(result).hasSize(2);
        assertThat(result.stream().mapToInt(Order::getQuantity).sum()).isEqualTo(50);
        List<Order> resultAfterCart = queueHolder.allOrders();
        assertThat(resultAfterCart).hasSize(1);
    }

    @Test
    public void retrieveCartTest_cutOffNotEnough() {
        Order order1 = new Order(123, 23);
        Order order2 = new Order(234, 5);
        Order order3 = new Order(345, 30);
        try {
            queueHolder.addNewOrder(order1);
            queueHolder.addNewOrder(order2);
            queueHolder.addNewOrder(order3);
        } catch (Exception e) {
            //should not happen in this test
        }
        List<Order> result = queueHolder.retrieveNextCart();
        assertThat(result).hasSize(2);
        assertThat(result.stream().mapToInt(Order::getQuantity).sum()).isEqualTo(28);
        List<Order> resultAfterCart = queueHolder.allOrders();
        assertThat(resultAfterCart).hasSize(1);
    }

    @Test
    public void retrievePremiumOrdersFirst() {
        Order order1 = new Order(123, 20);
        Order order2 = new Order(3_000, 5);
        Order order3 = new Order(999, 30);
        try {
            queueHolder.addNewOrder(order1);
            queueHolder.addNewOrder(order2);
            queueHolder.addNewOrder(order3);
        } catch (Exception e) {
            //should not happen in this test
        }
        List<Order> result = queueHolder.retrieveNextCart();
        assertThat(result).hasSize(2);
        assertThat(result.stream().mapToInt(Order::getQuantity).sum()).isEqualTo(50);
        List<Order> resultAfterCart = queueHolder.allOrders();
        assertThat(resultAfterCart).hasSize(1);
    }

    @Test
    public void retrievePremiumOrdersFirst_andOneNormal() {
        Order order1 = new Order(123, 23);
        Order order2 = new Order(234, 5);
        Order order3 = new Order(1_000, 15);
        Order order4 = new Order(5_000, 30);
        try {
            queueHolder.addNewOrder(order1);
            queueHolder.addNewOrder(order2);
            queueHolder.addNewOrder(order3);
            queueHolder.addNewOrder(order4);
        } catch (Exception e) {
            //should not happen in this test
        }
        List<Order> result = queueHolder.retrieveNextCart();
        assertThat(result).hasSize(3);
        assertThat(result.stream().mapToInt(Order::getQuantity).sum()).isEqualTo(43);
        List<Order> resultAfterCart = queueHolder.allOrders();
        assertThat(resultAfterCart).hasSize(1);
    }

    @Test
    public void totalQuantityBeforeTest() {
        Order order2 = new Order(234, 5);
        Order order3 = new Order(1_000, 15);
        Order order4 = new Order(5_000, 30);
        try {
            queueHolder.addNewOrder(order2);
            queueHolder.addNewOrder(order3);
            queueHolder.addNewOrder(order4);
        } catch (Exception e) {
            //should not happen in this test
        }
        int q = queueHolder.totalOrderQuantityBefore(order4.getClientId());
        assertThat(q).isEqualTo(20);
    }

    @Test
    public void totalQuantityPremiumBeforeTest() {
        Order order2 = new Order(234, 5);
        Order order3 = new Order(1_000, 15);
        Order order4 = new Order(111, 30);
        try {
            queueHolder.addNewOrder(order2);
            queueHolder.addNewOrder(order3);
            queueHolder.addNewOrder(order4);
        } catch (Exception e) {
            //should not happen in this test
        }
        int q = queueHolder.totalPremiumOrdersBefore(order4.getClientId());
        assertThat(q).isEqualTo(5);
    }


}
