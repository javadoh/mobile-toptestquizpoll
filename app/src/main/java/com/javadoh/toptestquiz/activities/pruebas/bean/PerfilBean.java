package com.javadoh.toptestquiz.activities.pruebas.bean;

import java.io.Serializable;

/**
 * Created by luiseliberal on 08-07-2015.
 */
public class PerfilBean implements Serializable{

    private String nombre;
    private String edad;
    private String sexo;
    private String password;
    private String repassword;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepassword() {
        return repassword;
    }

    public void setRepassword(String repassword) {
        this.repassword = repassword;
    }
}
