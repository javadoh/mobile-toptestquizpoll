package com.javadoh.toptestquiz.utils.bean.exam;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by luiseliberal on 26/03/16.
 */
public class ExamenUsuarioCurrentPreguntas implements Serializable{

    @SerializedName("id")
    private int id;
    @SerializedName("desc")
    private String desc;
    @SerializedName("imgpregpath")
    private String imgPregPathServer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImgPregPathServer() {
        return imgPregPathServer;
    }

    public void setImgPregPathServer(String imgPregPathServer) {
        this.imgPregPathServer = imgPregPathServer;
    }
}
