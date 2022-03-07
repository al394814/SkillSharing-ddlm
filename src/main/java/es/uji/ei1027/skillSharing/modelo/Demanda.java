package es.uji.ei1027.skillSharing.modelo;

import java.util.Date;

public class Demanda {
    private int idDemanda;
    private String estudiante;
    private int horas;
    private Date iniFecha;
    private Date finFecha;
    private boolean activa;
    private String skill;
    private String nivel;
    private String descripcion;

    public Demanda(){
        super();
    }


    public void setIdDemanda(int idDemanda) {
        this.idDemanda = idDemanda;
    }

    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public void setIniFecha(Date iniFecha) {
        this.iniFecha = iniFecha;
    }

    public void setFinFecha(Date finFecha) {
        this.finFecha = finFecha;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdDemanda() {
        return idDemanda;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public int getHoras() {
        return horas;
    }

    public Date getIniFecha() {
        return iniFecha;
    }

    public Date getFinFecha() {
        return finFecha;
    }

    public boolean isActiva() {
        return activa;
    }

    public String getSkill() {
        return skill;
    }

    public String getNivel() {
        return nivel;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return "Demanda{" +
                "idDemanda=" + idDemanda +
                ", estudiante='" + estudiante + '\'' +
                ", horas=" + horas +
                ", iniFecha=" + iniFecha +
                ", finFecha=" + finFecha +
                ", activa=" + activa +
                ", skill='" + skill + '\'' +
                ", nivel='" + nivel + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
