package co.com.ies.fidelizacioncliente.entity;

public class CargarMaquinaBody {
    private String nombreUsuario;
    private String serialDispositivo;
    private String codigoCasino;
    private String valorCarga;
    private String tipoCarga;

    public CargarMaquinaBody(String nombreUsuario, String serialDispositivo, String codigoCasino, String valorCarga, String tipoCarga) {
        this.nombreUsuario = nombreUsuario;
        this.serialDispositivo = serialDispositivo;
        this.codigoCasino = codigoCasino;
        this.valorCarga = valorCarga;
        this.tipoCarga = tipoCarga;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getSerialDispositivo() {
        return serialDispositivo;
    }

    public void setSerialDispositivo(String serialDispositivo) {
        this.serialDispositivo = serialDispositivo;
    }

    public String getCodigoCasino() {
        return codigoCasino;
    }

    public void setCodigoCasino(String codigoCasino) {
        this.codigoCasino = codigoCasino;
    }

    public String getValorCarga() {
        return valorCarga;
    }

    public void setValorCarga(String valorCarga) {
        this.valorCarga = valorCarga;
    }

    public String getTipoCarga() {
        return tipoCarga;
    }

    public void setTipoCarga(String tipoCarga) {
        this.tipoCarga = tipoCarga;
    }
}
