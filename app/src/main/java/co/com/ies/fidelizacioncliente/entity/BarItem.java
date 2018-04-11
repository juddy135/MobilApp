package co.com.ies.fidelizacioncliente.entity;

import java.io.Serializable;

/**
 * Clase para representar los items del bar.
 */
public class BarItem implements Serializable {

    private String id;
    private String name;
    private String price = null;
    private String points = null;
    private String absoluthPathThumbnail = null;
    private OrderState orderState = null;
    private int cantAvailable;
    private String category;
    private String payment;


    public BarItem(String id, int cantAvailable) {
        this.id = id;
        this.cantAvailable = cantAvailable;
        category = "";
    }

    public BarItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getAbsoluthPathThumbnail() {
        return absoluthPathThumbnail;
    }

    public void setAbsoluthPathThumbnail(String absoluthPathThumbnail) {
        this.absoluthPathThumbnail = absoluthPathThumbnail;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    public boolean hasOrder() {
        return orderState != null;
    }

    public int getCantAvailable() {
        return cantAvailable;
    }

    public void setCantAvailable(int cantAvailable) {
        this.cantAvailable = cantAvailable;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

}
