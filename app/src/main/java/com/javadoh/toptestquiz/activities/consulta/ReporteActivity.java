package com.javadoh.toptestquiz.activities.consulta;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.javadoh.toptestquiz.activities.CrearPrueba;
import com.javadoh.toptestquiz.activities.consulta.bean.ReporteAuxBean;
import com.javadoh.toptestquiz.activities.consulta.bean.ReportePreguntaLinealAuxBean;
import com.javadoh.toptestquiz.R;
import com.javadoh.toptestquiz.activities.PostLogin;
import com.javadoh.toptestquiz.activities.consulta.bean.premium.ReporteExamenesNube;
import com.javadoh.toptestquiz.activities.registro.RegistroPerfilEncuestado;
import com.javadoh.toptestquiz.utils.Constants;
import com.javadoh.toptestquiz.utils.GeneralHttpTask;
import com.javadoh.toptestquiz.utils.GoogleInAppPayUtils;
import com.javadoh.toptestquiz.utils.LoginDataBaseHelper;
import com.javadoh.toptestquiz.utils.bean.DynamicTestResponse;
import com.javadoh.toptestquiz.utils.bean.MemoryBean;
import com.javadoh.toptestquiz.utils.bean.OperacionesBdBean;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioNube;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioNube;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.StringUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by LUIS-EXTERNO on 08-07-2015.
 */
public class ReporteActivity extends AppCompatActivity{

    private static final String TAG = ReporteActivity.class.getName();
    TextView fechaCell, usuarioCell, idExamCell, idPreguntaCell, idRespuestaCell, tiempoRespuestaCell, preguntaCell, respuestaCell;
    Button btnExportarDataXls, btnRegresar;
    LinearLayout dataReportesLayout, dataReportesRow;
    LoginDataBaseHelper loginDataBaseHelper;
    boolean archivoXlsCreado = false;
    PerfilUsuarioNube encuestador;
    ExamenUsuarioNube examenUsuarioNube;
    //ADMOB
    AdView mAdView;
    //ADMOB INTERSTITIAL
    InterstitialAd mInterstitialAd;
    //IN APP BILLING STORE
    GoogleInAppPayUtils inAppPayApi = new GoogleInAppPayUtils(ReporteActivity.this);
    private Toolbar toolbar;
    String passwordUsedForLogin;
    //FLAG VERSION DESPUES DE KITKAT
    boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportes_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        //MENU AL FINAL DE LA VISTA
        /*actionToolBar = (Toolbar) findViewById(R.id.postLoginBar);
        actionToolBar.inflateMenu(R.menu.menu_post_login);*/

        HashMap<String, OperacionesBdBean> mapaRegistrosReportesPorUsuario;
        Bundle sesion = getIntent().getExtras();
        encuestador = (PerfilUsuarioNube) sesion.get("SESSION_USER");
        examenUsuarioNube = (ExamenUsuarioNube) sesion.get("SESSION_EXAMS");
        passwordUsedForLogin = sesion.getString("PASSWORD_USED");

        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        if (!encuestador.isUser_ads_disabled()) {
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

        dataReportesLayout = (LinearLayout) findViewById(R.id.dataReportesTable);
        //ADMIN VERA TODOS LOS REPORTES
        loginDataBaseHelper = new LoginDataBaseHelper(this);
        loginDataBaseHelper = loginDataBaseHelper.open();

        //GET REPORTES POR USUARIO EN BD
        mapaRegistrosReportesPorUsuario = loginDataBaseHelper.getTotalReportesPorUsuario(encuestador);

        if(mapaRegistrosReportesPorUsuario != null){
        //POBLADO DE CABECERA DE TABLA
        addTableHeaders();
        //POBLADO DE TABLA
        addTableData(mapaRegistrosReportesPorUsuario);

        btnExportarDataXls = (Button) findViewById(R.id.buttonExportarExcel);
        btnRegresar = (Button) findViewById(R.id.btnRegresar);
        if (!isKitKat) {
            btnExportarDataXls.setBackgroundColor(getResources().getColor(R.color.textBackGreen));
            btnRegresar.setBackgroundColor(getResources().getColor(R.color.color_textgray_programatically));
        }

        btnExportarDataXls.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                //exportDataToCsv(usuarioSesion);
                archivoXlsCreado = exportDataToXls(examenUsuarioNube);

                if (archivoXlsCreado) {
                    Toast.makeText(getBaseContext().getApplicationContext(), getResources().getString(R.string.msgGenExcelReport) +
                                    MemoryBean.getContextBase().getExternalFilesDir(null) + getResources().getString(R.string.nombreArchivoExcelReport),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext().getApplicationContext(), getResources().getString(R.string.msgErrorGenExcelReport),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //CERRAMOS LA ACTIVIDAD DE CREAR PRUEBAS
                Intent intentRegresar = new Intent(ReporteActivity.this, PostLogin.class);
                intentRegresar.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentRegresar.putExtra("SESSION_USER", encuestador);
                intentRegresar.putExtra("SESSION_EXAMS", examenUsuarioNube);
                intentRegresar.putExtra("PASSWORD_USED", passwordUsedForLogin);
                startActivity(intentRegresar);
                finish();
            }
        });

        /*actionToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                if (item.getItemId() == R.id.create) {
                    if (passwordUsedForLogin != null) {//ES EL ADMINISTRADOR
                        //CREAR INTENT DE REGISTRO Y COMIENZO DE LA ACTIVIDAD EN LA APP
                        Intent intentCrearPrueba = new Intent(getApplicationContext(), CrearPrueba.class);
                        intentCrearPrueba.putExtra("SESSION_USER", encuestador);
                        intentCrearPrueba.putExtra("SESSION_EXAMS", examenUsuarioNube);
                        intentCrearPrueba.putExtra("PASSWORD_USED", passwordUsedForLogin);
                        startActivity(intentCrearPrueba);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "No tienes privilegios para usar esta opción", Toast.LENGTH_SHORT).show();
                    }
                } else if (item.getItemId() == R.id.make) {
                    //CREAR INTENT DE REGISTRO Y COMIENZO DE LA ACTIVIDAD EN LA APP
                    Intent intentPerfilEncuestado = new Intent(getApplicationContext(), RegistroPerfilEncuestado.class);
                    intentPerfilEncuestado.putExtra("SESSION_USER", encuestador);
                    intentPerfilEncuestado.putExtra("SESSION_EXAMS", examenUsuarioNube);
                    intentPerfilEncuestado.putExtra("PASSWORD_USED", passwordUsedForLogin);
                    startActivity(intentPerfilEncuestado);
                    finish();
                } else if (item.getItemId() == R.id.report) {

                    if (passwordUsedForLogin != null) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ReporteActivity.this);
                        builder.setTitle("TIPOS DE REPORTES DE EXAMENES");

                        CharSequence[] chars = {};
                        listaReporteExamenes = new ArrayList<>();

                        if (examenUsuarioNube != null) {
                            //PARA VER TODOS LOS EXAMENES POR DEFECTO SIEMPRE Y CUANDO EXISTAN EXAMENES
                            listaReporteExamenes.add("TODOS LOS EXAMENES");
                            System.out.println("Exam conf size: " + examenUsuarioNube.getExam_conf().size());
                            //AGREGAMOS EL EXAMEN POR DEFECTO PARA VER TODOS
                            for (int i = 0; i < examenUsuarioNube.getExam_conf().size(); i++) {
                                //AGREGAMOS EN LA LISTA AUXILIAR EL ID DEL EXAMEN
                                listaReporteExamenes.add(examenUsuarioNube.getExam_conf().get(i).getExam_designId() + " - " +
                                        examenUsuarioNube.getExam_conf().get(i).getExam_title());
                            }

                            chars = listaReporteExamenes.toArray(new CharSequence[listaReporteExamenes.size()]);

                            builder.setItems(chars,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            //PROCEDEMOS
                                            ProgressBar progressBar = new ProgressBar(ReporteActivity.this);
                                            DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();
                                            String url;
                                            JSONObject jsonObject = new JSONObject();
                                            ReporteExamenesNube reporteExamenesNube = new ReporteExamenesNube();
                                            String examenSeleccionado = listaReporteExamenes.get(which);
                                            String[] splitExamenSeleccionado = examenSeleccionado.split("-");
                                            int idExamenEscogido = Integer.parseInt(splitExamenSeleccionado[0].trim());
                                            //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                                            url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.GET_REPORT_BY_USER_AND_EXAM + "?idUserPpal=" + encuestador.getUser_id() + "&idExamDesign=" + idExamenEscogido;

                                            new GeneralHttpTask(ReporteActivity.this, progressBar, dynamicTestResponse, "DATA_EXAMS_REPORTS",
                                                    jsonObject, encuestador, examenUsuarioNube, reporteExamenesNube, dialog, examenSeleccionado, passwordUsedForLogin).execute(url);
                                        }
                                    });

                            builder.create().show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Estimado usuario, debe crear una prueba primero para que " +
                                    "aparezca en la lista de examenes disponibles a presentar. Por favor, regrese a la ventana previa.", Toast.LENGTH_LONG).show();
                        }

                    } else {//REPORTE DE USUARIO SECUNDARIO SOLO VE DATA DE PRUEBAS LOCALES SIN EXPORTACION DE NINGUN TIPO
                        Intent intentReportesAdmin = new Intent(getApplicationContext(), ReporteActivity.class);
                        intentReportesAdmin.putExtra("SESSION_USER", encuestador);
                        intentReportesAdmin.putExtra("SESSION_EXAMS", examenUsuarioNube);
                        intentReportesAdmin.putExtra("PASSWORD_USED", passwordUsedForLogin);
                        startActivity(intentReportesAdmin);
                        finish();
                    }
                }else if(item.getItemId() == R.id.goback){
                    Intent intentBack = new Intent(getApplicationContext(), PostLogin.class);
                    intentBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intentBack.putExtra("SESSION_USER", encuestador);
                    intentBack.putExtra("SESSION_EXAMS", examenUsuarioNube);
                    intentBack.putExtra("PASSWORD_USED", passwordUsedForLogin);
                    startActivity(intentBack);
                    finish();
                }
                return false;
            }
        });*/

        }else{//SE MUESTRA MENSAJE DE QUE NO HAY REPORTES
            Toast.makeText(ReporteActivity.this, getResources().getString(R.string.msgReportNoData), Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }

    private void addTableHeaders(){

        try {
            dataReportesRow = new LinearLayout(this);
            //dataReportesRow.setWeightSum(15);
            dataReportesRow.setOrientation(LinearLayout.HORIZONTAL);
            LayoutParams rowParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            dataReportesRow.setLayoutParams(rowParam);
            //CABECERAS
            /*fechaCell = new TextView(this);
            fechaCell.setText(getResources().getString(R.string.tabHeadFechaReport));
            fechaCell.setTextColor(Color.WHITE);
            fechaCell.setTextSize(14);
            LinearLayout.LayoutParams rowParamFecha = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3.5f);
            fechaCell.setLayoutParams(rowParamFecha);
            fechaCell.setPadding(5, 5, 5, 5);
            dataReportesRow.addView(fechaCell);*/
            fechaCell = new TextView(this);
            fechaCell.setText(getResources().getString(R.string.tabHeadFechaReport));
            fechaCell.setTextColor(Color.WHITE);
            fechaCell.setTextSize(12);
            LinearLayout.LayoutParams rowParamFecha = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.reporte_date_width), ViewGroup.LayoutParams.WRAP_CONTENT);
            fechaCell.setLayoutParams(rowParamFecha);
            fechaCell.setPadding(5, 5, 5, 5);
            dataReportesRow.addView(fechaCell);

            usuarioCell = new TextView(this);
            usuarioCell.setText(getResources().getString(R.string.tabHeadUsuarioReport));
            usuarioCell.setTextColor(Color.WHITE);
            usuarioCell.setTextSize(12);
            LinearLayout.LayoutParams rowParamUsuario = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.reporte_user_width), ViewGroup.LayoutParams.WRAP_CONTENT);
            usuarioCell.setLayoutParams(rowParamUsuario);
            usuarioCell.setPadding(5, 5, 5, 5);
            dataReportesRow.addView(usuarioCell);// add the column to the table row here

            idExamCell = new TextView(this);
            //cabeceraIdExam.setId(002);// define id that must be unique
            idExamCell.setText(getResources().getString(R.string.tabHeadExamenReport)); // set the text for the header
            idExamCell.setTextColor(Color.WHITE); // set the color
            idExamCell.setTextSize(12);
            LinearLayout.LayoutParams rowParamIdExam = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.reporte_idexam_width), ViewGroup.LayoutParams.WRAP_CONTENT);
            idExamCell.setLayoutParams(rowParamIdExam);
            idExamCell.setPadding(5, 5, 5, 5); // set the padding (if required)
            dataReportesRow.addView(idExamCell); // add the column to the table row here

            idPreguntaCell = new TextView(this);
            idPreguntaCell.setText(getResources().getString(R.string.tabHeadIdPregReport)); // set the text for the header
            idPreguntaCell.setTextColor(Color.WHITE); // set the color
            idPreguntaCell.setTextSize(12);
            LinearLayout.LayoutParams rowParamIdPreg = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.reporte_idquestion_width), ViewGroup.LayoutParams.WRAP_CONTENT);
            idPreguntaCell.setLayoutParams(rowParamIdPreg);
            idPreguntaCell.setPadding(5, 5, 5, 5); // set the padding (if required)
            dataReportesRow.addView(idPreguntaCell); // add the column to the table row here

            preguntaCell = new TextView(this);
            //cabeceraIdExam.setId(002);// define id that must be unique
            preguntaCell.setText(getResources().getString(R.string.tabHeadPregReport)); // set the text for the header
            preguntaCell.setTextColor(Color.WHITE); // set the color
            preguntaCell.setTextSize(12);
            LinearLayout.LayoutParams rowParamPreg = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.reporte_question_width), ViewGroup.LayoutParams.WRAP_CONTENT);
            preguntaCell.setLayoutParams(rowParamPreg);
            preguntaCell.setPadding(5, 5, 5, 5); // set the padding (if required)
            dataReportesRow.addView(preguntaCell); // add the column to the table row here

            idRespuestaCell = new TextView(this);
            idRespuestaCell.setText(getResources().getString(R.string.tabHeadIdRespReport));
            idRespuestaCell.setTextColor(Color.WHITE);
            idRespuestaCell.setTextSize(12);
            idRespuestaCell.setPadding(5, 5, 5, 5);
            LinearLayout.LayoutParams rowParamIdRes = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.reporte_idanswer_width), ViewGroup.LayoutParams.WRAP_CONTENT);
            idRespuestaCell.setLayoutParams(rowParamIdRes);
            dataReportesRow.addView(idRespuestaCell);// add the column to the table row here

            respuestaCell = new TextView(this);
            respuestaCell.setText(getResources().getString(R.string.tabHeadRespReport));
            respuestaCell.setTextColor(Color.WHITE);
            respuestaCell.setTextSize(12);
            respuestaCell.setPadding(5, 5, 5, 5);
            LinearLayout.LayoutParams rowParamRes = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.reporte_answer_width), ViewGroup.LayoutParams.WRAP_CONTENT);
            respuestaCell.setLayoutParams(rowParamRes);
            dataReportesRow.addView(respuestaCell);// add the column to the table row here

            TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            dataReportesRow.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            dataReportesLayout.addView(dataReportesRow, params);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void addTableData(HashMap<String, OperacionesBdBean> mapaRegistrosReportesPorUsuario) {

        try {

            for (Map.Entry<String, OperacionesBdBean> entry : mapaRegistrosReportesPorUsuario.entrySet()) {
                System.out.println(entry.getKey() + "/" + entry.getValue());

                String diaHora = String.valueOf(entry.getValue().getDia()) +", " +String.valueOf(entry.getValue().getHora())+ " hrs";
                String usuario = entry.getValue().getNombresEncuestado()+entry.getValue().getApellidosEncuestado();
                if(usuario.length() > 20){usuario = usuario.substring(0, 19) + "..";}
                int idExamen = entry.getValue().getIdExamen();
                String pregunta = entry.getValue().getPregunta();
                if(pregunta.length() > 21){pregunta = pregunta.substring(0, 20) + "..";}
                String respuesta = entry.getValue().getRespuesta();
                if(respuesta.length() > 21){respuesta = respuesta.substring(0, 20)+ "..";}
                int idPregunta = entry.getValue().getIdPregunta();
                int idRespuesta = entry.getValue().getIdRespuesta();

                /** ADD NEW TABLE LAYOUT PENDIENTE PARA PROBAR **/
                /*tableLayoutData = new TableLayout(this);
                tableLayoutData.setLayoutParams(new TableLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));*/
                /** CREATE TABLE ROW DINAMICA **/
                dataReportesRow = new LinearLayout(this);
                dataReportesRow.setOrientation(LinearLayout.HORIZONTAL);
                dataReportesRow.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));
                dataReportesRow.setBackgroundColor(getResources().getColor(R.color.colorSecondaryText));

                /** FECHA TEXT VIEW **/
                fechaCell = new TextView(this);
                fechaCell.setText(diaHora);
                fechaCell.setTextColor(Color.BLACK);
                fechaCell.setTextSize(12);
                LinearLayout.LayoutParams rowParamFecha = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.reporte_date_width), ViewGroup.LayoutParams.WRAP_CONTENT);
                fechaCell.setLayoutParams(rowParamFecha);
                fechaCell.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(fechaCell);

                /** USUARIO TEXT VIEW **/
                usuarioCell = new TextView(this);
                usuarioCell.setText(usuario);
                usuarioCell.setTextColor(Color.BLACK);
                usuarioCell.setTextSize(12);
                LinearLayout.LayoutParams rowParamUsuario = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.reporte_user_width), ViewGroup.LayoutParams.WRAP_CONTENT);
                usuarioCell.setLayoutParams(rowParamUsuario);
                usuarioCell.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(usuarioCell);  // Adding textView to tablerow.

                /** ID EXAM TEXT VIEW **/
                idExamCell = new TextView(this);
                idExamCell.setText(String.valueOf(idExamen));
                idExamCell.setTextColor(Color.BLACK);
                idExamCell.setTextSize(12);
                LinearLayout.LayoutParams rowParamIdExam = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.reporte_idexam_width), ViewGroup.LayoutParams.WRAP_CONTENT);
                idExamCell.setLayoutParams(rowParamIdExam);
                idExamCell.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(idExamCell); // Adding textView to tablerow.

                /** ID PREGUNTA TEXT VIEW **/
                idPreguntaCell = new TextView(this);
                idPreguntaCell.setText(String.valueOf(idPregunta));
                idPreguntaCell.setTextColor(Color.BLACK);
                idPreguntaCell.setTextSize(12);
                LinearLayout.LayoutParams rowParamIdPreg = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.reporte_idquestion_width), ViewGroup.LayoutParams.WRAP_CONTENT);
                idPreguntaCell.setLayoutParams(rowParamIdPreg);
                idPreguntaCell.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(idPreguntaCell);

                /** ID PREGUNTA TEXT VIEW **/
                preguntaCell = new TextView(this);
                preguntaCell.setText(pregunta);
                preguntaCell.setTextColor(Color.BLACK);
                preguntaCell.setTextSize(12);
                LinearLayout.LayoutParams rowParamPreg = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.reporte_question_width), ViewGroup.LayoutParams.WRAP_CONTENT);
                preguntaCell.setLayoutParams(rowParamPreg);
                preguntaCell.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(preguntaCell);

                /**ID RESPUESTA TEXT VIEW**/
                idRespuestaCell = new TextView(this);
                idRespuestaCell.setText(String.valueOf(idRespuesta));
                idRespuestaCell.setTextColor(Color.BLACK);
                idRespuestaCell.setTextSize(12);
                LinearLayout.LayoutParams rowParamIdRes = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.reporte_idanswer_width), ViewGroup.LayoutParams.WRAP_CONTENT);
                idRespuestaCell.setLayoutParams(rowParamIdRes);
                idRespuestaCell.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(idRespuestaCell);

                /**ID RESPUESTA TEXT VIEW**/
                respuestaCell = new TextView(this);
                respuestaCell.setText(respuesta);
                respuestaCell.setTextColor(Color.BLACK);
                respuestaCell.setTextSize(12);
                LinearLayout.LayoutParams rowParamRes = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.reporte_answer_width), ViewGroup.LayoutParams.WRAP_CONTENT);
                respuestaCell.setLayoutParams(rowParamRes);
                respuestaCell.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(respuestaCell);

                // Add the TableRow to the TableLayout
                /*tableLayoutData.addView(dataReportesRow, new TableLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));*/

                /*dataReportesLayout.addView(dataReportesRow, new TableLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));*/

                dataReportesLayout.addView(dataReportesRow, new TableLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private boolean exportDataToXls(ExamenUsuarioNube examenUsuarioNube) {

        HashMap<String, ReporteAuxBean> mapaDatosReporte;
        int contador = 0;
        int contadorAuxHeader = 0;
        int contadorCeldaMin = 17;//INICIO DE CELDAS PARA PREGUNTAS
        int totalPreguntasCount = 0;
        int contadorCeldaMax = 0;//MAX CELDAS PARA PREGUNTAS SEGUN TOTAL TAMA&Ntilde;O LISTA PREGUNTAS
        int maxIdPreguntaRegistradoBd = 0;
        boolean success = false;

        try {

            loginDataBaseHelper = new LoginDataBaseHelper(this);
            loginDataBaseHelper = loginDataBaseHelper.open();
            maxIdPreguntaRegistradoBd = loginDataBaseHelper.validarMaxIdPreguntaReporte();
            totalPreguntasCount = examenUsuarioNube.getExam_conf().get(0).getExamUsuarioNubeCurrentConfList().getQuestions().size();

            if(totalPreguntasCount < maxIdPreguntaRegistradoBd){

                totalPreguntasCount = maxIdPreguntaRegistradoBd;
            }

            contadorCeldaMax = totalPreguntasCount * 3;
            contadorCeldaMax = contadorCeldaMax + contadorCeldaMin;

            // check if available and not read only
            if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                Log.i(TAG, "No est\u00e1 disponible el storage del tel\u00e9fono o tableta");
                return false;
            }

            loginDataBaseHelper = new LoginDataBaseHelper(this);
            loginDataBaseHelper = loginDataBaseHelper.open();
            mapaDatosReporte = loginDataBaseHelper.getTotalReporteLinealPorEncuesta(examenUsuarioNube);

            //New Workbook
            Workbook wb = new HSSFWorkbook();

            Cell c = null;

            //CELL STYLE PARA HEADERS
            CellStyle csData = wb.createCellStyle();
            csData.setFillBackgroundColor(HSSFColor.LEMON_CHIFFON.index);
            csData.setFillPattern(HSSFCellStyle.ALIGN_CENTER);

            //CELL STYLE PARA TODA LA DATA
            CellStyle cs = wb.createCellStyle();
            cs.setFillForegroundColor(HSSFColor.WHITE.index);
            cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            //New Sheet
            Sheet sheet1 = null;
            sheet1 = wb.createSheet(getResources().getString(R.string.tituloExcelReport));

            //CABECERA
            Row row = sheet1.createRow(0);
            c = row.createCell(0);
            c.setCellValue(getResources().getString(R.string.encuestadoExcelReport));
            c.setCellStyle(csData);

            c = row.createCell(1);
            c.setCellValue(getResources().getString(R.string.numEncuestaExcelReport));
            c.setCellStyle(csData);
            //DATOS ENCUESTADO
            c = row.createCell(2);
            c.setCellValue(getResources().getString(R.string.diaExcelReport));
            c.setCellStyle(csData);

            c = row.createCell(3);
            c.setCellValue(getResources().getString(R.string.horaExcelReport));
            c.setCellStyle(csData);

            c = row.createCell(4);
            c.setCellValue(getResources().getString(R.string.direccionExcelReport));
            c.setCellStyle(csData);

            c = row.createCell(5);
            c.setCellValue(getResources().getString(R.string.generoExcelReport));
            c.setCellStyle(csData);

            c = row.createCell(6);
            c.setCellValue(getResources().getString(R.string.edadExcelReport));
            c.setCellStyle(csData);

            /*c = row.createCell(7);
            c.setCellValue("CLASE USUARIO");
            c.setCellStyle(csData);*/

            c = row.createCell(7);
            c.setCellValue(getResources().getString(R.string.comunaExcelReport));
            c.setCellStyle(csData);

            /*c = row.createCell(9);
            c.setCellValue("FRECUENCIA");
            c.setCellStyle(csData);

            c = row.createCell(10);
            c.setCellValue("MOTIVO");
            c.setCellStyle(csData);*/

            c = row.createCell(8);
            c.setCellValue(getResources().getString(R.string.ocupExcelReport));
            c.setCellStyle(csData);

            c = row.createCell(9);
            c.setCellValue(getResources().getString(R.string.tiempoTotExcelReport));
            c.setCellStyle(csData);

            /*c = row.createCell(12);
            c.setCellValue("OTROS 1");
            c.setCellStyle(csData);

            c = row.createCell(13);
            c.setCellValue("OTROS 2");
            c.setCellStyle(csData);

            c = row.createCell(14);
            c.setCellValue("OTROS 3");
            c.setCellStyle(csData);

            c = row.createCell(15);
            c.setCellValue("OTROS 4");
            c.setCellStyle(csData);

            c = row.createCell(16);
            c.setCellValue("OTROS 5");
            c.setCellStyle(csData);*/

            //BLOQUE DINAMICO DE PREGUNTAS
            for (int y = contadorCeldaMin; y < contadorCeldaMax; y += 2) {
                contadorAuxHeader++;

                c = row.createCell(y);
                c.setCellValue(getResources().getString(R.string.pregExcelReportAlias) + contadorAuxHeader);
                c.setCellStyle(csData);

                c = row.createCell(y + 1);
                c.setCellValue(getResources().getString(R.string.respExcelReportAlias) + contadorAuxHeader);
                c.setCellStyle(csData);

                /*c = row.createCell(y + 2);
                c.setCellValue("T" + contadorAuxHeader);
                c.setCellStyle(csData);*/
            }

            //ROWS EN BASE A LA DATA
            for (Map.Entry<String, ReporteAuxBean> entry : mapaDatosReporte.entrySet()) {

                //BEAN AUXILIAR DE REPORTE PARA EL ULTIMO CAMBIO DE NEGOCIO
                contador++;
                // Generate column headings
                row = sheet1.createRow(contador);

                c = row.createCell(0);
                c.setCellValue(entry.getValue().getNombresEncuestado() +" "+ entry.getValue().getApellidosEncuestado());
                c.setCellStyle(cs);

                c = row.createCell(1);
                c.setCellValue(entry.getValue().getIdExamen());
                c.setCellStyle(cs);

                c = row.createCell(2);
                c.setCellValue(entry.getValue().getDia());
                c.setCellStyle(cs);

                c = row.createCell(3);
                c.setCellValue(entry.getValue().getHora());
                c.setCellStyle(cs);

                c = row.createCell(4);
                c.setCellValue(entry.getValue().getLocacion());
                c.setCellStyle(cs);

                c = row.createCell(5);
                c.setCellValue(entry.getValue().getGenero());
                c.setCellStyle(cs);

                c = row.createCell(6);
                c.setCellValue(entry.getValue().getEdad());
                c.setCellStyle(cs);

                /*c = row.createCell(7);
                c.setCellValue(entry.getValue().getClaseUsuario());
                c.setCellStyle(cs);*/

                c = row.createCell(7);
                c.setCellValue(entry.getValue().getComuna());
                c.setCellStyle(cs);

                /*c = row.createCell(9);
                c.setCellValue(entry.getValue().getFrecuencia());
                c.setCellStyle(cs);

                c = row.createCell(10);
                c.setCellValue(entry.getValue().getMotivo());
                c.setCellStyle(cs);*/

                c = row.createCell(8);
                c.setCellValue(entry.getValue().getOcupacion());
                c.setCellStyle(cs);

                c = row.createCell(9);
                c.setCellValue(String.valueOf(entry.getValue().getTiempoRespuestaMapaPorLinea()));
                c.setCellStyle(cs);

                /*c = row.createCell(12);
                c.setCellValue(entry.getValue().getOtros1());
                c.setCellStyle(cs);

                c = row.createCell(13);
                c.setCellValue(entry.getValue().getOtros2());
                c.setCellStyle(cs);

                c = row.createCell(14);
                c.setCellValue(entry.getValue().getOtros3());
                c.setCellStyle(cs);

                c = row.createCell(15);
                c.setCellValue(entry.getValue().getOtros4());
                c.setCellStyle(cs);

                c = row.createCell(16);
                c.setCellValue(entry.getValue().getOtros5());
                c.setCellStyle(cs);*/

                //NECESIDAD DE UBICAR CONTADOR EN CELDA POR DEFECTO ANTES DE LOOP
                contadorCeldaMin = 10;
                //BLOQUE DINAMICO DE PREGUNTAS
                for (Map.Entry<String, ReportePreguntaLinealAuxBean> entryPreguntas : entry.getValue().getPreguntaIdMapaPorLinea().entrySet()) {

                    c = row.createCell(contadorCeldaMin);
                    c.setCellValue(entryPreguntas.getValue().getIdPreguntaLinea());
                    c.setCellStyle(cs);

                    c = row.createCell(contadorCeldaMin + 1);
                    c.setCellValue(entryPreguntas.getValue().getIdRespuestaLinea());
                    c.setCellStyle(cs);

                    /*c = row.createCell(contadorCeldaMin + 2);
                    c.setCellValue(entryPreguntas.getValue().getIdTiempoRespuestaLinea() + " segundos");
                    c.setCellStyle(cs);*/

                    //contadorCeldaMin += 3;
                    contadorCeldaMin += 2;
                }//FIN BLOQUE DINAMICO PREGUNTAS

            }//FIN FOR
            sheet1.setColumnWidth(0, (15 * 500));
            sheet1.setColumnWidth(1, (15 * 500));
            sheet1.setColumnWidth(2, (15 * 500));

            // Create a path where we will place our List of objects on external storage
            File file = new File(MemoryBean.getContextBase().getExternalFilesDir(null), getResources().getString(R.string.nombreArchivoExcelReport));
            FileOutputStream os = null;

            try {
                os = new FileOutputStream(file);
                wb.write(os);
                Log.w("FileUtils", "Escribiendo en el archivo " + file);
                success = true;
            } catch (IOException e) {
                Log.w("FileUtils", "Error escribiendo en " + file, e);
            } catch (Exception e) {
                Log.w("FileUtils", "Fall\u00f3 el intento de salvar el archivo xls", e);
            } finally {
                try {
                    if (null != os)
                        os.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(ReporteActivity.this, getResources().getString(R.string.msgErrorGenExcelReport), Toast.LENGTH_LONG).show();
        }

        return success;

    }

    public static boolean isExternalStorageReadOnly(){
        String extStorageState = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)){
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable(){
        String extStorageState = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(extStorageState)){
            return true;
        }
        return false;
    }

    private void exportDataToCsv(PerfilUsuarioNube encuestador) {

        File exportDir = new File(getBaseContext().getFilesDir().getPath(), getResources().getString(R.string.csvExportTitle));
        HashMap<String, OperacionesBdBean> mapaDatosReporte;
        int contador = 0;

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, getResources().getString(R.string.csvArchivoTitle));

        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            loginDataBaseHelper=new LoginDataBaseHelper(this);
            loginDataBaseHelper=loginDataBaseHelper.open();
            mapaDatosReporte = loginDataBaseHelper.getTotalReportesPorUsuario(encuestador);

            String[] nombreColumnas = new String[6];
            nombreColumnas[0] = "ID";
            nombreColumnas[1] = getResources().getString(R.string.tabHeadUsuarioReport);
            nombreColumnas[2] = getResources().getString(R.string.tabHeadExamenReport);
            nombreColumnas[3] = getResources().getString(R.string.columnIdPregBDReport);
            nombreColumnas[4] = getResources().getString(R.string.tabHeadPregReport);
            nombreColumnas[5] = getResources().getString(R.string.columnIdRespBDReport);
            nombreColumnas[6] = getResources().getString(R.string.tabHeadRespReport);
            nombreColumnas[7] = getResources().getString(R.string.columnTiemResBDReport);

            csvWrite.writeNext(nombreColumnas);

            for (Map.Entry<String, OperacionesBdBean> entry : mapaDatosReporte.entrySet()) {
                contador++;
                System.out.println(entry.getKey() + "/" + entry.getValue());
                //Which column you want to exprort
                String arrStr[] = {String.valueOf(contador), entry.getValue().getUserLoginEncuestador(), String.valueOf(entry.getValue().getIdExamen()),
                        String.valueOf(entry.getValue().getIdPregunta()), entry.getValue().getPregunta(),
                        String.valueOf(entry.getValue().getIdRespuesta()), entry.getValue().getRespuesta(),
                        String.valueOf(entry.getValue().getTiempoDeRespuesta())};

                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();

        } catch (Exception sqlEx) {
            sqlEx.printStackTrace();
            Log.e("ReporteActivity", sqlEx.getMessage(), sqlEx);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        if(!encuestador.isUser_ads_disabled()) {
            //ADMOB INTERSTITIAL
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }

        Intent intent = new Intent(ReporteActivity.this, PostLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("SESSION_USER", encuestador);
        intent.putExtra("SESSION_EXAMS", examenUsuarioNube);
        intent.putExtra("PASSWORD_USED", passwordUsedForLogin);
        startActivity(intent);
        finish();
    }

    //ADMOB INTERSTITIAL
    private void requestNewInterstitial() {

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buscador, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem menuItemCompartir = menu.findItem(R.id.action_compartir);
        MenuItem menuItemNoticias = menu.findItem(R.id.action_news);
        MenuItem menuStore = menu.findItem(R.id.action_store);
        menuItemCompartir.setVisible(true);
        menuStore.setVisible(false);
        menuItemNoticias.setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_faq:

                LayoutInflater li = LayoutInflater.from(ReporteActivity.this);

                View vistaDialogoFaq = li.inflate(R.layout.dialog_faq_from_menu, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ReporteActivity.this);

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

            case R.id.action_store:

                LayoutInflater linflater = LayoutInflater.from(ReporteActivity.this);
                View vistaDialogoStore = linflater.inflate(R.layout.dialog_store_from_menu, null);

                AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(
                        ReporteActivity.this);

                alertDialogBuilder2.setView(vistaDialogoStore);
                //VISTAS LAYOUT
                TextView txtTituloProducto = (TextView) vistaDialogoStore.findViewById(R.id.txtTituloProducto);

                Button buttonPayOne = (Button) vistaDialogoStore.findViewById(R.id.buttonPayOne);
                Button buttonPayTwo = (Button) vistaDialogoStore.findViewById(R.id.buttonPayTwo);
                Button buttonPayThree = (Button) vistaDialogoStore.findViewById(R.id.buttonPayThree);
                Button buttonPayFour = (Button) vistaDialogoStore.findViewById(R.id.buttonPayFour);

                if(encuestador.isUser_ads_disabled()) {//SI HAY PAGO MOSTRAMOS MENSAJE Y DESHABILITAMOS BOTON DE PAGO
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
                                    //IN APP BILLING GOOGLE
                                    inAppPayApi.onCreate();
                                    if (Constants.isInAppSetupCreated) {
                                        getPayment(1);
                                    } else {
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
                                    //IN APP BILLING GOOGLE
                                    inAppPayApi.onCreate();
                                    if (Constants.isInAppSetupCreated) {
                                        getPayment(2);
                                    } else {
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
                                    //IN APP BILLING GOOGLE
                                    inAppPayApi.onCreate();
                                    if (Constants.isInAppSetupCreated) {
                                        getPayment(3);
                                    } else {
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
                                    //IN APP BILLING GOOGLE
                                    inAppPayApi.onCreate();
                                    if (Constants.isInAppSetupCreated) {
                                        getPayment(4);
                                    } else {
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
                AlertDialog alertDialog2 = alertDialogBuilder2.create();
                // show it
                alertDialog2.show();
                break;

                case R.id.action_compartir:
                Toast.makeText(ReporteActivity.this, getString(R.string.proxSeccionCompartir), Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        if(!encuestador.isUser_ads_disabled()) {
            //ADMOB INTERSTITIAL
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
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

    @Override
    public void onDestroy() {
        //IN APP BILLING GOOGLE
        inAppPayApi.onDestroy();
        super.onDestroy();
    }

    public void getPayment(int product){
        //IN APP BILLING GOOGLE
        if(Constants.isInAppSetupCreated) {
            if(product == 1) {//DESHABILITA PUBLICIDAD
                inAppPayApi.purchaseRemoveAds();
            }else if(product == 2){//PRUEBAS ILIMITADAS
                inAppPayApi.purchaseUnlimitedTests();//this,encuestador.getUser_id(), 2
            }else if(product == 3){//RESPUESTAS MULTIPLES
                inAppPayApi.purchaseMultipleAnswers();
            }else if(product == 4){//REPORTES PREMIUM
                inAppPayApi.purchasePremiumReports();
            }
        }else{
            Toast.makeText(getBaseContext(), getString(R.string.errorTienda), Toast.LENGTH_LONG).show();
        }
    }
}