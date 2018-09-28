package co.com.ies.fidelizacioncliente.entity;

public class StateCallServiceBody {

    private String nombreUsuario;
    private String estado;
    private String idsolicitud;

    public StateCallServiceBody() {
    }

    public StateCallServiceBody(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
        this.estado="";
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
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
