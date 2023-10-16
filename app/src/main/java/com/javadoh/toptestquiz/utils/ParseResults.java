package com.javadoh.toptestquiz.utils;

import android.content.Context;
import android.util.Log;

import com.javadoh.toptestquiz.activities.consulta.bean.premium.ReporteExamenesNube;
import com.javadoh.toptestquiz.utils.bean.DynamicTestResponse;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioNube;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioNube;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by luiseliberal on 27-09-2015.
 */
public class ParseResults {

    private static final String TAG = ParseResults.class.getName();
    private Context context;
    private DynamicTestResponse responsePostLogin;
    private static PerfilUsuarioNube usuarioPerfilNube;
    private static ExamenUsuarioNube examenUsuarioNube;


    public ParseResults(DynamicTestResponse responsePostLogin, Context context){
        this.responsePostLogin = responsePostLogin;
        //this.usuarioPerfilNube = usuarioPerfilNube;
        //this.examenUsuarioNube = examenUsuarioNube;
        this.context = context;

    }

    public DynamicTestResponse reponsePostLogin (String result){

        try {
            responsePostLogin = new DynamicTestResponse();

            if(result.contains("[")){
                result = result.replace("[", "");
                result = result. replace("]", "");
            }
            Type listType = new TypeToken<List<DynamicTestResponse>>() {}.getType();
            responsePostLogin = new Gson().fromJson(result.toString(), listType);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error: ", e);
        }

        return responsePostLogin;
    }

    public PerfilUsuarioNube parseResultPerfilUsuario (String result) {

        try {
            usuarioPerfilNube = new PerfilUsuarioNube();

            if(result.contains("[")){
                result = result.replace("[", "");
                result = result. replace("]", "");
            }

            Type listType = new TypeToken<PerfilUsuarioNube>() {}.getType();
            usuarioPerfilNube = new Gson().fromJson(result.toString(), listType);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error: ", e);
        }

        return usuarioPerfilNube;
    }


    public ExamenUsuarioNube parseResultExamConfig(String result) {

        try {
            examenUsuarioNube = new ExamenUsuarioNube();

            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<ExamenUsuarioNube>() {}.getType();
            examenUsuarioNube = gson.fromJson(result, listType);

            Log.d(TAG, "Check: "+examenUsuarioNube.getExam_conf().size());

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error: ", e);
        }

        return examenUsuarioNube;
    }

    //###################### SECCION DE REPORTES PREMIUM #####################//
    public ReporteExamenesNube parseReporteExamUsuPremium(String result){

        ReporteExamenesNube listaExamenesPorUsuarioRetorno;
        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<ReporteExamenesNube>() {}.getType();
        listaExamenesPorUsuarioRetorno = gson.fromJson(result, listType);

        return listaExamenesPorUsuarioRetorno;
    }
}
