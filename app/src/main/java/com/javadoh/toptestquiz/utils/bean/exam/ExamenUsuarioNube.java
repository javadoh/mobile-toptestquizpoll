package com.javadoh.toptestquiz.utils.bean.exam;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luiseliberal on 26/03/16.
 */
public class ExamenUsuarioNube implements Serializable{

    @SerializedName("_id")
    private String _id;

    @SerializedName("user_id")
    private int user_id;
    @SerializedName("exam_conf")
    private List<ExamUsuarioNubeConf> exam_conf;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public List<ExamUsuarioNubeConf> getExam_conf() {
        return exam_conf;
    }

    public void setExam_conf(List<ExamUsuarioNubeConf> exam_conf) {
        this.exam_conf = exam_conf;
    }
}