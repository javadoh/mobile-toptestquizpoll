package com.javadoh.toptestquiz.activities.consulta.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lliberal on 14-07-2016.
 */
public class ReporteExamenActualPregResp implements Serializable{

    @SerializedName("id_pregunta")
    private int idPregunta;
    //@SerializedName("respuesta_correcta")
    //private int respuestaCorrecta;
    @SerializedName("respuesta_seleccionada")
    private int respuestaSeleccionada;
    @SerializedName("tiempo_respuesta")
    private int tiempoRespuesta;

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    /*public int getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(int respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }*/

    public int getRespuestaSeleccionada() {
        return respuestaSeleccionada;
    }

    public void setRespuestaSeleccionada(int respuestaSeleccionada) {
        this.respuestaSeleccionada = respuestaSeleccionada;
    }

    public int getTiempoRespuesta() {
        return tiempoRespuesta;
    }

    public void setTiempoRespuesta(int tiempoRespuesta) {
        this.tiempoRespuesta = tiempoRespuesta;
    }
}
