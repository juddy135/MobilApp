package co.com.ies.fidelizacioncliente.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Clase para contener toda la info necesaria del bar
 */
public class BarInfo {

    private String result;
    private List<BarItem> listAvaliableItems;
    private String avaliablePoints;

    public BarInfo() {
        listAvaliableItems = new ArrayList<>();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<BarItem> getListAvaliableItems() {
        return listAvaliableItems;
    }

    public void setListAvaliableItems(List<BarItem> listAvaliableItems) {
        this.listAvaliableItems = listAvaliableItems;
    }

    public String getAvaliablePoints() {
        return avaliablePoints;
    }

    public void setAvaliablePoints(String avaliablePoints) {
        this.avaliablePoints = avaliablePoints;
    }

    public boolean addOrderToItem(OrderState orderState) {

        boolean result = false;
        for (BarItem barItem : listAvaliableItems) {
            if (barItem.getId().equals(orderState.getIdBarItem())) {
                barItem.setOrderState(orderState);
                result = true;
                break;
            }
        }

        return result;
    }

    public boolean addPaymentToItem(String id, String payment) {
        boolean result = false;
        for (BarItem barItem : listAvaliableItems) {
            if (barItem.getId().equals(id)) {
                barItem.setPayment(payment);
                result = true;
                break;
            }
        }

        return result;
    }


    /**
     * Verificamos si existe alguna orden pendiente para que la pantalla de usuario active este estado
     *
     * @return
     */
    public boolean pendingOrders() {
        boolean result = false;
        for (BarItem barItem : listAvaliableItems) {
            if (barItem.getOrderState() != null) {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * En caso de que un item no tenga cantidad disponible ni ordenes pendientes no vale la pena que aparezca en la lista del bar
     */
    public void cleanList() {

        Iterator<BarItem> iterator = listAvaliableItems.iterator();

        while (iterator.hasNext()) {
            BarItem barItem = iterator.next();
            if (barItem.getCantAvailable() == 0 && !barItem.hasOrder()) {
                iterator.remove();
            }
        }
    }


}
