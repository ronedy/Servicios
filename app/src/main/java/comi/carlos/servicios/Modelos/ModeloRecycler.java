package comi.carlos.servicios.Modelos;

public class ModeloRecycler {



     private String uid; //el uid del servicio, no lo especificamos porque esto must match with firebase data ;)
    String nombre;
    String descripcion;
    String direccion;
    String color;
    String uidUsuario; //These must match with firebase data

    public ModeloRecycler(){

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getUidUsuario() {
        return uidUsuario;
    }

    public void setUidUsuario(String uidUsuario) {
        this.uidUsuario = uidUsuario;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


}
