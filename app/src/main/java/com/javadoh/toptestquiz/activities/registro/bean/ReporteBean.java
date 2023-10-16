package com.javadoh.toptestquiz.activities.registro.bean;

import java.io.Serializable;

/**
 * Created by luiseliberal on 05-07-2015.
 */
public class ReporteBean implements Serializable{

    private String idPregunta;
    private String idUsuario;
    private String idRespuesta;
    private String tiempoRespuesta;

    public String getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(String idPregunta){
        this.idPregunta = idPregunta;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario){
        this.idUsuario = idUsuario;
    }

    public String getIdRespuesta(){
        return idRespuesta;
    }

    public void setIdRespuesta(String idRespuesta){
        this.idRespuesta = idRespuesta;
    }

    public String getTiempoRespuesta(){
        return tiempoRespuesta;
    }

    public void setTiempoRespuesta(String tiempoRespuesta){
        this.tiempoRespuesta = tiempoRespuesta;
    }
}
