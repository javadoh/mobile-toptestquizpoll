package com.javadoh.toptestquiz.activities.pruebas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

import com.javadoh.toptestquiz.R;
import com.javadoh.toptestquiz.activities.pruebas.bean.PreguntasBean;
import com.javadoh.toptestquiz.activities.pruebas.bean.RespuestaPreDefBean;
import com.javadoh.toptestquiz.utils.Constants;
import com.javadoh.toptestquiz.utils.LoginDataBaseHelper;
import com.javadoh.toptestquiz.utils.bean.MemoryBean;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioNube;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioEncuestado;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioNube;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by luiseliberal on 04-07-2015.
 */
public class PruebaPrincipal extends FragmentActivity implements Chronometer.OnChronometerTickListener{

    PruebaPageAdapter pruebaPageAdapter;
    ViewPager viewPager;
    //ADMOB
    AdView mAdView;
    LoginDataBaseHelper loginDataBaseAdapter;
    private Chronometer crono;
    private static long timeElapsed = 0;
    private static int seconds = 0;
    ExamenUsuarioNube usuarioSesionExamen;
    PerfilUsuarioEncuestado usuarioEncuestado;
    PerfilUsuarioNube encuestador;
    int examenId;
    private static String passwordUsedForLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_view);

        Bundle sesion = getIntent().getExtras();

        encuestador = (PerfilUsuarioNube) sesion.get("SESSION_USER");
        usuarioEncuestado = (PerfilUsuarioEncuestado) sesion.get("SESSION_ENCUESTADO");
        usuarioSesionExamen = (ExamenUsuarioNube) sesion.get("SESSION_EXAMS");
        examenId = (int)sesion.get("SESSION_EXAMID");
        passwordUsedForLogin = (String) sesion.get("PASSWORD_USED");

        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        /*if(!encuestador.isUser_ads_disabled()) {
            //ADMOB INICIALIZACION BANNER
            mAdView = (AdView) findViewById(R.id.adBannerView);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdView.loadAd(adRequest);
        }*/

        //SETEAMOS TODOS LOS ELEMENTOS DE LA PAGINA DEL QUIZ
        setAllPageElements(encuestador, usuarioEncuestado);

        //CAMBIO MANTENIENDO BEAN ORIGINAL PARA TENER MENOS IMPACTO
        PreguntasBean preguntasBean;
        RespuestaPreDefBean respuestasBean;
        List<PreguntasBean> listaPreguntas = new ArrayList<PreguntasBean>();
        ArrayList<RespuestaPreDefBean> listaRespuestasXpregunta = new ArrayList<RespuestaPreDefBean>();

        for(int x = 0; x < usuarioSesionExamen.getExam_conf().get(examenId).getExamUsuarioNubeCurrentConfList().getQuestions().size(); x++) {

            preguntasBean = new PreguntasBean();
            preguntasBean.setIdPregunta(usuarioSesionExamen.getExam_conf().get(examenId).getExamUsuarioNubeCurrentConfList().getQuestions().get(x).getId());
            preguntasBean.setTextoPregunta(usuarioSesionExamen.getExam_conf().get(examenId).getExamUsuarioNubeCurrentConfList().getQuestions().get(x).getDesc());
            //0808
            preguntasBean.setPathImagenPregunta(usuarioSesionExamen.getExam_conf().get(examenId).getExamUsuarioNubeCurrentConfList().getQuestions().get(x).getImgPregPathServer());
            //preguntasBean.setPathImagenPregunta(x + ".jpg");
            preguntasBean.setRespuesta(String.valueOf(usuarioSesionExamen.getExam_conf().get(examenId).getExamUsuarioNubeCurrentConfList().getAnswers().get(x).getCorrect_answer()));

            for(int y = 0; y < usuarioSesionExamen.getExam_conf().get(examenId).getExamUsuarioNubeCurrentConfList().getAnswers().get(x).getAnswers().size(); y++) {

                //for(int z = 0; z < usuarioSesionExamen.getExam_conf().get(examenId).getExamUsuarioNubeCurrentConfList().getAnswers().get(x).getAnswers().size(); z++) {
                    respuestasBean = new RespuestaPreDefBean();
                    respuestasBean.setId(usuarioSesionExamen.getExam_conf().get(examenId).getExamUsuarioNubeCurrentConfList().getAnswers().get(x).getAnswers().get(y).getId());
                    respuestasBean.setRespuesta(usuarioSesionExamen.getExam_conf().get(examenId).getExamUsuarioNubeCurrentConfList().getAnswers().get(x).getAnswers().get(y).getDesc());

                    listaRespuestasXpregunta.add(respuestasBean);
                //}

            }

                preguntasBean.setRespuestasPreDef(listaRespuestasXpregunta);
                listaPreguntas.add(preguntasBean);

        }
        //DESORDENAMOS ALEATORIAMENTE LAS PREGUNTAS PARA SER PRESENTADAS EN EL EXAMEN
        Collections.shuffle(listaPreguntas);

        //INICIAMOS LA LISTA DE FRAGMENTOS SETEANDO LA LISTA EN MEMORIA DE LAS PREGUNTAS CARGADAS CON SUS ELEMENTOS
        //List<Fragment> paginasFragmentos = getFragmentosPag(MemoryBean.getListaEstaticaPreguntasConfig(), sesion);
        List<Fragment> paginasFragmentos = getFragmentosPag(listaPreguntas, sesion);

        pruebaPageAdapter = new PruebaPageAdapter(getSupportFragmentManager(), paginasFragmentos);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pruebaPageAdapter);
        //INICIALIZACION MAPA DATOS
        Constants.setMapaPreguntasRespuestasContestadas(null);
        //CRONOMETRO INICIO PRUEBA
        crono = (Chronometer) findViewById(R.id.calling_crono);
        crono.getOnChronometerTickListener();
        startCrono();
    }

    private List<Fragment> getFragmentosPag(List<PreguntasBean> listPreguntas, Bundle sesion){

        List<Fragment> fList = new ArrayList<>();
        int contador = 0;

        for(PreguntasBean preguntasBean: listPreguntas) {

            fList.add(PaginaFragmento.newInstance(preguntasBean, sesion, contador));

            contador++;
        }

        return fList;
    }

    private void setAllPageElements(PerfilUsuarioNube encuestador, PerfilUsuarioEncuestado usuarioEncuestado){

        try {
                //CREAR INSTANCIA DE BASE DE DATOS SQLITE
                loginDataBaseAdapter = new LoginDataBaseHelper(this);
                loginDataBaseAdapter = loginDataBaseAdapter.open();

                //SET ID EXAMEN DESDE VALIDACION CONTRA BD
                MemoryBean.setIdExamen(loginDataBaseAdapter.validarUltimoIdExamenPorUsuario(encuestador, usuarioEncuestado));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setCurrentItem (int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(PruebaPrincipal.this, getResources().getString(R.string.msgCannotReturnTestStart), Toast.LENGTH_SHORT).show();
    }

    public void startCrono() {
        crono.setBase(SystemClock.elapsedRealtime());
        crono.start();
    }

    public void stopCrono(){
        crono.stop();
    }

    private void resumeCrono(){

    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        //SI EL TIEMPO SE AGOTO SALIMOS DEL EXAMEN Y VAMOS A LA PANTALLA DE DESPEDIDA
        boolean tiempoAgotado = false;
        tiempoAgotado = tiempoLlegoALimite(chronometer);

        Log.d("PruebaPrincipal chron: ", String.valueOf(chronometer.getBase()));
        if(tiempoAgotado){
            //CREAR INTENT DE PAGINA FINAL AL AGOTARSE EL TIEMPO
            Intent intentCloseExam = new Intent(MemoryBean.getContextBase().getApplicationContext(), PruebaDespedida.class);
            intentCloseExam.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentCloseExam.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentCloseExam.putExtra("SESSION_USER", encuestador);
            intentCloseExam.putExtra("SESSION_EXAMS", usuarioSesionExamen);
            intentCloseExam.putExtra("SESSION_USER_ENCUESTADO", usuarioEncuestado);
            intentCloseExam.putExtra("SESSION_EXAM_ACTUAL_ID", examenId);
            intentCloseExam.putExtra("PASSWORD_USED", passwordUsedForLogin);
            startActivity(intentCloseExam);
        }
    }


    public boolean tiempoLlegoALimite(Chronometer chronometer) {
        boolean tiempoAcabado = false;
        long timeElapsed = timeRunningCrono(chronometer);

        //SI EL TIEMPO EN MINUTOS ES MAYOR QUE EL PARAMETRIZADO EN LA CREACION DEL EXAMEN
        if (timeElapsed > usuarioSesionExamen.getExam_conf().get(examenId).getExam_time_by_question()){
            tiempoAcabado = true;
        }
        return tiempoAcabado;
    }

    private long timeRunningCrono(Chronometer chronometer){
        long timeElapsed = SystemClock.elapsedRealtime() - chronometer.getBase();
        int hours = (int) (timeElapsed / 3600000);

        timeElapsed = (timeElapsed - hours + 3600000) / 60000;

        return timeElapsed;
    }
}