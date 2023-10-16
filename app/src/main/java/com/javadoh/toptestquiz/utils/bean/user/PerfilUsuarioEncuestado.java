package com.javadoh.toptestquiz.utils.bean.user;

import java.io.Serializable;

/**
 * Created by luiseliberal on 18/05/16.
 */
public class PerfilUsuarioEncuestado implements Serializable{

    private int userIdEncuestador;
    private String userLoginEncuestador;
    private String dniEncuestado;
    private String nombresEncuestado;
    private String apellidosEncuestado;
    private String emailEncuestado;
    private String diaEncuestado;
    private String horaEncuestado;
    private String locacionEncuestado;
    private String generoEncuestado;
    private int edadEncuestado;
    private String comunaEncuestado;
    private String ocupacionEncuestado;
    private int segundosTotalesPrueba;

    public int getUserIdEncuestador() {
        return userIdEncuestador;
    }

    public void setUserIdEncuestador(int userIdEncuestador) {
        this.userIdEncuestador = userIdEncuestador;
    }

    public String getUserLoginEncuestador() {
        return userLoginEncuestador;
    }

    public void setUserLoginEncuestador(String userLoginEncuestador) {
        this.userLoginEncuestador = userLoginEncuestador;
    }

    public String getDniEncuestado() {
        return dniEncuestado;
    }

    public void setDniEncuestado(String dniEncuestado) {
        this.dniEncuestado = dniEncuestado;
    }

    public String getNombresEncuestado() {
        return nombresEncuestado;
    }

    public void setNombresEncuestado(String nombresEncuestado) {
        this.nombresEncuestado = nombresEncuestado;
    }

    public String getApellidosEncuestado() {
        return apellidosEncuestado;
    }

    public void setApellidosEncuestado(String apellidosEncuestado) {
        this.apellidosEncuestado = apellidosEncuestado;
    }

    public String getEmailEncuestado() {
        return emailEncuestado;
    }

    public void setEmailEncuestado(String emailEncuestado) {
        this.emailEncuestado = emailEncuestado;
    }

    public String getDiaEncuestado() {
        return diaEncuestado;
    }

    public void setDiaEncuestado(String diaEncuestado) {
        this.diaEncuestado = diaEncuestado;
    }

    public String getHoraEncuestado() {
        return horaEncuestado;
    }

    public void setHoraEncuestado(String horaEncuestado) {
        this.horaEncuestado = horaEncuestado;
    }

    public String getLocacionEncuestado() {
        return locacionEncuestado;
    }

    public void setLocacionEncuestado(String locacionEncuestado) {
        this.locacionEncuestado = locacionEncuestado;
    }

    public String getGeneroEncuestado() {
        return generoEncuestado;
    }

    public void setGeneroEncuestado(String generoEncuestado) {
        this.generoEncuestado = generoEncuestado;
    }

    public int getEdadEncuestado() {
        return edadEncuestado;
    }

    public void setEdadEncuestado(int edadEncuestado) {
        this.edadEncuestado = edadEncuestado;
    }

    public String getComunaEncuestado() {
        return comunaEncuestado;
    }

    public void setComunaEncuestado(String comunaEncuestado) {
        this.comunaEncuestado = comunaEncuestado;
    }

    public String getOcupacionEncuestado() {
        return ocupacionEncuestado;
    }

    public void setOcupacionEncuestado(String ocupacionEncuestado) {
        this.ocupacionEncuestado = ocupacionEncuestado;
    }

    public int getSegundosTotalesPrueba() {
        return segundosTotalesPrueba;
    }

    public void setSegundosTotalesPrueba(int segundosTotalesPrueba) {
        this.segundosTotalesPrueba = segundosTotalesPrueba;
    }
}
