package co.com.ies.fidelizacioncliente.entity;

public class ConsultarBilleteroBody {
    private String nombreUsuario;
    private String documentoCliente;
    private String claveDinamica;

    public ConsultarBilleteroBody(String nombreUsuario, String documentoCliente, String claveDinamica) {
        this.nombreUsuario = nombreUsuario;
        this.documentoCliente = documentoCliente;
        this.claveDinamica = claveDinamica;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getDocumentoCliente() {
        return documentoCliente;
    }

    public void setDocumentoCliente(String documentoCliente) {
        this.documentoCliente = documentoCliente;
    }

    public String getClaveDinamica() {
        return claveDinamica;
    }

    public void setClaveDinamica(String claveDinamica) {
        this.claveDinamica = claveDinamica;
    }
}
