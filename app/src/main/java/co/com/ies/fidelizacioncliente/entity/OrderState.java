package co.com.ies.fidelizacioncliente.entity;

import co.com.ies.fidelizacioncliente.utils.AppConstants;

/**
 * Estado del perdido
 */

public class OrderState {
    private String id = null;
    private String idBarItem = null;
    private boolean isNullable;
    private boolean isConfirmable;
    private String state = null;
    private String note = null;

    public OrderState() {
        state = AppConstants.OrderState.ON_QUEUE;
    }

    public OrderState(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdBarItem() {
        return idBarItem;
    }

    public void setIdBarItem(String idBarItem) {
        this.idBarItem = idBarItem;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }

    public boolean isConfirmable() {
        return isConfirmable;
    }

    public void setConfirmable(boolean confirmable) {
        isConfirmable = confirmable;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
