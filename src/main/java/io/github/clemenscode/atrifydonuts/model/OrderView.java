package io.github.clemenscode.atrifydonuts.model;

public class OrderView {

    private Order order;
    private int remainingMinutes;

    public OrderView(Order order, int remainingMinutes){
        this.order = order;
        this.remainingMinutes = remainingMinutes;
    }

    public int getRemainingMinutes() {
        return remainingMinutes;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setRemainingMinutes(int remainingMinutes) {
        this.remainingMinutes = remainingMinutes;
    }
}
