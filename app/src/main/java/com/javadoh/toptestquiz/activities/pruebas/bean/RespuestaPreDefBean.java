package com.javadoh.toptestquiz.activities.pruebas.bean;

import java.io.Serializable;

/**
 * Created by luiseliberal on 05-07-2015.
 */
public class RespuestaPreDefBean implements Serializable {

    private int id;
    private String respuesta;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getRespuesta(){
        return respuesta;
    }

    public void setRespuesta(String respuesta){
        this.respuesta = respuesta;
    }
}
