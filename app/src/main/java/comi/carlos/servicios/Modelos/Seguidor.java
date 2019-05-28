package comi.carlos.servicios.Modelos;

public class Seguidor {

    private String uid;
    private String usuario;
    private String SigueA;
    private Boolean primeraVez;

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    private Boolean activo;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSigueA() {
        return SigueA;
    }

    public void setSigueA(String sigueA) {
        SigueA = sigueA;
    }


    public Boolean getPrimeraVez() {
        return primeraVez;
    }

    public void setPrimeraVez(Boolean primeraVez) {
        this.primeraVez = primeraVez;
    }
}
