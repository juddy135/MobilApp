package co.com.ies.fidelizacioncliente.entity;

/**
 * Clase para contener toda la info necesaria del premio
 * Created by juddy on 05/04/2018.
 */

public class PremioInfo {

    private PremioBody info;
    private String result;
    private String message;

    public PremioInfo() {
    }

    public PremioInfo(PremioBody info, String result) {
        this.info = info;
        this.result = result;
    }

    public PremioBody getInfo() {
        return info;
    }

    public void setInfo(PremioBody info) {
        this.info = info;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
