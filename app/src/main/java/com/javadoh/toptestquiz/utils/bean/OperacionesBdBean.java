package com.javadoh.toptestquiz.utils.bean;

import java.io.Serializable;

/**
 * Created by LUIS-EXTERNO on 08-07-2015.
 */
public class OperacionesBdBean implements Serializable{


    private int idRow;
    private String dniEncuestador;
    private int userIdEncuestador;
    private String userLoginEncuestador;
    private int idExamen;
    private int idPregunta;
    private String pregunta;
    private int idRespuesta;
    private String respuesta;
    private int tiempoDeRespuesta;
    //DATOS PERFIL ENCUESTADO
    private String dniEncuestado;
    private String nombresEncuestado;
    private String apellidosEncuestado;
    private String dia;
    private String hora;
    private String locacion;
    private int genero;
    private int edad;
    private int comuna;
    private int ocupacion;
    /*private int otros1;
    private int otros2;
    private int otros3;
    private int otros4;
    private int otros5;*/

    public int getIdRow() {
        return idRow;
    }

    public void setIdRow(int idRow) {
        this.idRow = idRow;
    }

    public String getDniEncuestador() {
        return dniEncuestador;
    }

    public void setDniEncuestador(String dniEncuestador) {
        this.dniEncuestador = dniEncuestador;
    }

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

    public int getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(int idExamen) {
        this.idExamen = idExamen;
    }

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public int getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(int idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public int getTiempoDeRespuesta() {
        return tiempoDeRespuesta;
    }

    public void setTiempoDeRespuesta(int tiempoDeRespuesta) {
        this.tiempoDeRespuesta = tiempoDeRespuesta;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getLocacion() {
        return locacion;
    }

    public void setLocacion(String locacion) {
        this.locacion = locacion;
    }

    public int getGenero() {
        return genero;
    }

    public void setGenero(int genero) {
        this.genero = genero;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public int getComuna() {
        return comuna;
    }

    public void setComuna(int comuna) {
        this.comuna = comuna;
    }

    public int getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(int ocupacion) {
        this.ocupacion = ocupacion;
    }

    /*public int getOtros1() {
        return otros1;
    }

    public void setOtros1(int otros1) {
        this.otros1 = otros1;
    }

    public int getOtros2() {
        return otros2;
    }

    public void setOtros2(int otros2) {
        this.otros2 = otros2;
    }

    public int getOtros3() {
        return otros3;
    }

    public void setOtros3(int otros3) {
        this.otros3 = otros3;
    }

    public int getOtros4() {
        return otros4;
    }

    public void setOtros4(int otros4) {
        this.otros4 = otros4;
    }

    public int getOtros5() {
        return otros5;
    }

    public void setOtros5(int otros5) {
        this.otros5 = otros5;
    }*/
}
