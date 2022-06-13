package io.github.clemenscode.atrifydonuts.service;

import io.github.clemenscode.atrifydonuts.model.Order;
import io.github.clemenscode.atrifydonuts.model.OrderView;
import io.github.clemenscode.atrifydonuts.persistance.DonutQueueHolder;
import io.github.clemenscode.atrifydonuts.utils.DuplicatedOrderException;
import io.github.clemenscode.atrifydonuts.utils.InvalidOrderException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DonutQueueServiceTest {

    private DonutQueueHolder queueHolder = Mockito.mock(DonutQueueHolder.class);

    private DonutQueueService service;

    @Before
    public void setUp() {
        service = new DonutQueueService(queueHolder);
    }

    @Test
    public void addNewOrderTest() throws DuplicatedOrderException, InvalidOrderException {
        Order order = new Order(123, 5);
        service.addNewOrder(order);
        Mockito.verify(queueHolder, Mockito.atLeast(1)).addNewOrder(order);
    }

    @Test
    public void removeOrderTest() throws DuplicatedOrderException {
        Order order = new Order(123, 5);
        service.removeOrder(order.getClientId());
        Mockito.verify(queueHolder, Mockito.atLeast(1)).removeOrder(order.getClientId());
    }

    @Test
    public void getOrderViewTest() {
        Order order = new Order(123, 5);
        List<Order> l = new ArrayList<>();
        l.add(order);
        Mockito.when(queueHolder.allOrders()).thenReturn(l);
        OrderView result = service.getOrderView(order.getClientId());
        assertThat(result.getOrder().getClientId()).isEqualTo(order.getClientId());
        assertThat(result.getOrder().getQuantity()).isEqualTo(order.getQuantity());
    }

    @Test
    public void getAllOrderViewsTest() {
        Order order = new Order(123, 5);
        Order order1 = new Order(4444, 5);
        List<Order> l = new ArrayList<>();
        l.add(order);
        l.add(order1);
        Mockito.when(queueHolder.allOrders()).thenReturn(l);
        List<OrderView> result = service.allOrdersInQueue();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.stream().findFirst().get().getOrder().getClientId()).isEqualTo(order.getClientId());
    }
}
