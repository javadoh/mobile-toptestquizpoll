package com.javadoh.toptestquiz.utils.bean.exam;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by luiseliberal on 26/03/16.
 */
public class ExamUsuarioNubeConf implements Serializable{

    @SerializedName("exam_design_id")
    private int exam_design_id;
    @SerializedName("exam_title")
    private String exam_title;
    @SerializedName("exam_time_by_question")
    private double exam_time_by_question;
    @SerializedName("exam_password")
    private String exam_password;
    @SerializedName("exam_current_config")
    private ExamUsuarioNubeCurrentConf exam_current_config;

    public int getExam_designId() {
        return exam_design_id;
    }

    public void setExam_designId(int exam_design_id) {
        this.exam_design_id = exam_design_id;
    }

    public String getExam_title() {
        return exam_title;
    }

    public void setExam_title(String exam_title) {
        this.exam_title = exam_title;
    }

    public double getExam_time_by_question() {
        return exam_time_by_question;
    }

    public void setExam_time_by_question(double exam_time_by_question) {
        this.exam_time_by_question = exam_time_by_question;
    }

    public String getExam_password() {
        return exam_password;
    }

    public void setExam_password(String exam_password) {
        this.exam_password = exam_password;
    }

    public ExamUsuarioNubeCurrentConf getExamUsuarioNubeCurrentConfList() {
        return exam_current_config;
    }

    public void setExamUsuarioNubeCurrentConfList(ExamUsuarioNubeCurrentConf exam_current_config) {
        if(exam_current_config == null){
            exam_current_config = new ExamUsuarioNubeCurrentConf();
        }
        this.exam_current_config = exam_current_config;
    }
}
