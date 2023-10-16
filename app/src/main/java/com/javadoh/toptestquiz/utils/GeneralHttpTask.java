package com.javadoh.toptestquiz.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.javadoh.toptestquiz.R;
import com.javadoh.toptestquiz.activities.consulta.ReportePremiumActivity;
import com.javadoh.toptestquiz.activities.consulta.bean.premium.ReporteExamenesNube;
import com.javadoh.toptestquiz.activities.PostLogin;
import com.javadoh.toptestquiz.utils.bean.DynamicTestResponse;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioNube;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioNube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by luiseliberal on 09/04/16.
 */
public class GeneralHttpTask extends AsyncTask<String, Void, Integer> {

    private static final String TAG = GeneralHttpTask.class.getName();
    ProgressBar progressBar;
    Context context;
    DynamicTestResponse dynamicTestResponse;
    PerfilUsuarioNube usuarioPerfilNube;
    ExamenUsuarioNube examenUsuarioNube;
    StringBuilder response = null;
    String flagCall = "";
    JSONObject jsonReqObj;
    Dialog dialog;
    //PARA IMAGENES
    HashMap<String,String> mapaImagenesBase64;
    //PARA REPORTES PREMIUM
    ReporteExamenesNube reporteExamenesNube;
    DialogInterface dialogInterface;
    String examenSeleccionadoReporte, passwordUsedForLogin;
    Activity activity;

    //TRATO DE USUARIOS Y EXAMENES
    public GeneralHttpTask (Context context, ProgressBar progressBar, DynamicTestResponse dynamicTestResponse,
                            String flagCall, JSONObject jsonReqObj, PerfilUsuarioNube usuarioPerfilNube){
        this.context = context;
        this.progressBar = progressBar;
        this.dynamicTestResponse = dynamicTestResponse;
        this.flagCall = flagCall;
        this.jsonReqObj = jsonReqObj;
        this.usuarioPerfilNube = usuarioPerfilNube;
    }
    //OCUPADO PARA LA INSTANCIACION Y TRANSPORTE DE IMAGENES CODIFICADAS JUNTO CON DATOS DE EXAMENES A GUARDAR
    public GeneralHttpTask (Context context, ProgressBar progressBar, DynamicTestResponse dynamicTestResponse,
                            String flagCall, JSONObject jsonReqObj, PerfilUsuarioNube usuarioPerfilNube, HashMap<String,String> mapaImagenesBase64, Activity activity){
        this.context = context;
        this.progressBar = progressBar;
        this.dynamicTestResponse = dynamicTestResponse;
        this.flagCall = flagCall;
        this.jsonReqObj = jsonReqObj;
        this.usuarioPerfilNube = usuarioPerfilNube;
        this.mapaImagenesBase64 = mapaImagenesBase64;
        this.activity = activity;
    }

    public GeneralHttpTask (Context context, ProgressBar progressBar, DynamicTestResponse dynamicTestResponse,
                            String flagCall, JSONObject jsonReqObj, PerfilUsuarioNube usuarioPerfilNube, Dialog dialog){
        this.context = context;
        this.progressBar = progressBar;
        this.dynamicTestResponse = dynamicTestResponse;
        this.flagCall = flagCall;
        this.jsonReqObj = jsonReqObj;
        this.usuarioPerfilNube = usuarioPerfilNube;
        this.dialog = dialog;
    }

    public GeneralHttpTask (Context context, ProgressBar progressBar, DynamicTestResponse dynamicTestResponse,
                            String flagCall, JSONObject jsonReqObj, PerfilUsuarioNube usuarioPerfilNube, DialogInterface dialogInterface){
        this.context = context;
        this.progressBar = progressBar;
        this.dynamicTestResponse = dynamicTestResponse;
        this.flagCall = flagCall;
        this.jsonReqObj = jsonReqObj;
        this.usuarioPerfilNube = usuarioPerfilNube;
        this.dialogInterface = dialogInterface;
    }

    //TRATO DE REPORTES PREMIUM
    public GeneralHttpTask (Context context, ProgressBar progressBar, DynamicTestResponse dynamicTestResponse,
                            String flagCall, JSONObject jsonReqObj, PerfilUsuarioNube usuarioPerfilNube,
                            ExamenUsuarioNube examenUsuarioNube, ReporteExamenesNube reporteExamenesNube,
                            DialogInterface dialogInterface, String examenSeleccionadoReporte, String passwordUsedForLogin){
        this.context = context;
        this.progressBar = progressBar;
        this.dynamicTestResponse = dynamicTestResponse;
        this.flagCall = flagCall;
        this.jsonReqObj = jsonReqObj;
        this.usuarioPerfilNube = usuarioPerfilNube;
        this.examenUsuarioNube = examenUsuarioNube;
        this.reporteExamenesNube = reporteExamenesNube;
        this.examenSeleccionadoReporte = examenSeleccionadoReporte;
        this.dialogInterface = dialogInterface;
        this.passwordUsedForLogin = passwordUsedForLogin;
    }

    public GeneralHttpTask (Context context, ProgressBar progressBar, DynamicTestResponse dynamicTestResponse,
                            String flagCall, JSONObject jsonReqObj, PerfilUsuarioNube usuarioPerfilNube, Activity activity){
        this.context = context;
        this.progressBar = progressBar;
        this.dynamicTestResponse = dynamicTestResponse;
        this.flagCall = flagCall;
        this.jsonReqObj = jsonReqObj;
        this.usuarioPerfilNube = usuarioPerfilNube;
        this.activity = activity;
    }

    public GeneralHttpTask (Context context, ProgressBar progressBar, DynamicTestResponse dynamicTestResponse,
                            String flagCall, JSONObject jsonReqObj, PerfilUsuarioNube usuarioPerfilNube,
                            ExamenUsuarioNube examenUsuarioNube, Activity activity, String passwordUsedForLogin){
        this.context = context;
        this.progressBar = progressBar;
        this.dynamicTestResponse = dynamicTestResponse;
        this.flagCall = flagCall;
        this.jsonReqObj = jsonReqObj;
        this.usuarioPerfilNube = usuarioPerfilNube;
        this.activity = activity;
        this.examenUsuarioNube = examenUsuarioNube;
        this.passwordUsedForLogin = passwordUsedForLogin;
    }

    //OCUPADO PARA LA INSTANCIACION Y TRANSPORTE DE IMAGENES CODIFICADAS JUNTO CON DATOS DE EXAMENES A GUARDAR
    public GeneralHttpTask (Context context, ProgressBar progressBar, DynamicTestResponse dynamicTestResponse,
                            String flagCall, JSONObject jsonReqObj, PerfilUsuarioNube usuarioPerfilNube,
                            HashMap<String,String> mapaImagenesBase64, Activity activity, String passwordUsedForLogin){
        this.context = context;
        this.progressBar = progressBar;
        this.dynamicTestResponse = dynamicTestResponse;
        this.flagCall = flagCall;
        this.jsonReqObj = jsonReqObj;
        this.usuarioPerfilNube = usuarioPerfilNube;
        this.mapaImagenesBase64 = mapaImagenesBase64;
        this.activity = activity;
        this.passwordUsedForLogin = passwordUsedForLogin;
    }

    @Override
    public void onPreExecute() {

        ((Activity)context).setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public Integer doInBackground(String... params) {

        Integer result;
        HttpURLConnection urlConnection;
        try {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            //MANEJO DE TIMEOUT
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);

                //SET PARAMETROS DE LLAMADO REST
                if("UPDATE_EXAMS_USER".equalsIgnoreCase(flagCall) || "UPDATE_USER".equalsIgnoreCase(flagCall) ||
                        "UPDATE_EXAM_REPORT".equalsIgnoreCase(flagCall) || "UPLOAD_IMAGES_EXAM".equalsIgnoreCase(flagCall)
                        || "REGISTRO_EXAMEN_TERMINADO_REPORTE".equalsIgnoreCase(flagCall)
                        || "CHANGE_PASSWORD".equalsIgnoreCase(flagCall) || "INAPPPAY".equalsIgnoreCase(flagCall)){
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestMethod("PUT");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestProperty("Accept", "application/json");

                    byte[] outputBytes = jsonReqObj.toString().getBytes("UTF-8");
                    OutputStream os = urlConnection.getOutputStream();
                    os.write(outputBytes);
                    os.flush();
                    os.close();

                }else if("ADD_NEW_EXAM".equalsIgnoreCase(flagCall) || "ADD_NEW_USER".equalsIgnoreCase(flagCall) ||
                        "NEW_DATA_EXAM_REPORT".equalsIgnoreCase(flagCall)){
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);//HACE SET DE METODO POST IMPLICITAMENTE
                    urlConnection.setUseCaches(false);

                    byte[] outputBytes = jsonReqObj.toString().getBytes("UTF-8");
                    OutputStream os = urlConnection.getOutputStream();
                    os.write(outputBytes);
                    os.flush();
                    os.close();
                }

            int statusCode = urlConnection.getResponseCode();

            // 200 REPRESENTA HTTP OK
            if (statusCode == 200 || statusCode == 202) {
                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                this.response = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    this.response.append(line);
                }

                //INVOCACION DE CLASE DE PARSEO GENERAL

                Log.d(TAG, "############################# RESPONSE JSON: " + response.toString());

                ParseResults parseResults = new ParseResults(dynamicTestResponse, context);

                //PARSEAMOS LOS DATOS DE LOS USUARIOS EXISTENTES O DE NO EXISTIR TRATO ESPECIAL
                if ("LOGIN".equalsIgnoreCase(flagCall) || "GET_LAST_USER".equalsIgnoreCase(flagCall)
                        || "FORGOT_PASSWORD".equalsIgnoreCase(flagCall)) {
                    this.usuarioPerfilNube = parseResults.parseResultPerfilUsuario(response.toString());

                //PARSEAMOS LOS DATOS DE LOS EXAMENES YA CREADOS POR EL USUARIO PRINCIPAL
                }else if(flagCall.equalsIgnoreCase("DATA_EXAM")) {
                        Log.d(TAG, "Test perfilUsuario: " + usuarioPerfilNube.getUser_id());

                    if(response.toString() != "" || response.toString().length() != 0) {
                        this.examenUsuarioNube = parseResults.parseResultExamConfig(response.toString());
                    }
                //PARSEAMOS LOS DATOS DE LOS EXAMENES YA PRESENTADOS POR USUARIO Y POR NUMERO DE EXAMEN
                }else if(flagCall.equalsIgnoreCase("DATA_EXAMS_REPORTS")){
                    if(response.toString() != "" || response.toString().length() != 0) {
                        this.reporteExamenesNube = parseResults.parseReporteExamUsuPremium(response.toString());
                    }
                }else{
                    this.dynamicTestResponse = parseResults.reponsePostLogin(this.response.toString());
                }

                result = 1; //EXITOSO
                Log.d(TAG, "Response: "+response);

                if (response.equals("") || response.length() == 0){
                    result = 2;
                }

            } else {
                result = 3; //FALLO AL OBTENER LA DATA
                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                this.response = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    this.response.append(line);
                }
            }

        } catch (SocketTimeoutException | ConnectException e)
        {
            e.printStackTrace();
            result = 3;
            if (TextUtils.isEmpty(response)){
                response = new StringBuilder();
                response.append("Lo sentimos, hubo un problema conectandose con el servidor.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getLocalizedMessage());
            throw new RuntimeException("Ocurrió un error: ", e.getCause());
        }

        return result; //"Failed to fetch data!";
    }


    @Override
    protected void onPostExecute(Integer result) {
        //DESCARGA COMPLETA , HACEMOS UPDATE DE LA UI
        progressBar.setVisibility(View.GONE);

        if (result == 1) {

            if ("LOGIN".equalsIgnoreCase(flagCall)) {

                ProgressBar progressBar = new ProgressBar(context);
                DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();
                String url = "";
                int user_id = usuarioPerfilNube.getUser_id();
                try {
                    this.jsonReqObj.put("user_id", user_id);

                    //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                    url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.GET_EXAM+user_id;

                    new GeneralHttpTask(context, progressBar, dynamicTestResponse, "DATA_EXAM", jsonReqObj, usuarioPerfilNube).execute(url);

                }catch (Exception e){
                  e.printStackTrace();
                    Log.d(TAG, "Error",e);
                }

            }else if("DATA_EXAM".equalsIgnoreCase(flagCall)){

                Intent intentPostLogin = new Intent();

                try {
                    intentPostLogin.setClass(context.getApplicationContext(), PostLogin.class);
                    intentPostLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentPostLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentPostLogin.putExtra("SESSION_USER", usuarioPerfilNube);
                    intentPostLogin.putExtra("SESSION_EXAMS", examenUsuarioNube);
                    intentPostLogin.putExtra("PASSWORD_USED", this.jsonReqObj.getString("password_used"));
                    context.getApplicationContext().startActivity(intentPostLogin);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "Error al iniciar postlogin: ",e);
                }

            }

            /*else if("GET_LAST_USER".equalsIgnoreCase(flagCall)) {

                ProgressBar progressBar = new ProgressBar(context);
                DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();
                String url = "";
                int user_id = usuarioPerfilNube.getUser_id()+1;//OJO AQUI CON ESTO
                try {
                    this.jsonReqObj.put("user_id", user_id);

                    //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                    url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.ADD_NEW_USER;

                    new GeneralHttpTask(context, progressBar, dynamicTestResponse, "ADD_NEW_USER", this.jsonReqObj,
                            usuarioPerfilNube, dialog).execute(url);

                }catch (JSONException jse){
                    Log.d(TAG, "Error: ",jse);
                    jse.printStackTrace();
                }
                //MemoryBean.setPerfilUsuarioNube(usuarioPerfilNube);

            } */
            else if("ADD_NEW_USER".equalsIgnoreCase(flagCall)) {

                try {
                    //ENVIO DE EMAIL CON LOS DATOS DEL USUARIO
                    new SendMailTask((Activity)context).execute("TestMy Javadoh",
                            "s1zha8to.", this.jsonReqObj.getString("user_email"), Html.fromHtml(context.getString(R.string.asuntoEmailSignUpSend)),
                            Html.fromHtml(context.getString(R.string.bodyEmailSignUpSend) + this.jsonReqObj.getString("user_login") +
                                    context.getString(R.string.bodyEmailSignUpSend2) + this.jsonReqObj.getString("user_password") +
                                    context.getString(R.string.bodyEmailSignUpSend3) + this.jsonReqObj.getString("user_secondary_password")), "");

                    //AVISO A LA UI
                    Toast.makeText(context, context.getResources().getString(R.string.msgUserRegistDone),
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, context.getString(R.string.errorEmailEnvio) + usuarioPerfilNube.getUser_login(), e);
                    //AVISO A LA UI
                    Toast.makeText(context, context.getString(R.string.errorEmailEnvio) + usuarioPerfilNube.getUser_login(),
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            } else if("UPDATE_EXAMS_USER".equalsIgnoreCase(flagCall) || "ADD_NEW_EXAM".equalsIgnoreCase(flagCall)){

                //PROCEDEMOS CON LA SUBIDA DE LAS IMAGENES POR SEPARADO
                ProgressBar progressBar = new ProgressBar(context);
                DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();
                String url;
                JSONObject objetoImagenesUpload;
                JSONObject objetoAux;
                JSONArray arregloImagenesUpload = new JSONArray();
                try {
                    Log.d(TAG, "Json ----> "+jsonReqObj);
                    //TITULO DEL EXAMEN VA EN EL NUEVO OBJETO JSON
                    objetoImagenesUpload = new JSONObject();
                    if("ADD_NEW_EXAM".equalsIgnoreCase(flagCall)) {
                        JSONArray innerArray = jsonReqObj.getJSONArray("exam_conf");
                        JSONObject examCurrentConfJson = innerArray.getJSONObject(0);
                        objetoImagenesUpload.put("examtitle", examCurrentConfJson.optString("exam_title").toString());//.optJSONObject("exam_conf").

                    }else if("UPDATE_EXAMS_USER".equalsIgnoreCase(flagCall)){
                        objetoImagenesUpload.put("examtitle", jsonReqObj.optString("exam_title").toString());
                    }

                    //LLENAMOS EL NUEVO ARREGLO DE STRINGS ENCODED DE IMAGENES PARA IR A LA CARPETA DEL SERVIDOR
                        for(Map.Entry<String, String> e : mapaImagenesBase64.entrySet()) {

                            objetoAux = new JSONObject();
                            objetoAux.put("nombrearchivo", e.getKey());
                            objetoAux.put("stringencoded", e.getValue());
                            arregloImagenesUpload.put(objetoAux);
                        }

                            objetoImagenesUpload.put("imgList64", arregloImagenesUpload);

                    //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                    url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.PUT_UPLOAD_IMG_DATA+usuarioPerfilNube.getUser_login();

                    Toast.makeText(context, context.getResources().getString(R.string.msgPreCreateTestSuccess), Toast.LENGTH_LONG).show();

                    new GeneralHttpTask(context, progressBar, dynamicTestResponse, "UPLOAD_IMAGES_EXAM", objetoImagenesUpload, usuarioPerfilNube, examenUsuarioNube, this.activity, passwordUsedForLogin).execute(url);

                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "Error", e);
                    Toast.makeText(context, context.getResources().getString(R.string.msgCreateTestError)+
                            usuarioPerfilNube.getUser_login(), Toast.LENGTH_SHORT).show();

                    //CERRAMOS LA ACTIVIDAD DE CREAR PRUEBAS
                    Intent intent = new Intent(this.activity, PostLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("SESSION_USER", usuarioPerfilNube);
                    intent.putExtra("SESSION_EXAMS", examenUsuarioNube);
                    intent.putExtra("PASSWORD_USED", passwordUsedForLogin);

                    this.activity.startActivity(intent);
                    this.activity.finish();
                }

            }else if("REGISTRO_EXAMEN_TERMINADO_REPORTE".equalsIgnoreCase(flagCall)){

                Toast.makeText(context, "Se ha enviado la data de la prueba presentada al servidor... " +
                        "Muchas gracias por tu tiempo! "+usuarioPerfilNube.getUser_login(), Toast.LENGTH_SHORT).show();

            }else if("UPLOAD_IMAGES_EXAM".equalsIgnoreCase(flagCall)){

                //CERRAMOS LA ACTIVIDAD DE CREAR PRUEBAS
                Intent intent = new Intent(this.activity, PostLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("SESSION_USER", usuarioPerfilNube);
                intent.putExtra("SESSION_EXAMS", examenUsuarioNube);
                intent.putExtra("PASSWORD_USED", passwordUsedForLogin);
                intent.putExtra("MSG_CREATE_TEST", context.getResources().getString(R.string.msgCreateTestSuccess) +" "+
                        usuarioPerfilNube.getUser_login());

                this.activity.startActivity(intent);
                this.activity.finish();

                //SI ES CORRECTA LA SUBIDA DE LAS IMAGENES DESPUES DE LA DATA DE LOS EXAMENES REINVOCAMOS LOS DATOS DE LOS EXAMENES
                //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                //String url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.GET_EXAM+usuarioPerfilNube.getUser_id();
                //new GeneralHttpTask(context, progressBar, dynamicTestResponse, "DATA_EXAM_AFTER_CREATE_TEST", jsonReqObj, usuarioPerfilNube, this.activity).execute(url);

            }else if("DATA_EXAMS_REPORTS".equalsIgnoreCase(flagCall)){

                Intent intentReportePremium = new Intent(context.getApplicationContext(), ReportePremiumActivity.class);

                intentReportePremium.putExtra("SESSION_USER", usuarioPerfilNube);
                intentReportePremium.putExtra("SESSION_EXAMS", examenUsuarioNube);
                intentReportePremium.putExtra("SESSION_REPORTE_NUBE", reporteExamenesNube);
                intentReportePremium.putExtra("SESSION_EXAM_SELECTED", examenSeleccionadoReporte);
                intentReportePremium.putExtra("PASSWORD_USED", passwordUsedForLogin);

                        context.startActivity(intentReportePremium);
                dialogInterface.dismiss();
            }
            else if("FORGOT_PASSWORD".equalsIgnoreCase(flagCall)){

                try {
                    if(usuarioPerfilNube != null) {
                        //context.startActivity(emailIntent);
                        new SendMailTask((Activity) context).execute("TestMy Javadoh",
                                "s1zha8to.", usuarioPerfilNube.getUser_email().trim(), Html.fromHtml(context.getString(R.string.asuntoEmailPassSend)),
                                Html.fromHtml(context.getString(R.string.bodyEmailPassSend) + usuarioPerfilNube.getUser_login() +
                                        context.getString(R.string.bodyEmailPassSend2) + usuarioPerfilNube.getUser_password() +
                                        context.getString(R.string.bodyEmailPassSend3) + usuarioPerfilNube.getUser_secondary_password()), "");

                        //AVISO A LA UI COMENTADO PORQUE DEBE MANEJAR MENSAJE DE TIMEOUT
                        //Toast.makeText(context, context.getString(R.string.emailSendSuccess), Toast.LENGTH_SHORT).show();
                        //dialogInterface.dismiss();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, context.getString(R.string.errorEmailEnvio) + usuarioPerfilNube.getUser_login(), e);
                    //AVISO A LA UI
                    Toast.makeText(context, context.getString(R.string.errorEmailEnvio) + usuarioPerfilNube.getUser_login(),
                            Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }else if("CHANGE_PASSWORD".equalsIgnoreCase(flagCall)){
                Toast.makeText(context, context.getString(R.string.msg_actualiza_pass_exito), Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
            //################################# TRATO IN APP PAY #############################//
            else if("INAPPPAY".equalsIgnoreCase(flagCall)){
                Toast.makeText(context, context.getResources().getString(R.string.msgPaySuccess), Toast.LENGTH_SHORT).show();
            }
            /*else if("DATA_EXAM_AFTER_CREATE_TEST".equalsIgnoreCase(flagCall)){

                //CERRAMOS LA ACTIVIDAD DE CREAR PRUEBAS
                Intent intent = new Intent(this.activity, PostLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("SESSION_USER", usuarioPerfilNube);
                intent.putExtra("SESSION_EXAMS", examenUsuarioNube);
                intent.putExtra("PASSWORD_USED", passwordUsedForLogin);

                this.activity.startActivity(intent);
                this.activity.finish();
            }*/

        }else if(result == 2) {

            if ("LOGIN".equalsIgnoreCase(flagCall) || "FORGOT_PASSWORD".equalsIgnoreCase(flagCall)) {

                /*ImageView imageNoData = new ImageView(context);
                imageNoData.setImageResource(R.drawable.logo);
                imageNoData.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        if("LOGIN".equalsIgnoreCase(flagCall)) {
                            dialog.dismiss();
                        }else{
                            dialogInterface.dismiss();
                        }
                    }

                });
                dialog.setContentView(imageNoData);*/
                Toast.makeText(context, context.getResources().getString(R.string.msgNoRegisteredUser), Toast.LENGTH_LONG).show();

            } else if("DATA_EXAM".equalsIgnoreCase(flagCall)){

                try {
                    Intent intentPostLogin = new Intent();
                    intentPostLogin.setClass(context.getApplicationContext(), PostLogin.class);
                    intentPostLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentPostLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentPostLogin.putExtra("SESSION_USER", usuarioPerfilNube);
                    intentPostLogin.putExtra("SESSION_EXAMS", examenUsuarioNube);
                    intentPostLogin.putExtra("PASSWORD_USED", this.jsonReqObj.getString("password_used"));
                    context.getApplicationContext().startActivity(intentPostLogin);
                }catch (JSONException jsoe){
                    jsoe.printStackTrace();
                }

            }else if("REGISTRO_EXAMEN_TERMINADO_REPORTE".equalsIgnoreCase(flagCall)){
                Toast.makeText(context, context.getString(R.string.msgErrorTaskServer)+" "+response.toString(), Toast.LENGTH_SHORT).show();
            }else if("DATA_EXAMS_REPORTS".equalsIgnoreCase(flagCall)){
                Toast.makeText(context, context.getResources().getString(R.string.msgNoReportsForExam)+" "+examenSeleccionadoReporte, Toast.LENGTH_SHORT).show();
            }
            else if("ADD_NEW_EXAM".equalsIgnoreCase(flagCall) || "UPDATE_EXAMS_USER".equalsIgnoreCase(flagCall)){
                Toast.makeText(context, context.getString(R.string.msgErrorTaskServer), Toast.LENGTH_LONG);
            }else if("UPLOAD_IMAGES_EXAM".equalsIgnoreCase(flagCall)){ //|| "DATA_EXAM_AFTER_CREATE_TEST".equalsIgnoreCase(flagCall)
                    //|| "UPDATE_EXAM".equalsIgnoreCase(flagCall) || "ADD_NEW_EXAM".equalsIgnoreCase(flagCall)) {
                    //CERRAMOS LA ACTIVIDAD DE CREAR PRUEBAS
                    Intent intent = new Intent(this.activity, PostLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("SESSION_USER", usuarioPerfilNube);
                    intent.putExtra("SESSION_EXAMS", examenUsuarioNube);
                    intent.putExtra("PASSWORD_USED", passwordUsedForLogin);
                    intent.putExtra("MSG_CREATE_TEST", context.getResources().getString(R.string.msgCreateTestError));

                    this.activity.startActivity(intent);
                    this.activity.finish();

            }else if("ADD_NEW_USER".equalsIgnoreCase(flagCall) || "UPDATE_USER".equalsIgnoreCase(flagCall)){
                Toast.makeText(context, context.getResources().getString(R.string.msgErrorDuplicatedUser), Toast.LENGTH_LONG);
            }

        }else{
            if(!"NEW_EXAM_USER".equalsIgnoreCase(flagCall) && !"UPDATE_EXAMS_USER".equalsIgnoreCase(flagCall) &&
                    !"UPLOAD_IMAGES_EXAM".equalsIgnoreCase(flagCall) && !"DATA_EXAMS_REPORTS".equalsIgnoreCase(flagCall)) {

                /*ImageView imageNoConex = new ImageView(context);
                imageNoConex.setImageResource(R.drawable.logo);
                imageNoConex.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        if ("FORGOT_PASSWORD".equalsIgnoreCase(flagCall) || "CHANGE_PASSWORD".equalsIgnoreCase(flagCall) ||
                                "ADD_NEW_USER".equalsIgnoreCase(flagCall)) {
                            dialogInterface.dismiss();
                        } else {
                            if(dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    }
                });*/

                if (response != null) {
                    Toast.makeText(context, "Error: "+response.toString(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context, context.getResources().getString(R.string.msgErrorTaskServer), Toast.LENGTH_LONG).show();
                }
                    //if(dialogInterface != null) {
                    //    dialogInterface.dismiss();
                    //}else
                    //if (dialog!= null){
                    //    dialog.dismiss();
                    //}
                //HACER SWITCH CASE CON ERRORES CONSTANTES
            } else {
                if (!"UPLOAD_IMAGES_EXAM".equalsIgnoreCase(flagCall)) {
                    Toast.makeText(context, "Error: " + response.toString(), Toast.LENGTH_SHORT).show();
                }else{
                    //CERRAMOS LA ACTIVIDAD DE CREAR PRUEBAS
                    Intent intent = new Intent(this.activity, PostLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("SESSION_USER", usuarioPerfilNube);
                    intent.putExtra("SESSION_EXAMS", examenUsuarioNube);
                    intent.putExtra("PASSWORD_USED", passwordUsedForLogin);
                    intent.putExtra("MSG_CREATE_TEST", context.getResources().getString(R.string.msgCreateTestSuccess));

                    this.activity.startActivity(intent);
                    this.activity.finish();
                }

            }

        }


    }

}
