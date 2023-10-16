package com.javadoh.toptestquiz.activities.consulta.bean;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by luiseliberal on 19-07-2015.
 */
public class ReporteAuxBean {

    private int idRow;
    private String encuestador;
    private int idExamen;
    private HashMap<String, ReportePreguntaLinealAuxBean> preguntaIdMapaPorLinea;
    private HashMap<String, Integer> tiempoRespuestaMapaPorLinea;
    private HashMap<String, Integer> respuestaIdMapaPorLinea;
    //DATOS PERFIL ENCUESTADO
    private String dia;
    private String hora;
    private String locacion;
    private int genero;
    private int edad;
    private int claseUsuario;
    private int comuna;
    private int frecuencia;
    private int motivo;
    private int ocupacion;
    private int otros1;
    private int otros2;
    private int otros3;
    private int otros4;
    private int otros5;
    private String nombresEncuestado;
    private String apellidosEncuestado;

    public int getIdRow() {
        return idRow;
    }

    public void setIdRow(int idRow) {
        this.idRow = idRow;
    }

    public String getEncuestador() {
        return encuestador;
    }

    public void setEncuestador(String encuestador) {
        this.encuestador = encuestador;
    }

    public int getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(int idExamen) {
        this.idExamen = idExamen;
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

    public int getClaseUsuario() {
        return claseUsuario;
    }

    public void setClaseUsuario(int claseUsuario) {
        this.claseUsuario = claseUsuario;
    }

    public int getComuna() {
        return comuna;
    }

    public void setComuna(int comuna) {
        this.comuna = comuna;
    }

    public int getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(int frecuencia) {
        this.frecuencia = frecuencia;
    }

    public int getMotivo() {
        return motivo;
    }

    public void setMotivo(int motivo) {
        this.motivo = motivo;
    }

    public int getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(int ocupacion) {
        this.ocupacion = ocupacion;
    }

    public int getOtros1() {
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
    }

    public HashMap<String, ReportePreguntaLinealAuxBean> getPreguntaIdMapaPorLinea() {
        if(preguntaIdMapaPorLinea == null){
            preguntaIdMapaPorLinea = new LinkedHashMap<>();
        }
        return preguntaIdMapaPorLinea;
    }

    public void setPreguntaIdMapaPorLinea(HashMap<String, ReportePreguntaLinealAuxBean> preguntaIdMapaPorLinea) {
        if(preguntaIdMapaPorLinea == null){
            preguntaIdMapaPorLinea = new LinkedHashMap<>();
        }
        this.preguntaIdMapaPorLinea = preguntaIdMapaPorLinea;
    }

    public HashMap<String, Integer> getTiempoRespuestaMapaPorLinea() {
        if(tiempoRespuestaMapaPorLinea == null){tiempoRespuestaMapaPorLinea = new LinkedHashMap<>();}
        return tiempoRespuestaMapaPorLinea;
    }

    public void setTiempoRespuestaMapaPorLinea(HashMap<String, Integer> tiempoRespuestaMapaPorLinea) {
        if(tiempoRespuestaMapaPorLinea == null){tiempoRespuestaMapaPorLinea = new LinkedHashMap<>();}
        this.tiempoRespuestaMapaPorLinea = tiempoRespuestaMapaPorLinea;
    }

    public HashMap<String, Integer> getRespuestaIdMapaPorLinea() {
        if(respuestaIdMapaPorLinea == null){
            respuestaIdMapaPorLinea = new LinkedHashMap<>();
        }
        return respuestaIdMapaPorLinea;
    }

    public void setRespuestaIdMapaPorLinea(HashMap<String, Integer> respuestaIdMapaPorLinea) {
        if(respuestaIdMapaPorLinea == null){respuestaIdMapaPorLinea = new LinkedHashMap<>();}
        this.respuestaIdMapaPorLinea = respuestaIdMapaPorLinea;
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
}
