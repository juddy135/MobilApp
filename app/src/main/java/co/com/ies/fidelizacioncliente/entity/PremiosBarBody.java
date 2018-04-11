package co.com.ies.fidelizacioncliente.entity;

/**
 * Se encarga de los parametros que se envian a un servicio web
 * Created by juddy on 28/03/2018.
 */

public class PremiosBarBody {
    private String codigoCasino;
    private String fecha;
    private String cargaTodo;

    public PremiosBarBody(String codigoCasino,String cargaTodo) {
        this.codigoCasino = codigoCasino;
        this.cargaTodo = cargaTodo;
    }

    /**
     * @return the codigoCasino
     */
    public String getCodigoCasino() {
        return codigoCasino;
    }
    /**
     * @param codigoCasino the codigoCasino to set
     */
    public void setCodigoCasino(String codigoCasino) {
        this.codigoCasino = codigoCasino;
    }
    /**
     * @return the fecha
     */
    public String getFecha() {
        return fecha;
    }
    /**
     * @param fecha the fecha to set
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    /**
     * @return the cargaTodo
     */
    public String getCargaTodo() {
        return cargaTodo;
    }
    /**
     * @param cargaTodo the cargaTodo to set
     */
    public void setCargaTodo(String cargaTodo) {
        this.cargaTodo = cargaTodo;
    }


}
