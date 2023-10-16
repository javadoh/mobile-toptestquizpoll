package com.javadoh.toptestquiz.activities.consulta.bean.premium;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lliberal on 19-07-2016.
 */
public class ReporteExamenesNubeCreados implements Serializable {

    @SerializedName("exam_design_id")
    private int examDesignId;
    @SerializedName("user_submitted_exam")
    private ArrayList<ReporteExamenesNubeSubmitted> userSubmittedExam;

    public int getExamDesignId() {
        return examDesignId;
    }

    public void setExamDesignId(int examDesignId) {
        this.examDesignId = examDesignId;
    }

    public ArrayList<ReporteExamenesNubeSubmitted> getUserSubmittedExam() {
        if(userSubmittedExam == null){
            userSubmittedExam = new ArrayList<>();
        }
        return userSubmittedExam;
    }

    public void setUserSubmittedExam(ArrayList<ReporteExamenesNubeSubmitted> userSubmittedExam) {
        this.userSubmittedExam = userSubmittedExam;
    }
}
