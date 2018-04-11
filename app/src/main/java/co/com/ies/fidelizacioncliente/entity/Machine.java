package co.com.ies.fidelizacioncliente.entity;

/**
 * Maquinas del casino
 */
public class Machine {

    private String numDisp;
    private String casino;
    private String serial;
    private String montoRecaudo;
    private String estadoRed;
    private String estadoSerial;
    private String estadoSwitch;
    private String estadoMaquina;

    public Machine(String numDisp, String casino, String serial, String montoRecaudo, String estadoRed, String estadoSerial, String estadoSwitch, String estadoMaquina) {
        this.numDisp = numDisp;
        this.casino = casino;
        this.serial = serial;
        this.montoRecaudo = montoRecaudo;
        this.estadoRed = estadoRed;
        this.estadoSerial = estadoSerial;
        this.estadoSwitch = estadoSwitch;
        this.estadoMaquina = estadoMaquina;
    }

    public String getNumDisp() {
        return numDisp;
    }

    public void setNumDisp(String numDisp) {
        this.numDisp = numDisp;
    }

    public String getCasino() {
        return casino;
    }

    public void setCasino(String casino) {
        this.casino = casino;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getMontoRecaudo() {
        return montoRecaudo;
    }

    public void setMontoRecaudo(String montoRecaudo) {
        this.montoRecaudo = montoRecaudo;
    }

    public String getEstadoRed() {
        return estadoRed;
    }

    public void setEstadoRed(String estadoRed) {
        this.estadoRed = estadoRed;
    }

    public String getEstadoSerial() {
        return estadoSerial;
    }

    public void setEstadoSerial(String estadoSerial) {
        this.estadoSerial = estadoSerial;
    }

    public String getEstadoSwitch() {
        return estadoSwitch;
    }

    public void setEstadoSwitch(String estadoSwitch) {
        this.estadoSwitch = estadoSwitch;
    }

    public String getEstadoMaquina() {
        return estadoMaquina;
    }

    public void setEstadoMaquina(String estadoMaquina) {
        this.estadoMaquina = estadoMaquina;
    }
}
