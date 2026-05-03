package model;

import java.util.Objects;

public class Order {

    private int id;
    private int CustomerId;
    private String orderDate;
    private double totalPrice;
    private String status;


    public Order(int id, int CustomerId, String orderDate, double totalPrice, String status) {
        this.id = id;
        this.CustomerId = CustomerId;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(int customerId) {
        CustomerId = customerId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", CustomerId=" + CustomerId + ", orderDate=" + orderDate + ", totalPrice=" + totalPrice + ", status=" + status + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && CustomerId == order.CustomerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, CustomerId);
    }
}
