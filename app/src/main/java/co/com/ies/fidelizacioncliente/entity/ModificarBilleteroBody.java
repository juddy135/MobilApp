package co.com.ies.fidelizacioncliente.entity;

public class ModificarBilleteroBody {
    private String nombreUsuario;
    private String codigoCasino;
    private String documentoCliente;
    private String valorBilletero;
    private String claveDinamica;
    private String serial;

    public ModificarBilleteroBody(String nombreUsuario, String codigoCasino, String documentoCliente, String valorBilletero, String claveDinamica) {
        this.nombreUsuario = nombreUsuario;
        this.codigoCasino = codigoCasino;
        this.documentoCliente = documentoCliente;
        this.valorBilletero = valorBilletero;
        this.claveDinamica = claveDinamica;

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

    public String getValorBilletero() {
        return valorBilletero;
    }

    public void setValorBilletero(String valorBilletero) {
        this.valorBilletero = valorBilletero;
    }

    public String getClaveDinamica() {
        return claveDinamica;
    }

    public void setClaveDinamica(String claveDinamica) {
        this.claveDinamica = claveDinamica;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
