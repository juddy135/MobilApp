package co.com.ies.fidelizacioncliente.entity;

/**
 * Se encarga de los parametros que se envian a un servicio web
 * Created by juddy on 27/03/2018.
 */

public class EnviarClaveBody {
    private String nombreUsuario;
    private String documentoCliente;

    public EnviarClaveBody(String nombreUsuario, String documentoCliente) {
        this.nombreUsuario = nombreUsuario;
        this.documentoCliente = documentoCliente;
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
}
