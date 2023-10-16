package com.javadoh.toptestquiz.activities.registro.bean;

import java.io.Serializable;

/**
 * Created by luiseliberal on 05-07-2015.
 */
public class TotalReportesBean implements Serializable{

    private String totalPreguntas;
    private String totalRespuestasCorrectas;
    private String totalTiempoRespuesta;
    private String idUsuario;

    public String getTotalPreguntas(){
        return totalPreguntas;
    }

    public void setTotalPreguntas(String totalPreguntas){
        this.totalPreguntas = totalPreguntas;
    }

    public String getTotalRespuestasCorrectas(){
        return totalRespuestasCorrectas;
    }

    public void setTotalRespuestasCorrectas(String totalRespuestasCorrectas){
        this.totalRespuestasCorrectas = totalRespuestasCorrectas;
    }

    public String getTotalTiempoRespuesta(){
        return totalTiempoRespuesta;
    }

    public void setTotalTiempoRespuesta(String totalTiempoRespuesta){
        this.totalTiempoRespuesta = totalTiempoRespuesta;
    }

    public String getIdUsuario(){
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario){
        this.idUsuario = idUsuario;
    }
}
