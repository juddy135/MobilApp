package co.com.ies.fidelizacioncliente.entity;

/**
 * Created by user on 26/10/2017.
 */

public class SelectableString {

    private String value;
    private boolean checked;

    public SelectableString(String value, boolean checked) {
        this.value = value;
        this.checked = checked;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
