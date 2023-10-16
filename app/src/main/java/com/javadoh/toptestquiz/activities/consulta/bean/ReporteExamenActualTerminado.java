package com.javadoh.toptestquiz.activities.consulta.bean;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lliberal on 14-07-2016.
 */
public class ReporteExamenActualTerminado implements Serializable{

    @SerializedName("id")
    private int id;
    @SerializedName("nombres")
    private String nombres;
    @SerializedName("apellidos")
    private String apellidos;
    @SerializedName("dni")
    private String dni;
    @SerializedName("direccion")
    private String direccion;
    @SerializedName("sexo")
    private String sexo;
    @SerializedName("dia")
    private String dia;
    @SerializedName("hora_inicio")
    private String horaInicio;
    @SerializedName("total_tiempo_prueba")
    private int totalTiempoPrueba;
    @SerializedName("nota_final")
    private float notaFinal;
    @SerializedName("datos_prueba")
    private ArrayList<ReporteExamenActualPregResp> datosPrueba;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public int getTotalTiempoPrueba() {
        return totalTiempoPrueba;
    }

    public void setTotalTiempoPrueba(int totalTiempoPrueba) {
        this.totalTiempoPrueba = totalTiempoPrueba;
    }

    public float getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(float notaFinal) {
        this.notaFinal = notaFinal;
    }

    public ArrayList<ReporteExamenActualPregResp> getDatosPrueba() {
        if(datosPrueba == null){
            datosPrueba = new ArrayList<>();
        }
        return datosPrueba;
    }

    public void setDatosPrueba(ArrayList<ReporteExamenActualPregResp> datosPrueba) {
        this.datosPrueba = datosPrueba;
    }
}
