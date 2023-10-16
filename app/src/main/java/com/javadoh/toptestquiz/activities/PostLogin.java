package com.javadoh.toptestquiz.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.javadoh.toptestquiz.R;
import com.javadoh.toptestquiz.activities.consulta.ReporteActivity;
import com.javadoh.toptestquiz.activities.consulta.bean.premium.ReporteExamenesNube;
import com.javadoh.toptestquiz.activities.registro.RegistroPerfilEncuestado;
import com.javadoh.toptestquiz.utils.Constants;
import com.javadoh.toptestquiz.utils.GeneralHttpTask;
import com.javadoh.toptestquiz.utils.GoogleInAppPayUtils;
import com.javadoh.toptestquiz.utils.bean.DynamicTestResponse;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioNube;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioNube;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luiseliberal on 13/03/16.
 */
public class PostLogin extends AppCompatActivity {

    private static final String TAG = PostLogin.class.getName();
    private final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int REQUEST_CODE = 12;
    //private ProgressBar progressBar;
    //private PostLoginAdapter adapter;
    //private ImageView imgNoData;
    //private ImageView imgNoConex;
    //DynamicTestResponse responsePostLogin;
    String url = "";
    Button botonRegresar;
    LinearLayout linearSubCustom, mainLinearVertical;
    int buttonIdAux = 0;
    private List<String> listaReporteExamenes;
    //ADMOB
    AdView mAdView;
    //ADMOB INTERSTITIAL
    InterstitialAd mInterstitialAd;
    private Toolbar toolbar;
    private PerfilUsuarioNube usuarioPerfilNube;
    private ExamenUsuarioNube usuarioPerfilExamenes;
    private String passwordUsedForLogin;
    //IN APP BILLING STORE
    GoogleInAppPayUtils inAppPayApi = new GoogleInAppPayUtils(PostLogin.this);
    //FLAG VERSION DESPUES DE KITKAT
    boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH;
    //DESIGN
    private Typeface face, faceStoreDialog;
    private FloatingActionButton btnFloatRegresar;
    private String msgAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_login_exam_card_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);

        //BILLING INIT
        try{

            if (ActivityCompat.checkSelfPermission(PostLogin.this, permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(PostLogin.this, permissions[1]) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(PostLogin.this, permissions[2]) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(PostLogin.this, permissions[3]) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(PostLogin.this, permissions, REQUEST_CODE);
            }

            loadSavedPreferences();

            if(Constants.isAdsDisabled) {
                savePreferences("Premium_User", true);
            }

            if(Constants.internetOn) {
                inAppPayApi.onCreate();
            }else{Toast.makeText(getBaseContext(), getString(R.string.errorNoInternet), Toast.LENGTH_SHORT).show();}
        }catch (Exception e){Toast.makeText(getBaseContext(), getString(R.string.errorInAppPayCreated), Toast.LENGTH_LONG).show();}

        //actionToolBar = (Toolbar) findViewById(R.id.postLoginBar);
        //actionToolBar.inflateMenu(R.menu.menu_post_login);

        //linearSubCustom = (LinearLayout)findViewById(R.id.linearSubCustom);
        mainLinearVertical = (LinearLayout) findViewById(R.id.mainLinearVertical);
        btnFloatRegresar = (FloatingActionButton) findViewById(R.id.btnFloatRegresarLogin);

        btnFloatRegresar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //CREAR INTENT DE RETORNO COMIENZO DE LA ACTIVIDAD DE LOGIN EN LA APP
                Intent  intentLoginPage = new Intent(getApplicationContext(), Login.class);
                intentLoginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLoginPage);
                finish();
            }
        });
        //INICIALIZACION DE RECYCLER VIEW
        //mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //adapter = new PostLoginAdapter(this, responsePostLogin);
        //responsePostLogin = new DynamicTestResponse();

        //progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        //progressBar.setVisibility(View.VISIBLE);

        //imgNoData = (ImageView) findViewById(R.id.img_no_records);
        //imgNoConex = (ImageView) findViewById(R.id.img_no_conex);

        Bundle sesion = getIntent().getExtras();
        usuarioPerfilNube = (PerfilUsuarioNube) sesion.get("SESSION_USER");
        usuarioPerfilExamenes = (ExamenUsuarioNube) sesion.get("SESSION_EXAMS");
        msgAction = (String) sesion.get("MSG_CREATE_TEST");

        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        if(!usuarioPerfilNube.isUser_ads_disabled()) {
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

        if(msgAction != null){//MENSAJE DESDE OTRAS ACTIVIDADES
            Toast.makeText(getApplicationContext(), msgAction, Toast.LENGTH_LONG).show();
        }

        if(usuarioPerfilNube.getUser_password().equalsIgnoreCase((String)sesion.get("PASSWORD_USED"))) {
            passwordUsedForLogin = usuarioPerfilNube.getUser_password();
        }
        //POST LOGIN MENU
        /*if(Constants.isPasswordAdmin) {
            MenuItem menuItemCreate = (MenuItem) findViewById(R.id.create);
            MenuItem menuItemReport = (MenuItem) findViewById(R.id.report);
            menuItemCreate.setVisible(true);
            menuItemReport.setVisible(true);
        }*/

        //reporteAdminImg=(ImageView)findViewById(R.id.idReportesImg);
        Log.d(TAG, "############################# USER_LOGIN POSTLOGIN: " + usuarioPerfilNube.getUser_login());

        //LOGICA DE SESION Y ACTIVIDADES
        /*if("admin".equalsIgnoreCase(usuarioPerfil.getUser_login())){
            reporteAdminImg.setVisibility(View.VISIBLE);
        }else{reporteAdminImg.setVisibility(View.INVISIBLE);}*/

        //SE ONCLICK LISTENER DEL BOTON DE REGISTRO


        /*encuestaImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //CREAR INTENT DE REGISTRO Y COMIENZO DE LA ACTIVIDAD EN LA APP
                Intent intentPerfilEncuestado = new Intent(getApplicationContext(),RegistroPerfilEncuestado.class);

                intentPerfilEncuestado.putExtra("SESSION_USER", usuarioPerfilNube);
                intentPerfilEncuestado.putExtra("SESSION_EXAMS", usuarioPerfilExamenes);

                startActivity(intentPerfilEncuestado);
                finish();
    }
});

        crearExamImg.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //CREAR INTENT DE REGISTRO Y COMIENZO DE LA ACTIVIDAD EN LA APP
                Intent intentCrearPrueba = new Intent(getApplicationContext(), CrearPrueba.class);

                intentCrearPrueba.putExtra("SESSION_USER", usuarioPerfilNube);
                intentCrearPrueba.putExtra("SESSION_EXAMS", usuarioPerfilExamenes);
                startActivity(intentCrearPrueba);
                finish();
            }

        });


        reporteAdminImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //CREAR INTENT DE REPORTES EN LA APP DEPENDIENDO DE SI EL USUARIO ES PREMIUM O NO SE MUESTRAN VISTAS DISTINTAS
                //PARA PROBAR
                usuarioPerfilNube.setUser_premium(true);

                if(usuarioPerfilNube.isUser_premium()){

                    AlertDialog.Builder builder = new AlertDialog.Builder(PostLogin.this);
                    builder.setTitle("TIPOS DE REPORTES DE EXAMENES");

                    CharSequence[] chars = {};
                    listaReporteExamenes = new ArrayList<>();

                    if (usuarioPerfilExamenes != null) {
                        //PARA VER TODOS LOS EXAMENES POR DEFECTO SIEMPRE Y CUANDO EXISTAN EXAMENES
                        listaReporteExamenes.add("TODOS LOS EXAMENES");
                        System.out.println("Exam conf size: " + usuarioPerfilExamenes.getExam_conf().size());
                        //AGREGAMOS EL EXAMEN POR DEFECTO PARA VER TODOS
                        for (int i = 0; i < usuarioPerfilExamenes.getExam_conf().size(); i++) {
                            //AGREGAMOS EN LA LISTA AUXILIAR EL ID DEL EXAMEN
                            listaReporteExamenes.add(usuarioPerfilExamenes.getExam_conf().get(i).getExam_designId()+" - "+
                                    usuarioPerfilExamenes.getExam_conf().get(i).getExam_title());
                        }

                        chars = listaReporteExamenes.toArray(new CharSequence[listaReporteExamenes.size()]);

                        builder.setItems(chars,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        //PROCEDEMOS
                                        ProgressBar progressBar = new ProgressBar(PostLogin.this);
                                        DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();
                                        String url;
                                        JSONObject jsonObject = new JSONObject();
                                        ReporteExamenesNube reporteExamenesNube = new ReporteExamenesNube();
                                        String examenSeleccionado = listaReporteExamenes.get(which);
                                        String[] splitExamenSeleccionado = examenSeleccionado.split("-");
                                        int idExamenEscogido = Integer.parseInt(splitExamenSeleccionado[0].trim());
                                        //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                                        url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.GET_REPORT_BY_USER_AND_EXAM + "?idUserPpal=" + usuarioPerfilNube.getUser_id() + "&idExamDesign=" + idExamenEscogido;

                                        new GeneralHttpTask(PostLogin.this, progressBar, dynamicTestResponse, "DATA_EXAMS_REPORTS",
                                                jsonObject, usuarioPerfilNube, usuarioPerfilExamenes, reporteExamenesNube, dialog, examenSeleccionado).execute(url);
                                    }
                                });

                        builder.create().show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Estimado usuario, debe crear una prueba primero para que " +
                                "aparezca en la lista de examenes disponibles a presentar. Por favor, regrese a la ventana previa.", Toast.LENGTH_LONG).show();
                        HERB_WICH_AUX = 0;
                    }

                }else {
                    Intent intentReportesAdmin = new Intent(getApplicationContext(), ReporteActivity.class);
                    intentReportesAdmin.putExtra("SESSION_USER", usuarioPerfilNube);
                    intentReportesAdmin.putExtra("SESSION_EXAMS", usuarioPerfilExamenes);
                    startActivity(intentReportesAdmin);
                    finish();
                }
            }
        });*/

        //FONT DE ESTILO A
        face = Typeface.createFromAsset(getAssets(), "droidbold.otf");

        //LOGICA DE VISTAS TIPO GRID DEPENDIENDO DEL USUARIO
        if(passwordUsedForLogin != null){
            mainLinearVertical.setWeightSum(3);//TIENE TODAS LAS OPCIONES DE ADMINISTRADOR

            //####################### CREAR EXAMEN #######################//
            TextView textCreate = new TextView(this);
            textCreate.setText(getResources().getString(R.string.tituloCrearPostLog));
            textCreate.setTextColor(getResources().getColor(R.color.error_color));
            textCreate.setTextSize(36);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            textCreate.setPadding(0, 0, 0, 10);
            textCreate.setLayoutParams(params);
            textCreate.setGravity(Gravity.CENTER);
            textCreate.setTypeface(face);
            //textCreate.setShadowLayer(50, 0, 0, Color.WHITE);
            textCreate.setBackground(getResources().getDrawable(R.drawable.create_back));

            textCreate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //PREMIUM PRODUCT ACTION
                    if((usuarioPerfilNube.isUser_unlimited_exams()) || usuarioPerfilExamenes == null) {
                        //CREAR INTENT DE REGISTRO Y COMIENZO DE LA ACTIVIDAD EN LA APP
                        Intent intentCrearPrueba = new Intent(getApplicationContext(), CrearPrueba.class);
                        intentCrearPrueba.putExtra("SESSION_USER", usuarioPerfilNube);
                        intentCrearPrueba.putExtra("SESSION_EXAMS", usuarioPerfilExamenes);
                        intentCrearPrueba.putExtra("PASSWORD_USED", passwordUsedForLogin);
                        startActivity(intentCrearPrueba);
                        finish();
                    }else if(usuarioPerfilExamenes.getExam_conf().size() < 3){
                        Intent intentCrearPrueba = new Intent(getApplicationContext(), CrearPrueba.class);
                        intentCrearPrueba.putExtra("SESSION_USER", usuarioPerfilNube);
                        intentCrearPrueba.putExtra("SESSION_EXAMS", usuarioPerfilExamenes);
                        intentCrearPrueba.putExtra("PASSWORD_USED", passwordUsedForLogin);
                        startActivity(intentCrearPrueba);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.errorExamsMaxPremium), Toast.LENGTH_LONG).show();
                    }

                }
            });
            mainLinearVertical.addView(textCreate);
            /*ImageView createImg = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            createImg.setLayoutParams(params);
            createImg.setBackground(getResources().getDrawable(R.drawable.create_back));
            createImg.setClickable(true);
            createImg.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //CREAR INTENT DE REGISTRO Y COMIENZO DE LA ACTIVIDAD EN LA APP
                    Intent intentCrearPrueba = new Intent(getApplicationContext(), CrearPrueba.class);
                    intentCrearPrueba.putExtra("SESSION_USER", usuarioPerfilNube);
                    intentCrearPrueba.putExtra("SESSION_EXAMS", usuarioPerfilExamenes);
                    intentCrearPrueba.putExtra("PASSWORD_USED", passwordUsedForLogin);
                    startActivity(intentCrearPrueba);
                    finish();
                }
            });
            mainLinearVertical.addView(createImg);*/

            //####################### PRESENTAR EXAMEN #######################//
            TextView textPresent = new TextView(this);
            textPresent.setText(getResources().getString(R.string.tituloPresentarPostLog));
            textPresent.setTextColor(getResources().getColor(R.color.error_color));
            textPresent.setTextSize(36);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params2.weight = 1;
            textPresent.setLayoutParams(params2);
            textPresent.setGravity(Gravity.CENTER);
            textPresent.setTypeface(face);
            //textPresent.setShadowLayer(50, 0, 0, Color.WHITE);
            textPresent.setPadding(0, 0, 0, 10);
            textPresent.setBackground(getResources().getDrawable(R.drawable.present_back));

            textPresent.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //CREAR INTENT DE REGISTRO Y COMIENZO DE LA ACTIVIDAD EN LA APP
                    Intent intentCrearPrueba = new Intent(getApplicationContext(), RegistroPerfilEncuestado.class);
                    intentCrearPrueba.putExtra("SESSION_USER", usuarioPerfilNube);
                    intentCrearPrueba.putExtra("SESSION_EXAMS", usuarioPerfilExamenes);
                    intentCrearPrueba.putExtra("PASSWORD_USED", passwordUsedForLogin);
                    startActivity(intentCrearPrueba);
                    finish();
                }
            });
            mainLinearVertical.addView(textPresent);

            /*ImageView presentImg = new ImageView(this);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params2.weight = 1;
            presentImg.setLayoutParams(params2);
            presentImg.setBackground(getResources().getDrawable(R.drawable.present_back));
            presentImg.setClickable(true);
            presentImg.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //CREAR INTENT DE REGISTRO Y COMIENZO DE LA ACTIVIDAD EN LA APP
                    Intent intentPerfilEncuestado = new Intent(getApplicationContext(), RegistroPerfilEncuestado.class);
                    intentPerfilEncuestado.putExtra("SESSION_USER", usuarioPerfilNube);
                    intentPerfilEncuestado.putExtra("SESSION_EXAMS", usuarioPerfilExamenes);
                    intentPerfilEncuestado.putExtra("PASSWORD_USED", passwordUsedForLogin);
                    startActivity(intentPerfilEncuestado);
                    finish();
                }
            });
            });
            mainLinearVertical.addView(textPresent);*/

            //####################### REPORTE DE EXAMEN ###########################//
            TextView textReports = new TextView(this);
            textReports.setText(getResources().getString(R.string.tituloReportesPostLog));
            textReports.setTextColor(getResources().getColor(R.color.error_color));
            textReports.setTextSize(36);
            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params3.weight = 1;
            textReports.setLayoutParams(params3);
            textReports.setGravity(Gravity.CENTER);
            textReports.setTypeface(face);
            //textReports.setShadowLayer(50, 0, 0, Color.WHITE);
            textReports.setPadding(0, 0, 0, 10);
            textReports.setBackground(getResources().getDrawable(R.drawable.reports_back));

            textReports.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    //PRODUCT PREMIUM 4
                    if (passwordUsedForLogin != null && usuarioPerfilNube.isUser_has_premium_reports()) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(PostLogin.this);
                        builder.setTitle(getResources().getString(R.string.tituloDialogRepDispPostLog));

                        CharSequence[] chars = {};
                        listaReporteExamenes = new ArrayList<>();

                        if (usuarioPerfilExamenes != null) {
                            //PARA VER TODOS LOS EXAMENES POR DEFECTO SIEMPRE Y CUANDO EXISTAN EXAMENES
                            listaReporteExamenes.add(getResources().getString(R.string.tituloDialogRepExamPostLog));
                            Log.i(TAG, "Exam conf size: " + usuarioPerfilExamenes.getExam_conf().size());
                            //AGREGAMOS EL EXAMEN POR DEFECTO PARA VER TODOS
                            for (int i = 0; i < usuarioPerfilExamenes.getExam_conf().size(); i++) {
                                //AGREGAMOS EN LA LISTA AUXILIAR EL ID DEL EXAMEN
                                listaReporteExamenes.add(usuarioPerfilExamenes.getExam_conf().get(i).getExam_designId() + " - " +
                                        usuarioPerfilExamenes.getExam_conf().get(i).getExam_title());
                            }

                            chars = listaReporteExamenes.toArray(new CharSequence[listaReporteExamenes.size()]);

                            builder.setItems(chars,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            //PROCEDEMOS
                                            ProgressBar progressBar = new ProgressBar(PostLogin.this);
                                            DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();
                                            String url;
                                            JSONObject jsonObject = new JSONObject();
                                            ReporteExamenesNube reporteExamenesNube = new ReporteExamenesNube();
                                            String examenSeleccionado = listaReporteExamenes.get(which);
                                            String[] splitExamenSeleccionado = examenSeleccionado.split("-");
                                            int idExamenEscogido = Integer.parseInt(splitExamenSeleccionado[0].trim());
                                            //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                                            url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.GET_REPORT_BY_USER_AND_EXAM + "?idUserPpal=" + usuarioPerfilNube.getUser_id() + "&idExamDesign=" + idExamenEscogido;

                                            new GeneralHttpTask(PostLogin.this, progressBar, dynamicTestResponse, "DATA_EXAMS_REPORTS",
                                                    jsonObject, usuarioPerfilNube, usuarioPerfilExamenes, reporteExamenesNube, dialog, examenSeleccionado, passwordUsedForLogin).execute(url);
                                        }
                                    });

                            builder.create().show();
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgNoExamsForReportPostLog), Toast.LENGTH_LONG).show();
                        }

                    } else {//REPORTE DE USUARIO SECUNDARIO SOLO VE DATA DE PRUEBAS LOCALES SIN EXPORTACION DE NINGUN TIPO
                        Intent intentReportesAdmin = new Intent(getApplicationContext(), ReporteActivity.class);
                        intentReportesAdmin.putExtra("SESSION_USER", usuarioPerfilNube);
                        intentReportesAdmin.putExtra("SESSION_EXAMS", usuarioPerfilExamenes);
                        intentReportesAdmin.putExtra("PASSWORD_USED", passwordUsedForLogin);
                        startActivity(intentReportesAdmin);
                        finish();
                    }
                }
            });

            mainLinearVertical.addView(textReports);


        }else{
            mainLinearVertical.setWeightSum(1);//SOLO PRESENTA EXAMENES

            //PRESENTAR EXAMEN
            TextView textPresentStudent = new TextView(this);
            textPresentStudent.setText(getResources().getString(R.string.tituloPresentarPostLog));
            textPresentStudent.setTextColor(getResources().getColor(R.color.error_color));
            textPresentStudent.setTextSize(48);
            textPresentStudent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            textPresentStudent.setGravity(Gravity.CENTER);
            textPresentStudent.setTypeface(face);
            //textPresentStudent.setShadowLayer(50, 0, 0, Color.WHITE);
            textPresentStudent.setPadding(0, 0, 0, 10);
            textPresentStudent.setBackground(getResources().getDrawable(R.drawable.present_back_student));
            textPresentStudent.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //CREAR INTENT DE REGISTRO Y COMIENZO DE LA ACTIVIDAD EN LA APP
                    Intent intentPresentarPrueba = new Intent(getApplicationContext(), RegistroPerfilEncuestado.class);
                    intentPresentarPrueba.putExtra("SESSION_USER", usuarioPerfilNube);
                    intentPresentarPrueba.putExtra("SESSION_EXAMS", usuarioPerfilExamenes);
                    intentPresentarPrueba.putExtra("PASSWORD_USED", passwordUsedForLogin);
                    startActivity(intentPresentarPrueba);
                    finish();
                }
            });

            mainLinearVertical.addView(textPresentStudent);

            /*ImageView presentImg = new ImageView(this);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params2.weight = 1;
            presentImg.setLayoutParams(params2);
            presentImg.setBackground(getResources().getDrawable(R.drawable.present_back_student));

            mainLinearVertical.addView(textPresentStudent);
        }*/

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buscador, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem menuItemPassEdit = menu.findItem(R.id.action_perfil_pass_edit);
        menuItemPassEdit.setVisible(true);
        MenuItem menuTienda = menu.findItem(R.id.action_store);
        menuTienda.setVisible(false);
        MenuItem menuItemNews = menu.findItem(R.id.action_news);
        menuItemNews.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_store:
                LayoutInflater linflater = LayoutInflater.from(PostLogin.this);
                View vistaDialogoStore = linflater.inflate(R.layout.dialog_store_from_menu, null);

                AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(
                        PostLogin.this);

                alertDialogBuilder2.setView(vistaDialogoStore);

                //faceStoreDialog = Typeface.createFromAsset(getAssets(), "croissant_sandwich.ttf");
                //VISTAS LAYOUT
                TextView txtProductOne = (TextView) vistaDialogoStore.findViewById(R.id.txtTituloProducto);
                //txtProductOne.setTypeface(faceStoreDialog);
                TextView txtProductDos = (TextView) vistaDialogoStore.findViewById(R.id.txtTituloProductoDos);
                //txtProductDos.setTypeface(faceStoreDialog);
                TextView txtProductTres = (TextView) vistaDialogoStore.findViewById(R.id.txtTituloProductoTres);
                //txtProductTres.setTypeface(faceStoreDialog);
                TextView txtProductCuatro = (TextView) vistaDialogoStore.findViewById(R.id.txtTituloProductoCuatro);
                //txtProductCuatro.setTypeface(faceStoreDialog);

                Button buttonPayOne = (Button) vistaDialogoStore.findViewById(R.id.buttonPayOne);
                Button buttonPayTwo = (Button) vistaDialogoStore.findViewById(R.id.buttonPayTwo);
                Button buttonPayThree = (Button) vistaDialogoStore.findViewById(R.id.buttonPayThree);
                Button buttonPayFour = (Button) vistaDialogoStore.findViewById(R.id.buttonPayFour);

                if(!isKitKat){
                    buttonPayOne.setBackgroundColor(getResources().getColor(R.color.textBackGreen));
                    buttonPayTwo.setBackgroundColor(getResources().getColor(R.color.textBackGreen));
                    buttonPayThree.setBackgroundColor(getResources().getColor(R.color.textBackGreen));
                    buttonPayFour.setBackgroundColor(getResources().getColor(R.color.textBackDarkGray));
                }

                Log.d(TAG, "ADS DISABLED? "+Constants.isAdsDisabled);
                Log.d(TAG, "Usuario Ads Disabled? "+usuarioPerfilNube.isUser_ads_disabled());
                if(usuarioPerfilNube.isUser_ads_disabled() || Constants.isAdsDisabled) {//SI HAY PAGO MOSTRAMOS MENSAJE Y DESHABILITAMOS BOTON DE PAGO
                    //txtTituloProducto.setText(R.string.compra_realizada_store);
                    buttonPayOne.setEnabled(false);
                    buttonPayOne.setClickable(false);
                    buttonPayOne.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_grey_face));
                }else{
                    //txtTituloProducto.setText(R.string.subTituloPago);
                }

                //############################### PRODUCTO UNO ##########################/
                buttonPayOne.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (Constants.isInAppSetupCreated) {
                            getPayment(1);
                        } else {
                            try {
                                //IN APP BILLING GOOGLE // ESTO PUEDE REVISARSE NO SE SI DEBE IR ONCREATE DE NUEVO AQUI
                                if (Constants.internetOn) {
                                    if (Constants.isInAppSetupCreated) {
                                        getPayment(1);
                                    } else {
                                        //IN APP BILLING GOOGLE
                                        inAppPayApi.onCreate();
                                        Toast.makeText(getBaseContext(), getString(R.string.errorInAppPayCreated), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), getString(R.string.errorNoInternet), Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error no se pudo realizar el llamado a pago del producto uno. ", e);
                                Toast.makeText(getBaseContext(), getString(R.string.errorInAppPayCreated), Toast.LENGTH_LONG).show();
                            }
                        }


                    }
                });

                //############################### PRODUCTO DOS ##########################/7
                buttonPayTwo.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if(Constants.isInAppSetupCreated) {
                            getPayment(2);}else{
                            try {
                                //IN APP BILLING GOOGLE // ESTO PUEDE REVISARSE NO SE SI DEBE IR ONCREATE DE NUEVO AQUI
                                if (Constants.internetOn) {
                                    if (Constants.isInAppSetupCreated) {
                                        getPayment(2);
                                    } else {
                                        //IN APP BILLING GOOGLE
                                        inAppPayApi.onCreate();
                                        Toast.makeText(getBaseContext(), getString(R.string.errorInAppPayCreated), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), getString(R.string.errorNoInternet), Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e){Log.e(TAG, "Error no se pudo realizar el llamado a pago del producto dos. ", e);
                                Toast.makeText(getBaseContext(), getString(R.string.errorInAppPayCreated), Toast.LENGTH_LONG).show();}
                        }


                    }
                });

                //############################### PRODUCTO TRES ##########################/7
                buttonPayThree.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if(Constants.isInAppSetupCreated) {
                            getPayment(3);}else{
                            try {
                                //IN APP BILLING GOOGLE // ESTO PUEDE REVISARSE NO SE SI DEBE IR ONCREATE DE NUEVO AQUI
                                if (Constants.internetOn) {
                                    if (Constants.isInAppSetupCreated) {
                                        getPayment(3);
                                    } else {
                                        //IN APP BILLING GOOGLE
                                        inAppPayApi.onCreate();
                                        Toast.makeText(getBaseContext(), getString(R.string.errorInAppPayCreated), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), getString(R.string.errorNoInternet), Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e){Log.e(TAG, "Error no se pudo realizar el llamado a pago del producto tres. ", e);
                                Toast.makeText(getBaseContext(), getString(R.string.errorInAppPayCreated), Toast.LENGTH_LONG).show();}
                        }


                    }
                });

                //############################### PRODUCTO CUATRO ##########################/7
                buttonPayFour.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if(Constants.isInAppSetupCreated) {
                            getPayment(4);}else{
                            try {
                                //IN APP BILLING GOOGLE // ESTO PUEDE REVISARSE NO SE SI DEBE IR ONCREATE DE NUEVO AQUI
                                if (Constants.internetOn) {
                                    if (Constants.isInAppSetupCreated) {
                                        getPayment(4);
                                    } else {
                                        //IN APP BILLING GOOGLE
                                        inAppPayApi.onCreate();
                                        Toast.makeText(getBaseContext(), getString(R.string.errorInAppPayCreated), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), getString(R.string.errorNoInternet), Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e){Log.e(TAG, "Error no se pudo realizar el llamado a pago del producto cuatro. ", e);
                                Toast.makeText(getBaseContext(), getString(R.string.errorInAppPayCreated), Toast.LENGTH_LONG).show();}
                        }


                    }
                });

                //set dialog message
                alertDialogBuilder2
                        .setNegativeButton(getString(R.string.btn_later), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog3 = alertDialogBuilder2.create();
                // show it
                alertDialog3.show();
                break;

            case R.id.action_faq:
                LayoutInflater li = LayoutInflater.from(PostLogin.this);
                View vistaDialogoFaq = li.inflate(R.layout.dialog_faq_from_menu, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        PostLogin.this);

                alertDialogBuilder.setView(vistaDialogoFaq);
                alertDialogBuilder.setTitle("FAQ");
                //set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                break;
            case R.id.action_perfil_pass_edit:

                LayoutInflater liActionPerfil = LayoutInflater.from(PostLogin.this);
                final View vistaDialogo = liActionPerfil.inflate(R.layout.login_dialog_password_change, null);

                final EditText etxtLoginUser = (EditText)vistaDialogo.findViewById(R.id.etxtLoginUser);
                final EditText etxtPassPpal = (EditText)vistaDialogo.findViewById(R.id.etxtPassPpal);
                final EditText etxtNewPassPpal = (EditText)vistaDialogo.findViewById(R.id.etxtNewPassPpal);
                final EditText etxtNewPassPpalConfirm = (EditText)vistaDialogo.findViewById(R.id.etxtNewPassPpalConfirm);
                final EditText etxtNewPassSecondary = (EditText)vistaDialogo.findViewById(R.id.etxtNewPassSecondary);
                final EditText etxtNewPassSecondaryConfirm = (EditText)vistaDialogo.findViewById(R.id.etxtNewPassSecondaryConfirm);

                AlertDialog.Builder alertDialogBuilderPerfil = new AlertDialog.Builder(
                        PostLogin.this);

                alertDialogBuilderPerfil.setView(vistaDialogo);
                //set dialog message
                alertDialogBuilderPerfil
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.btn_accept),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //SALVAMOS COMO PREFERENCIA EL VALOR DEL CHECKBOX DE TERMINOS
                                        //savePreferences("CheckBox_Value", checkBoxTerms.isChecked());
                                        //checkBoxTerms.setChecked(true);

                                        String userLogin = etxtLoginUser.getText().toString();
                                        String passwordPpal = etxtPassPpal.getText().toString();
                                        String newPassPpal = etxtNewPassPpal.getText().toString();
                                        String confirmNewPassPpal = etxtNewPassPpalConfirm.getText().toString();
                                        String newPassSecondary = etxtNewPassSecondary.getText().toString();
                                        String confirmNewPassSecondary = etxtNewPassSecondaryConfirm.getText().toString();

                                        if(usuarioPerfilNube.getUser_password().equalsIgnoreCase(passwordPpal) ||
                                                usuarioPerfilNube.getUser_login().equalsIgnoreCase(userLogin)){

                                            if(newPassPpal.length() >= 6 && confirmNewPassPpal.length() >= 6 &&
                                                    newPassSecondary.length() >= 6 && confirmNewPassSecondary.length() >= 6) {

                                                if (newPassPpal.equalsIgnoreCase(confirmNewPassPpal)) {

                                                    if (newPassSecondary.equalsIgnoreCase(confirmNewPassSecondary)) {

                                                        if (!newPassPpal.equalsIgnoreCase(newPassSecondary)) {
                                                            //ENVIO DE DATOS A LA BASE DE DATOS REMOTA MONGO
                                                            try {
                                                                ProgressBar progressBar = new ProgressBar(PostLogin.this);
                                                                DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();

                                                                //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                                                                url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.CHANGE_PASSWORDS + usuarioPerfilNube.getUser_id();
                                                                JSONObject jsonObject = new JSONObject();

                                                                jsonObject.put("user_login", usuarioPerfilNube.getUser_login());
                                                                jsonObject.put("user_password", passwordPpal);
                                                                jsonObject.put("user_new_admin_password", newPassPpal);
                                                                jsonObject.put("user_secondary_password", newPassSecondary);

                                                                new GeneralHttpTask(PostLogin.this, progressBar, dynamicTestResponse, "CHANGE_PASSWORD", jsonObject, usuarioPerfilNube, dialog).execute(url);

                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                                Log.d(TAG, getString(R.string.errorEmailEnvio), e);
                                                            } finally {
                                                                //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
                                                                if (!usuarioPerfilNube.isUser_ads_disabled()) {
                                                                    //ADMOB INTERSTITIAL
                                                                    if (mInterstitialAd.isLoaded()) {
                                                                        mInterstitialAd.show();
                                                                    }
                                                                }
                                                            }

                                                        } else {
                                                            Toast.makeText(PostLogin.this, getString(R.string.msg_pass_edit_error_match_ppal_sec), Toast.LENGTH_SHORT).show();}
                                                    }else {
                                                        Toast.makeText(PostLogin.this, getString(R.string.passSecCambioNoCoincide),
                                                                Toast.LENGTH_SHORT).show();}
                                                } else {
                                                    Toast.makeText(PostLogin.this, getString(R.string.passPpalCambioNoCoincide),
                                                            Toast.LENGTH_SHORT).show();}
                                            }else{Toast.makeText(PostLogin.this, getString(R.string.passLongitudError),
                                                    Toast.LENGTH_SHORT).show();}
                                        }else{Toast.makeText(PostLogin.this, getString(R.string.passLoginActualIncorrecto),
                                                Toast.LENGTH_SHORT).show();}

                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton(getString(R.string.btn_cancelar),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog2 = alertDialogBuilderPerfil.create();
                // show it
                alertDialog2.show();

                break;
            case R.id.action_compartir:
                Toast.makeText(PostLogin.this, getString(R.string.proxSeccionCompartir), Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
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
        if(!usuarioPerfilNube.isUser_ads_disabled()) {
            //ADMOB INTERSTITIAL
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        if(!usuarioPerfilNube.isUser_ads_disabled()) {
            //ADMOB INTERSTITIAL
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
        //CREAR INTENT DE RETORNO COMIENZO DE LA ACTIVIDAD DE LOGIN EN LA APP
        Intent  intentLoginPage = new Intent(getApplicationContext(), Login.class);
        intentLoginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentLoginPage);
        finish();
    }

    public void getPayment(int product){

        try {
            //IN APP BILLING GOOGLE
            if (Constants.isInAppSetupCreated) {
                if (product == 1) {//DESHABILITA PUBLICIDAD
                    inAppPayApi.purchaseRemoveAds();
                } else if (product == 2) {//PRUEBAS ILIMITADAS
                    inAppPayApi.purchaseUnlimitedTests();//this, usuarioPerfilNube.getUser_id(), 2
                } else if (product == 3) {//RESPUESTAS MULTIPLES
                    inAppPayApi.purchaseMultipleAnswers();//this, usuarioPerfilNube.getUser_id(), 3
                } else if (product == 4) {//REPORTES PREMIUM
                    inAppPayApi.purchasePremiumReports();
                }
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.errorTienda), Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){System.out.println("Error: "+e);}
    }

    @Override
    protected void onDestroy() {
        //IN APP BILLING GOOGLE
        inAppPayApi.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        //IN APP BILLING GOOGLE
        inAppPayApi.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Request: " + requestCode + ", Result: " + resultCode + ", data: " + data);
        if (resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "IN APP BILL OK");
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i(TAG, "IN APP BILL: The user canceled.");
        }
    }

    private void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean checkPremiumUser = sharedPreferences.getBoolean("Premium_User", false);

        if (checkPremiumUser) {
            Constants.isAdsDisabled = true;
        } else {
            Constants.isAdsDisabled = false;
        }
    }

    private void savePreferences(String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length == 4 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(PostLogin.this, "Permission granted!!!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(PostLogin.this, "Necessary permissions not granted...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
