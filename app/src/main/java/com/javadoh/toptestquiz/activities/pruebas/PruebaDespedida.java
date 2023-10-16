package com.javadoh.toptestquiz.activities.pruebas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.javadoh.toptestquiz.R;
import com.javadoh.toptestquiz.activities.PostLogin;
import com.javadoh.toptestquiz.activities.consulta.bean.premium.ReporteExamenesNubeSubmitted;
import com.javadoh.toptestquiz.utils.Constants;
import com.javadoh.toptestquiz.utils.GeneralHttpTask;
import com.javadoh.toptestquiz.utils.LoginDataBaseHelper;
import com.javadoh.toptestquiz.utils.bean.DynamicTestResponse;
import com.javadoh.toptestquiz.utils.bean.MemoryBean;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioNube;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioEncuestado;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioNube;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by luiseliberal on 11-07-2015.
 */
public class PruebaDespedida extends Activity{

    Button btnFinalizarTest;
    LoginDataBaseHelper loginDataBaseHelper;
    private String passwordUsedForLogin;
    private TextView txtNotaFinal;
    private Animation txtScoreAnimation;
    //ADMOB
    AdView mAdView;
    //ADMOB INTERSTITIAL
    InterstitialAd mInterstitialAd;

    private static final String TAG = PruebaDespedida.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prueba_despedida);

        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        if(!Constants.isAdsDisabled) {
            //ADMOB INICIALIZACION BANNER
            mAdView = (AdView) findViewById(R.id.adBannerView);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdView.loadAd(adRequest);
            //ADMOB INTERSTITIAL
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.adintersticial));
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                }
            });
            requestNewInterstitial();
        }

        TextView mensajeNotaFinal = new TextView(this);
        txtNotaFinal = (TextView) findViewById(R.id.msg_notafinal);
        btnFinalizarTest = (Button) findViewById(R.id.FinalizarTest);

        float notaFinal = 0;
        Bundle sesion = getIntent().getExtras();
        final PerfilUsuarioNube usuarioPerfilSesion = (PerfilUsuarioNube) sesion.get("SESSION_USER");
        final ExamenUsuarioNube examenesPerfilSesion = (ExamenUsuarioNube) sesion.get("SESSION_EXAMS");
        final PerfilUsuarioEncuestado usuarioPerfilEncuestado = (PerfilUsuarioEncuestado) sesion.get("SESSION_USER_ENCUESTADO");
        final int idExamenActual = sesion.getInt("SESSION_EXAM_ACTUAL_ID");
        passwordUsedForLogin = sesion.getString("PASSWORD_USED");

        try {
            //INSTANCIACION DE BD
            loginDataBaseHelper = new LoginDataBaseHelper(MemoryBean.getContextBase());
            loginDataBaseHelper.open();
            notaFinal = envioDatosDePruebaAlServidor(usuarioPerfilSesion, examenesPerfilSesion, usuarioPerfilEncuestado, idExamenActual);

            //NOTA FINAL A MOSTRAR
            txtNotaFinal.setText(String.valueOf(notaFinal));

            //INICIALIZAMOS LOS OBJETOS DE ANIMACION DE DIALOGO FIN TURNO
            txtScoreAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
            txtScoreAnimation.reset();
            txtScoreAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    txtNotaFinal.setBackgroundResource(R.drawable.btn_rounded_box_blue);
                }
                @Override
                public void onAnimationEnd(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "Error: ",e);
        }finally {
            if(loginDataBaseHelper != null)
            loginDataBaseHelper.close();
        }
        //MENSAJE NOTA FINAL
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mensajeNotaFinal.setTextSize(22);
        mensajeNotaFinal.setText("Tu nota obtenida ha sido: "+notaFinal);
        mensajeNotaFinal.setLayoutParams(params);
        //SE ONCLICK LISTENER DEL BOTON DE FIN
        btnFinalizarTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {

                    finish();

                }catch (Exception e){
                    e.printStackTrace();
                }
                //CREAR INTENT DE REGISTRO Y COMIENZO DE LA ACTIVIDAD EN LA APP
                Intent retornarPostLogin = new Intent(getApplicationContext(), PostLogin.class);

                retornarPostLogin.putExtra("SESSION_USER", usuarioPerfilSesion);
                retornarPostLogin.putExtra("SESSION_EXAMS", examenesPerfilSesion);
                retornarPostLogin.putExtra("PASSWORD_USED", passwordUsedForLogin);
                startActivity(retornarPostLogin);
            }
        });
    }

    private float envioDatosDePruebaAlServidor(PerfilUsuarioNube perfilCreadorExamen, ExamenUsuarioNube perfilExamen,
                                              PerfilUsuarioEncuestado perfilEncuestado, int idExamenActual) throws Exception{

        ProgressBar progressBar = new ProgressBar(PruebaDespedida.this);
        DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();
        String url = "";
        String metodoRest = "REGISTRO_EXAMEN_TERMINADO_REPORTE";
        JSONObject jsonObject;
        //BEAN DE DATOS DE PRUEBA ACTUAL TOMADOS DESDE LA BASE DE DATOS
        //ReporteExamenActualTerminado reporteExamenActual = null;

        ReporteExamenesNubeSubmitted reporteExamenActual = null;
        try {
            reporteExamenActual = loginDataBaseHelper.getCurrentFinishedExamData(perfilCreadorExamen, perfilExamen,
                    perfilEncuestado, idExamenActual);
                //SOLO SI EL USUARIO ES PREMIUM ENVIAREMOS LOS DATOS AL SERVIDOR
            //PRUEBA
            //perfilCreadorExamen.setUser_premium(true);

            if(perfilCreadorExamen.isUser_has_premium_reports()){

                //PARSE JAVA BEAN EXAMENES A GSON Y JSON
                Gson gson = new Gson();
                String json;
                //DEFINIR LA URL DEL SERVIDOR
                //NEW_DATA_EXAM_REPORT
                url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.UPDATE_EXAM_REPORT+"?idUser="+perfilCreadorExamen.getUser_id()+
                        "&idExam="+idExamenActual;
                //SOLO CUERPO DE EXAM_CONF CUANDO SEA UN UPDATE
                Type type = new TypeToken<ReporteExamenesNubeSubmitted>() {
                }.getType();
                json = gson.toJson(reporteExamenActual, type);

                //TEST
                //json = "{'estaok': 'SI'}";
                jsonObject = new JSONObject(json);
                //INVOCAMOS A LA TAREA ASYNCRONA ENVIANDO NUESTROS DATOS Y BEAN PARA SER GUARDADOS EN EL SERVIDOR
                new GeneralHttpTask(PruebaDespedida.this, progressBar, dynamicTestResponse, metodoRest, jsonObject,
                        perfilCreadorExamen).execute(url);
            }

        }catch (JSONException jsoe){
            jsoe.printStackTrace();
            Log.d(TAG, "Error: ",jsoe);
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "Error: ",e);
        }

        return reporteExamenActual.getNotaFinal();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgCannotReturnTest), Toast.LENGTH_SHORT).show();
    }

    //ADMOB INTERSTITIAL
    private void requestNewInterstitial() {

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        if (!Constants.isAdsDisabled) {
            //ADMOB INTERSTITIAL
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }
}
