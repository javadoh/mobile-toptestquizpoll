package com.javadoh.toptestquiz.utils;

import java.util.HashMap;

/**
 * Created by luiseliberal on 26/03/16.
 */
public class Constants {
    public static final String URL_SERVIDOR_RMT_APP_DYNAMIC_TEST = "http://207.244.75.230:8000/";//"http://192.168.1.82:3000/";
    //USUARIOS
    public static final String GET_ALL_USER = "dyntestusers/getAll";
    public static final String GET_USER = "dyntestusers/getUser/";
    public static final String USER_LOGIN = "dyntestusers/loginUser";
    public static final String GET_LAST_USER = "dyntestusers/getLastUser";
    public static final String ADD_NEW_USER = "dyntestusers/newUser";
    public static final String UPDATE_USER = "dyntestusers/updateUser/";
    public static final String DELETE_USER = "dyntestusers/deleteUser/";
    //EXAMENES
    public static final String GET_ALL_EXAMS = "dyntestexams/getAll";
    public static final String GET_EXAM = "dyntestexams/getExam/";
    public static final String ADD_NEW_EXAM = "dyntestexams/newExam";
    public static final String UPDATE_EXAMS = "dyntestexams/updateExam/";
    public static final String DELETE_EXAM = "dyntestexams/deleteExam/";
    public static final String PUT_UPLOAD_IMG_DATA = "dyntestexams/copyExamImagesToServer/";
    //MAX PREGUNTAS
    public static final String MAX_QUESTIONS_EXAM_BY_USER = "dyntestexams/getMaxQuestionSizeByUser/";
    //IMAGENES CONTENEDOR
    public static final String URL_IMAGEN_PREDEFINIDA = "/drawable/main_background.jpg";
    //REPORTES EXAMENES PRESENTADOS
    public static final String GET_TOTAL_DATA_REPORT = "dyntestreports/getAllReports";
    public static final String GET_ALL_EXAMS_REPORT_BY_USER = "dyntestreports/getAllExamReportByUser/";//:idUser
    public static final String GET_REPORT_BY_USER_AND_EXAM = "dyntestreports/getExamReport";//?idUserPpal=&idExamDesign=
    public static final String NEW_DATA_EXAM_REPORT = "dyntestreports/newExamReport";
    public static final String UPDATE_EXAM_REPORT = "dyntestreports/updateExamReport";//?idUser=&idExam=
    //RUTA SERVIDOR PARA GUARDAR ARCHIVOS FISICOS COMO IMAGENES Y DOCUMENTOS
    public static final String URL_FOLDER_APP_SERVER = "http://www.javadoh.com/landings/topquiztestpoll/content/";
    //AYUDA
    public static final String FORGOT_PASSWORD_RECOVER = "dyntestusers/forgotPassHelp/";//:email /dyntestusers/forgotPassHelp/:email
    public static final String CHANGE_PASSWORDS = "dyntestusers/updatePasswords/";//:user_id y BODY JSON PUT
    //IN APP PAY
    public static final String USER_HAS_PAID_PRODUCT = "dyntestusers/updateProductStatus";//?idUser=&productId=
    public static boolean isAdsDisabled, isUnlimitedTests, isMultipleAnswers, isPremiumReports;
    public static boolean isInAppSetupCreated;
    public static boolean isPasswordAdmin;
    public static boolean internetOn;

    //PREMIUM
    private static int MAX_TESTSURVEYS_ALLOWED =  3;
    private static HashMap<Integer, String> mapaPreguntasRespuestasContestadas;

    public static HashMap<Integer, String> getMapaPreguntasRespuestasContestadas() {
        if(mapaPreguntasRespuestasContestadas == null){
            mapaPreguntasRespuestasContestadas = new HashMap<Integer, String>();
        }
        return mapaPreguntasRespuestasContestadas;
    }

    public static void setMapaPreguntasRespuestasContestadas(HashMap<Integer, String> mapaPreguntasRespuestasContestadas) {
        Constants.mapaPreguntasRespuestasContestadas = mapaPreguntasRespuestasContestadas;
    }
}
