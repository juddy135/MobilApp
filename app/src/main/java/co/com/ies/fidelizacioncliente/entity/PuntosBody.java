package co.com.ies.fidelizacioncliente.entity;

/**
 * Se encarga de los parametros que se envian a un servicio web
 * Created by juddy on 28/03/2018.
 */

public class PuntosBody {
    private String numeroDocumento;
    private String serial;
    private String tipoDispositivo;
    private String codigoCasino;
    /**
     *
     */
    public PuntosBody(String numeroDocumento, String serial, String tipoDispositivo, String codigoCasino) {
        this.numeroDocumento = numeroDocumento;
        this.serial = serial;
        this.tipoDispositivo = tipoDispositivo;
        this.codigoCasino = codigoCasino;
    }

    public PuntosBody(String numeroDocumento, String serial, String codigoCasino) {
        this.numeroDocumento = numeroDocumento;
        this.serial = serial;
        this.codigoCasino = codigoCasino;
    }


    /**
     * @return the numeroDocumento
     */
    public String getNumeroDocumento() {
        return numeroDocumento;
    }
    /**
     * @param numeroDocumento the numeroDocumento to set
     */
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }
    /**
     * @return the serial
     */
    public String getSerial() {
        return serial;
    }
    /**
     * @param serial the serial to set
     */
    public void setSerial(String serial) {
        this.serial = serial;
    }
    /**
     * @return the tipoDispositivo
     */
    public String getTipoDispositivo() {
        return tipoDispositivo;
    }
    /**
     * @param tipoDispositivo the tipoDispositivo to set
     */
    public void setTipoDispositivo(String tipoDispositivo) {
        this.tipoDispositivo = tipoDispositivo;
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


}
