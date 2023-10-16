package com.javadoh.toptestquiz.utils.bean.exam;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luiseliberal on 26/03/16.
 */
public class ExamenUsuarioCurrentRespuestas implements Serializable {

    @SerializedName("question_id")
    private int question_id;
    @SerializedName("answers")
    private List<ExamUsuarioCurrentRespDetalle> answers;
    @SerializedName("correct_answer")
    private int correct_answer;

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public List<ExamUsuarioCurrentRespDetalle> getAnswers() {

        if(answers == null){
            answers = new ArrayList<ExamUsuarioCurrentRespDetalle>();}
        return answers;
    }

    public void setAnswers(List<ExamUsuarioCurrentRespDetalle> answers) {
        if(answers == null){
            answers = new ArrayList<ExamUsuarioCurrentRespDetalle>();}
        this.answers = answers;
    }

    public int getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(int correct_answer) {
        this.correct_answer = correct_answer;
    }
}
