package com.javadoh.toptestquiz.utils.bean.exam;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luiseliberal on 26/03/16.
 */
public class ExamUsuarioNubeCurrentConf implements Serializable{

    @SerializedName("answers")
    private List<ExamenUsuarioCurrentRespuestas> answers;
    @SerializedName("questions")
    private List<ExamenUsuarioCurrentPreguntas> questions;

    public List<ExamenUsuarioCurrentPreguntas> getQuestions() {
        if(questions == null){
            questions = new ArrayList<ExamenUsuarioCurrentPreguntas>();}
        return questions;
    }

    public void setQuestions(List<ExamenUsuarioCurrentPreguntas> questions) {
        this.questions = questions;
    }

    public List<ExamenUsuarioCurrentRespuestas> getAnswers() {
        if(answers == null){
            answers = new ArrayList<ExamenUsuarioCurrentRespuestas>();}
        return answers;
    }

    public void setAnswers(List<ExamenUsuarioCurrentRespuestas> answers) {
        this.answers = answers;
    }
}
