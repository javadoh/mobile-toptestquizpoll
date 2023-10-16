package com.javadoh.toptestquiz.utils;

/**
 * Created by luiseliberal on 27-09-2015.
 */

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.javadoh.toptestquiz.utils.bean.DynamicTestResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

    private static final String TAG = AsyncHttpTask.class.getName();
    ProgressBar progressBar;
    Context context;
    RecyclerView mRecyclerView;
    DynamicTestResponse responsePostLogin;
    //private List<ExamenUsuarioNube> examenAttributesBean;
    //private List<PerfilUsuarioNube> userAttributesBean;
    ImageView imgNoData;
    ImageView imgNoConex;

    public AsyncHttpTask(Context context, RecyclerView mRecyclerView,
                         ProgressBar progressBar, DynamicTestResponse responsePostLogin,
                         ImageView imgNoData, ImageView imgNoConex){
        this.context = context;
        this.mRecyclerView = mRecyclerView;
        this.progressBar = progressBar;
        this.imgNoData = imgNoData;
        this.imgNoConex = imgNoConex;
        this.responsePostLogin = responsePostLogin;

    }

    @Override
    public void onPreExecute() {

        ((Activity)context).setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public Integer doInBackground(String... params) {

        Integer result = 0;
        HttpURLConnection urlConnection;
        try {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

            //MANEJO DE TIMEOUT
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);

            int statusCode = urlConnection.getResponseCode();

            // 200 REPRESENTA HTTP OK
            if (statusCode == 200) {
                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    response.append(line);
                }

                //INVOCACION DE CLASE DE PARSEO GENERAL

                Log.d(TAG, "############################# RESPONSE JSON: " + response.toString());

                ParseResults parseResults = new ParseResults(responsePostLogin, context);

                responsePostLogin = parseResults.reponsePostLogin(response.toString());
                //userAttributesBean = parseResults.parseResultPerfilUsuario(response.toString());
                //examenAttributesBean = parseResults.parseResult(response.toString());

                result = 1; //EXITOSO

                if (responsePostLogin == null){
                   result = 2;
                }

            } else {
                result = 3; //FALLO AL OBTENER LA DATA
            }

        } catch (SocketTimeoutException | ConnectException e)
        {
            e.printStackTrace();
            result = 3;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getLocalizedMessage());
            throw new RuntimeException("Ocurrió un error: ", e.getCause());
        }

        return result; //"Failed to fetch data!";
    }


    /*
    @Override
    protected void onPostExecute(Integer result) {
        //DESCARGA COMPLETA , HACEMOS UPDATE DE LA UI
        progressBar.setVisibility(View.GONE);

        if (result == 1) {
            adapter = new BusquedaRespuestaAdapter(context, hierbasList);
            mRecyclerView.setAdapter(adapter);
        } else if(result == 2){
            imgNoData.setVisibility(View.VISIBLE);
        }else if(result == 3){
            imgNoConex.setVisibility(View.VISIBLE);
        } else{
            Toast.makeText(context, "Ha ocurrido un error, Lo sentimos.", Toast.LENGTH_SHORT).show();
        }
    }
    */

}