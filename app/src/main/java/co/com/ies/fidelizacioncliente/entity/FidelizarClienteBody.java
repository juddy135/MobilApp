package co.com.ies.fidelizacioncliente.entity;

/**
 * Se encarga de los parametros que se envian a un servicio web
 *
 * Created by juddy on 27/03/2018.
 */

public class FidelizarClienteBody {
    private String numeroDocumento;
    private String serial;
    private String tipoDispositivo;

    public FidelizarClienteBody(String numeroDocumento, String serial, String tipoDispositivo) {
        this.numeroDocumento = numeroDocumento;
        this.serial = serial;
        this.tipoDispositivo = tipoDispositivo;
    }

    public FidelizarClienteBody() {

    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getTipoDispositivo() {
        return tipoDispositivo;
    }

    public void setTipoDispositivo(String tipoDispositivo) {
        this.tipoDispositivo = tipoDispositivo;
    }
}
