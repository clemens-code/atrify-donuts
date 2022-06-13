package io.github.clemenscode.atrifydonuts.model;

import io.github.clemenscode.atrifydonuts.utils.Constants;
import io.github.clemenscode.atrifydonuts.utils.InvalidOrderException;

public class Order {

    private int clientId;
    private int quantity;

    public Order(int clientId, int quantity) {
        this.clientId = clientId;
        this.quantity = quantity;
    }

    public int getClientId() {
        return clientId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderView toOrderView(int remainingTime) {
        return new OrderView(this, remainingTime);
    }
}
