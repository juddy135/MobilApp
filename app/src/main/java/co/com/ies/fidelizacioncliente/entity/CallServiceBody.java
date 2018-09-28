package co.com.ies.fidelizacioncliente.entity;

public class CallServiceBody {

    private String nombreUsuario;
    private String codigoCasino;
    private String documentoCliente;
    private String serial;
    private String estado;
    private String idsolicitud;

    public CallServiceBody() {
        this.idsolicitud = "";
    }

    public CallServiceBody(String nombreUsuario, String codigoCasino, String serial) {
        this.nombreUsuario = nombreUsuario;
        this.codigoCasino = codigoCasino;
        this.serial = serial;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCodigoCasino() {
        return codigoCasino;
    }

    public void setCodigoCasino(String codigoCasino) {
        this.codigoCasino = codigoCasino;
    }

    public String getDocumentoCliente() {
        return documentoCliente;
    }

    public void setDocumentoCliente(String documentoCliente) {
        this.documentoCliente = documentoCliente;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getIdsolicitud() {
        return idsolicitud;
    }

    public void setIdsolicitud(String idsolicitud) {
        this.idsolicitud = idsolicitud;
    }
}
