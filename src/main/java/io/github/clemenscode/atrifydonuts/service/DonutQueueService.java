package io.github.clemenscode.atrifydonuts.service;

import io.github.clemenscode.atrifydonuts.model.Order;
import io.github.clemenscode.atrifydonuts.model.OrderView;
import io.github.clemenscode.atrifydonuts.persistance.DonutQueueHolder;
import io.github.clemenscode.atrifydonuts.utils.Constants;
import io.github.clemenscode.atrifydonuts.utils.DuplicatedOrderException;
import io.github.clemenscode.atrifydonuts.utils.InvalidOrderException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonutQueueService {

    private DonutQueueHolder queueHolder;

    public DonutQueueService(DonutQueueHolder queueHolder) {
        this.queueHolder = queueHolder;
    }

    public void addNewOrder(Order newOrder) throws DuplicatedOrderException, InvalidOrderException {
        if (isOrderValid(newOrder)) {
            queueHolder.addNewOrder(newOrder);
        }else {
            throw new InvalidOrderException();
        }
    }

    public void removeOrder(int clientId) {
        queueHolder.removeOrder(clientId);
    }

    public OrderView getOrderView(int clientId) {
        return toOrderViewOrNull(queueHolder.allOrders().stream().filter(it -> it.getClientId() == clientId).findFirst(), clientId);
    }

    private OrderView toOrderViewOrNull(Optional<Order> optionalOrder, int clientId) {
        return optionalOrder.map(order -> order.toOrderView(getEstimatedTime(clientId))).orElse(null);
    }

    public List<OrderView> allOrdersInQueue() {
        return queueHolder.allOrders().stream().map(it -> it.toOrderView(getEstimatedTime(it.getClientId()))).toList();
    }

    public List<Order> retrieveNextCart() {
        return queueHolder.retrieveNextCart();
    }

    private int getEstimatedTime(int clientId) {
        if (clientId < Constants.MAX_PREMIUM_CLIENT_ID) {
            return waitTimeInMinutes(queueHolder.totalPremiumOrdersBefore(clientId));
        } else {
            return waitTimeInMinutes(queueHolder.totalOrderQuantityBefore(clientId));
        }
    }

    private int waitTimeInMinutes(int quantity) {
        return quantity / Constants.MAX_ORDER_QUANTITY * Constants.MINUTES_BETWEEN_ORDER_RETRIEVE + Constants.MINUTES_BETWEEN_ORDER_RETRIEVE;
    }

    private boolean isOrderValid(Order order) {
        return order.getQuantity() < Constants.MAX_ORDER_QUANTITY &&
                order.getQuantity() >= 0 &&
                order.getClientId() < Constants.MAX_CLIENT_ID &&
                order.getClientId() >= 0;
    }
}
