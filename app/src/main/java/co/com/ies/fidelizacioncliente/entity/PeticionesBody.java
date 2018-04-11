package co.com.ies.fidelizacioncliente.entity;

/**
 * Created by juddy on 28/03/2018.
 */

public class PeticionesBody {
    private String numeroDocumento;
    private String idPeticion;
    private String serial;
    private String nota;
    private String haySesion;

    public PeticionesBody(String idPeticion, String serial) {
        this.idPeticion = idPeticion;
        this.serial = serial;
    }

    public PeticionesBody() {
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getIdPeticion() {
        return idPeticion;
    }

    public void setIdPeticion(String idPeticion) {
        this.idPeticion = idPeticion;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getHaySesion() {
        return haySesion;
    }

    public void setHaySesion(String haySesion) {
        this.haySesion = haySesion;
    }
}
