package co.com.ies.fidelizacioncliente.entity;

/**
 * Se encarga de los parametros que se reciben de un servicio web
 * Created by juddy on 05/04/2018.
 */
public class PremioBody {

    private String consecutivo;
    private String fechaGeneracion;
    private String monto;
    private String formato;
    private String progresivo;
    private String serialDispositivo;
    private String montoRetencion;
    private String montoReal;

    public PremioBody() {
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(String fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getProgresivo() {
        return progresivo;
    }

    public void setProgresivo(String progresivo) {
        this.progresivo = progresivo;
    }

    public String getSerialDispositivo() {
        return serialDispositivo;
    }

    public void setSerialDispositivo(String serialDispositivo) {
        this.serialDispositivo = serialDispositivo;
    }

    public String getMontoRetencion() {
        return montoRetencion;
    }

    public void setMontoRetencion(String montoRetencion) {
        this.montoRetencion = montoRetencion;
    }

    public String getMontoReal() {
        return montoReal;
    }

    public void setMontoReal(String montoReal) {
        this.montoReal = montoReal;
    }
}
