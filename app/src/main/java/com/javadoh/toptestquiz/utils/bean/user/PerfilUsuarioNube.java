package com.javadoh.toptestquiz.utils.bean.user;

import java.io.Serializable;

/**
 * Created by luiseliberal on 26/03/16.
 */
public class PerfilUsuarioNube implements Serializable{

    private int id;
    private int user_id;
    private String user_rut;
    private String user_email;
    private String user_login;
    private String user_password;
    private String user_secondary_password;
    //PRODUCT 1
    private boolean user_ads_disabled;
    //PRODUCT 2
    private boolean user_unlimited_exams;
    //PRODUCT 3
    private boolean user_exams_multi_answers;
    //PRODUCT 4
    private boolean user_has_premium_reports;
    private String exp_date_unlimited_tests;
    private String exp_date_premium_reports;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_rut() {
        return user_rut;
    }

    public void setUser_rut(String user_rut) {
        this.user_rut = user_rut;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_login() {
        return user_login;
    }

    public void setUser_login(String user_login) {
        this.user_login = user_login;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_secondary_password() {
        return user_secondary_password;
    }

    public void setUser_secondary_password(String user_secondary_password) {
        this.user_secondary_password = user_secondary_password;
    }

    public boolean isUser_ads_disabled() {
        return user_ads_disabled;
    }

    public void setUser_ads_disabled(boolean user_ads_disabled) {
        this.user_ads_disabled = user_ads_disabled;
    }

    public boolean isUser_unlimited_exams() {
        return user_unlimited_exams;
    }

    public void setUser_unlimited_exams(boolean user_unlimited_exams) {
        this.user_unlimited_exams = user_unlimited_exams;
    }

    public boolean isUser_exams_multi_answers() {
        return user_exams_multi_answers;
    }

    public void setUser_exams_multi_answers(boolean user_exams_multi_answers) {
        this.user_exams_multi_answers = user_exams_multi_answers;
    }

    public boolean isUser_has_premium_reports() {
        return user_has_premium_reports;
    }

    public void setUser_has_premium_reports(boolean user_has_premium_reports) {
        this.user_has_premium_reports = user_has_premium_reports;
    }

    public String getExp_date_unlimited_tests() {
        return exp_date_unlimited_tests;
    }

    public void setExp_date_unlimited_tests(String exp_date_unlimited_tests) {
        this.exp_date_unlimited_tests = exp_date_unlimited_tests;
    }

    public String getExp_date_premium_reports() {
        return exp_date_premium_reports;
    }

    public void setExp_date_premium_reports(String exp_date_premium_reports) {
        this.exp_date_premium_reports = exp_date_premium_reports;
    }
}
