package com.javadoh.toptestquiz.utils.bean.exam;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by luiseliberal on 26/03/16.
 */
public class ExamUsuarioCurrentRespDetalle implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("desc")
    private String desc;

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
}
