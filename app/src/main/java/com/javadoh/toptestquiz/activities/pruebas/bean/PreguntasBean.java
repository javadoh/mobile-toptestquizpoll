package com.javadoh.toptestquiz.activities.pruebas.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by luiseliberal on 04-07-2015.
 */
public class PreguntasBean implements Serializable {

    //ID DE PREGUNTA PREDEFINIDA
    private int idPregunta;
    //PREGUNTA PREDEFINIDA
    private String textoPregunta;
    //RUTA DEL ARCHIVO DE IMAGEN EN EL SISTEMA
    private String pathImagenPregunta;
    //RESPUESTA EXITOSA PREDEFINIDA
    private String respuesta;

    private ArrayList<RespuestaPreDefBean> respuestasPreDef;

    public ArrayList<RespuestaPreDefBean> getRespuestasPreDef() {
        if (respuestasPreDef == null){respuestasPreDef = new ArrayList<RespuestaPreDefBean>();}
        return respuestasPreDef;
    }

    public void setRespuestasPreDef(ArrayList<RespuestaPreDefBean> respuestasPreDef) {
        this.respuestasPreDef = respuestasPreDef;
    }

    public int getIdPregunta(){
        return this.idPregunta;
    }

    public void setIdPregunta(int idPregunta){
        this.idPregunta = idPregunta;
    }

    public String getTextoPregunta(){
        return this.textoPregunta;
    }

    public void setTextoPregunta(String textoPregunta){
        this.textoPregunta = textoPregunta;
    }

    public String getPathImagenPregunta(){
        return pathImagenPregunta;
    }

    public void setPathImagenPregunta(String pathImagenPregunta){
        this.pathImagenPregunta = pathImagenPregunta;
    }

    public String getRespuesta(){
        return this.respuesta;
    }

    public void setRespuesta(String respuesta){
        this.respuesta = respuesta;
    }
}
