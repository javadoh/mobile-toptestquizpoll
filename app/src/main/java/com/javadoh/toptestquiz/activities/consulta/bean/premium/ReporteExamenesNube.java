package com.javadoh.toptestquiz.activities.consulta.bean.premium;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lliberal on 19-07-2016.
 */
public class ReporteExamenesNube implements Serializable{

    @SerializedName("id")
    private int id;
    @SerializedName("user_ppal_id")
    private int userPpalId;
    @SerializedName("exams_created")
    private ArrayList<ReporteExamenesNubeCreados> examsCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserPpalId() {
        return userPpalId;
    }

    public void setUserPpalId(int userPpalId) {
        this.userPpalId = userPpalId;
    }

    public ArrayList<ReporteExamenesNubeCreados> getExamsCreated() {
        if(examsCreated == null){
            examsCreated = new ArrayList<>();
        }
        return examsCreated;
    }

    public void setExamsCreated(ArrayList<ReporteExamenesNubeCreados> examsCreated) {
        this.examsCreated = examsCreated;
    }
}
