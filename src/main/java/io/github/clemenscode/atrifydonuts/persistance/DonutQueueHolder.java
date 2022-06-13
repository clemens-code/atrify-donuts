package io.github.clemenscode.atrifydonuts.persistance;

import io.github.clemenscode.atrifydonuts.model.Order;
import io.github.clemenscode.atrifydonuts.utils.Constants;
import io.github.clemenscode.atrifydonuts.utils.DuplicatedOrderException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class DonutQueueHolder {

    private final ConcurrentLinkedQueue<Order> orders = new ConcurrentLinkedQueue<>();

    public List<Order> allOrders() {
        return new ArrayList<>(orders);
    }

    /**
     * @param order the new Order that should be placed in the Queue
     * @throws DuplicatedOrderException if the ClientId is already present in the Queue
     */
    public void addNewOrder(Order order) throws DuplicatedOrderException {
        if (orders.stream().anyMatch(it -> it.getClientId() == order.getClientId())) {
            throw new DuplicatedOrderException();
        } else {
            orders.add(order);
        }
    }

    /**
     * @param clientId the Id of the Client that wants his order to be removed=
     */
    public void removeOrder(int clientId) {
        getFirst(clientId).ifPresent(orders::remove);
    }

    private Optional<Order> getFirst(int clientId) {
        return orders.stream()
                .filter(it -> it.getClientId() == clientId)
                .findFirst();
    }

    public List<Order> retrieveNextCart() {
        List<Order> orderCart = new ArrayList<>();
        int cartSize = 0;
        while (orders.iterator().hasNext() && !isCartTooLarge(cartSize)) {
            if (allPremiumOrders().iterator().hasNext()) {
                orderCart.addAll(retrievePremiumOrders());
                cartSize = orderCart.stream().mapToInt(Order::getQuantity).sum();
            }
            Order o = orders.peek();
            if (o != null) {
                cartSize += o.getQuantity();
                addOrderIfPossible(orderCart, cartSize, o);
            }
        }
        return orderCart;
    }

    private List<Order> retrievePremiumOrders() {
        List<Order> orderCart = new ArrayList<>();
        int cartSize = 0;
        for (Order o : allPremiumOrders()) {
            cartSize += o.getQuantity();
            if (isCartTooLarge(cartSize)) {
                break;
            } else {
                addOrderIfPossible(orderCart, cartSize, o);
            }
        }

        return orderCart;
    }

    private void addOrderIfPossible(List<Order> orderCart, int cartSize, Order o) {
        if (!isCartTooLarge(cartSize)) {
            orderCart.add(o);
            orders.remove(o);
        }
    }

    public int totalOrderQuantityBefore(int clientId) {
        return orderQuantityBefore(allNormalOrders(), clientId) + totalPremiumOrdersBefore(null);
    }

    public int totalPremiumOrdersBefore(@Nullable Integer clientId) {
        if (clientId == null) {
            return allPremiumOrders().stream().mapToInt(Order::getQuantity).sum();
        } else {
            return orderQuantityBefore(allPremiumOrders(), clientId);
        }
    }

    private int orderQuantityBefore(List<Order> orderList, int clientId) {
        int quantity = 0;
        for (Order order : orderList) {
            if (order.getClientId() != clientId) {
                quantity += order.getQuantity();
            } else {
                break;
            }
        }
        return quantity;
    }

    private List<Order> allPremiumOrders() {
        return orders.stream().filter(it -> it.getClientId() < Constants.MAX_PREMIUM_CLIENT_ID).toList();
    }

    private List<Order> allNormalOrders() {
        return orders.stream().filter(it -> it.getClientId() >= Constants.MAX_PREMIUM_CLIENT_ID).toList();
    }

    private boolean isCartTooLarge(int cartSize) {
        return cartSize > Constants.MAX_ORDER_QUANTITY;
    }

}
