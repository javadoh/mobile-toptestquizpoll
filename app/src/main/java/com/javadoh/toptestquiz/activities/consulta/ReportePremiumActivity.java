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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.javadoh.toptestquiz.activities.consulta.bean.premium.ReporteExamenesNube;
import com.javadoh.toptestquiz.R;
import com.javadoh.toptestquiz.activities.consulta.bean.premium.ReporteExamenesNubeDatos;
import com.javadoh.toptestquiz.activities.consulta.bean.premium.ReporteExamenesNubeSubmitted;
import com.javadoh.toptestquiz.activities.PostLogin;
import com.javadoh.toptestquiz.utils.Constants;
import com.javadoh.toptestquiz.utils.GoogleInAppPayUtils;
import com.javadoh.toptestquiz.utils.SendMailTask;
import com.javadoh.toptestquiz.utils.bean.MemoryBean;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioCurrentPreguntas;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lliberal on 19-07-2016.
 */
public class ReportePremiumActivity extends AppCompatActivity {

    private static final String TAG = ReporteActivity.class.getName();
    PerfilUsuarioNube encuestador;
    ExamenUsuarioNube examenUsuarioNube;
    ReporteExamenesNube reporteExamenesNube;
    //CABECERAS DE TABLA
    TextView tituloExamenSeleccionado, idExamCell, nombresPresentador, apellidosPresentador, dniPresentador, diaExamen,
            horaInicioExamen, direccionPresentador, sexoPresentador, notaFinalPresentador, totalTiempoPrueba;
    Button btnExportarDataPremXls;
    TableLayout dataReportesLayout;
    LinearLayout linearExamsTitles;
    TableRow dataReportesRow;
    //EXCEL
    boolean archivoXlsCreado = false;
    //ADMOB
    AdView mAdView;
    //ADMOB INTERSTITIAL
    InterstitialAd mInterstitialAd;
    //IN APP BILLING STORE
    GoogleInAppPayUtils inAppPayApi = new GoogleInAppPayUtils(ReportePremiumActivity.this);
    private Toolbar toolbar;
    String passwordUsedForLogin;
    int arrayPosExamConf, arrayPosReportExam;
    private FloatingActionButton btnFloatRegresar;
    //FLAG VERSION DESPUES DE KITKAT
    boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes_premium);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        //MENU AL FINAL DE LA VISTA
        /*actionToolBar = (Toolbar) findViewById(R.id.postLoginBar);
        actionToolBar.inflateMenu(R.menu.menu_post_login);*/

        Bundle sesion = getIntent().getExtras();
        encuestador = (PerfilUsuarioNube) sesion.get("SESSION_USER");
        examenUsuarioNube = (ExamenUsuarioNube) sesion.get("SESSION_EXAMS");
        reporteExamenesNube = (ReporteExamenesNube) sesion.get("SESSION_REPORTE_NUBE");
        passwordUsedForLogin = sesion.getString("PASSWORD_USED");
        final String examenidTitulo = sesion.getString("SESSION_EXAM_SELECTED");
        String[] splitExamenSeleccionado = examenidTitulo.split("-");
        final int idExamenSeleccionado = Integer.parseInt(splitExamenSeleccionado[0].trim());

        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        if(!encuestador.isUser_ads_disabled()) {
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

        //ELEMENTOS DE VISTA
        tituloExamenSeleccionado = (TextView) findViewById(R.id.tituloExamenSeleccionado);
        tituloExamenSeleccionado.setText(splitExamenSeleccionado[1]);

        btnExportarDataPremXls = (Button) findViewById(R.id.btnExportarExcel);
        btnFloatRegresar = (FloatingActionButton) findViewById(R.id.btnFloatRegresar);
        if(!isKitKat){
            btnExportarDataPremXls.setBackgroundColor(getResources().getColor(R.color.color_primary_programatically));
        }

        btnExportarDataPremXls.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                archivoXlsCreado = exportDataToXls();

                if (archivoXlsCreado) {
                    String msg = getResources().getString(R.string.msgGenExcelReport)+
                            MemoryBean.getContextBase().getExternalFilesDir(null)+getResources().getString(R.string.nombreArchivoExcelReport);
                    Toast.makeText(getBaseContext().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    //ENVIAMOS EMAIL CON EL ARCHIVO GENERADO
                    if(encuestador.isUser_has_premium_reports()) {// SE ENVÍA UN EMAIL CON EL EXCEL COMPLETO SOLO SI ES UN USUARIO PREMIUM
                        sendEmailWithXlsGenerated(getResources().getString(R.string.nombreArchivoExcelReport));
                    }
                } else {
                    Toast.makeText(getBaseContext().getApplicationContext(), getResources().getString(R.string.msgErrorGenExcelReport), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnFloatRegresar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //CERRAMOS LA ACTIVIDAD DE CREAR PRUEBAS
                Intent intentRegresar = new Intent(ReportePremiumActivity.this, PostLogin.class);
                intentRegresar.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentRegresar.putExtra("SESSION_USER", encuestador);
                intentRegresar.putExtra("SESSION_EXAMS", examenUsuarioNube);
                intentRegresar.putExtra("PASSWORD_USED", passwordUsedForLogin);
                startActivity(intentRegresar);
                finish();
            }
        });


        linearExamsTitles = (LinearLayout) findViewById(R.id.linearExamsTitles);

        //SOLUCION A DIFERENCIA DE CRITERIO EN MODELO ENTRE SIZE DE ARREGLO Y DESIGN ID EXAM
        for(int x = 0; x < examenUsuarioNube.getExam_conf().size(); x++){

            if(idExamenSeleccionado == examenUsuarioNube.getExam_conf().get(x).getExam_designId()){
                arrayPosExamConf = x;
            }
        }
        for(int y = 0; y < reporteExamenesNube.getExamsCreated().size(); y++){

            if(idExamenSeleccionado == reporteExamenesNube.getExamsCreated().get(y).getExamDesignId()){
                arrayPosReportExam = y;
            }
        }

        dataReportesLayout = (TableLayout) findViewById(R.id.dataPremReportesTable);
        //TITULO DEL EXAMEN ANTES DE LOS HEADERS DE LA TABLA
        addTableHeaders();
        //POBLADO DE TABLA
        addTableData();

        //final int idExamen = Integer.parseInt(examenidTitulo.replace("-", ""));

        //INVOCAMOS A LA TAREA ASYNCRONA ENVIANDO NUESTROS DATOS Y BEAN PARA SER GUARDADOS EN EL SERVIDOR
        //new GeneralHttpTask(ReportePremiumActivity.this, progressBar, dynamicTestResponse, metodoRest, jsonObject, usuarioPerfil).execute(url);

        /*actionToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                if (item.getItemId() == R.id.create) {
                    if(passwordUsedForLogin != null) {//ES EL ADMINISTRADOR
                        //CREAR INTENT DE REGISTRO Y COMIENZO DE LA ACTIVIDAD EN LA APP
                        Intent intentCrearPrueba = new Intent(getApplicationContext(), CrearPrueba.class);
                        intentCrearPrueba.putExtra("SESSION_USER", encuestador);
                        intentCrearPrueba.putExtra("SESSION_EXAMS", examenUsuarioNube);
                        intentCrearPrueba.putExtra("PASSWORD_USED", passwordUsedForLogin);
                        startActivity(intentCrearPrueba);
                        finish();
                    }else{
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

                    if(passwordUsedForLogin != null) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ReportePremiumActivity.this);
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
                                            ProgressBar progressBar = new ProgressBar(ReportePremiumActivity.this);
                                            DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();
                                            String url;
                                            JSONObject jsonObject = new JSONObject();
                                            ReporteExamenesNube reporteExamenesNube = new ReporteExamenesNube();
                                            String examenSeleccionado = listaReporteExamenes.get(which);
                                            String[] splitExamenSeleccionado = examenSeleccionado.split("-");
                                            int idExamenEscogido = Integer.parseInt(splitExamenSeleccionado[0].trim());
                                            //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                                            url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.GET_REPORT_BY_USER_AND_EXAM + "?idUserPpal=" + encuestador.getUser_id() + "&idExamDesign=" + idExamenEscogido;

                                            new GeneralHttpTask(ReportePremiumActivity.this, progressBar, dynamicTestResponse, "DATA_EXAMS_REPORTS",
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

    }

    private void addTableHeaders(){

        try {
            dataReportesRow = new TableRow(this);
            dataReportesRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            //CABECERAS
            idExamCell = new TextView(this);
            idExamCell.setText(getResources().getString(R.string.tabHeadExamenReport)); // set the text for the header
            idExamCell.setTextColor(Color.WHITE); // set the color
            idExamCell.setTextSize(16);
            idExamCell.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            idExamCell.setPadding(5, 5, 5, 5); // set the padding (if required)
            dataReportesRow.addView(idExamCell); // add the column to the table row here

            nombresPresentador = new TextView(this);
            nombresPresentador.setText(getResources().getString(R.string.nombresExcelReportPrem));
            nombresPresentador.setTextColor(Color.WHITE); // set the color
            nombresPresentador.setTextSize(16);
            nombresPresentador.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            nombresPresentador.setPadding(5, 5, 5, 5); // set the padding (if required)
            dataReportesRow.addView(nombresPresentador); // add the column to the table row here

            apellidosPresentador = new TextView(this);
            apellidosPresentador.setText(getResources().getString(R.string.apellidosExcelReportPrem));
            apellidosPresentador.setTextColor(Color.WHITE); // set the color
            apellidosPresentador.setTextSize(16);
            apellidosPresentador.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            apellidosPresentador.setPadding(5, 5, 5, 5); // set the padding (if required)
            dataReportesRow.addView(apellidosPresentador); // add the column to the table row here

            dniPresentador = new TextView(this);
            dniPresentador.setText(getResources().getString(R.string.tabHeadDniReport));
            dniPresentador.setTextColor(Color.WHITE);
            dniPresentador.setTextSize(16);
            dniPresentador.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            dniPresentador.setPadding(5, 5, 5, 5);
            dataReportesRow.addView(dniPresentador);

                diaExamen = new TextView(this);
                diaExamen.setText(getResources().getString(R.string.diaExcelReport));
                diaExamen.setTextColor(Color.WHITE);
                diaExamen.setTextSize(16);
                diaExamen.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                diaExamen.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(diaExamen);

                horaInicioExamen = new TextView(this);
                horaInicioExamen.setText(getResources().getString(R.string.horaExcelReport));
                horaInicioExamen.setTextColor(Color.WHITE);
                horaInicioExamen.setTextSize(16);
                horaInicioExamen.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                horaInicioExamen.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(horaInicioExamen);

                direccionPresentador = new TextView(this);
                direccionPresentador.setText(getResources().getString(R.string.locacionExcelReportPrem));
                direccionPresentador.setTextColor(Color.WHITE);
                direccionPresentador.setTextSize(16);
                direccionPresentador.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                direccionPresentador.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(direccionPresentador);

                sexoPresentador = new TextView(this);
                sexoPresentador.setText(getResources().getString(R.string.sexoExcelReportPrem));
                sexoPresentador.setTextColor(Color.WHITE);
                sexoPresentador.setTextSize(16);
                sexoPresentador.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                sexoPresentador.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(sexoPresentador);

            notaFinalPresentador = new TextView(this);
            notaFinalPresentador.setText(getResources().getString(R.string.notaFinExcelReportPrem));
            notaFinalPresentador.setTextColor(Color.WHITE);
            notaFinalPresentador.setTextSize(16);
            notaFinalPresentador.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            notaFinalPresentador.setPadding(5, 5, 5, 5);
            dataReportesRow.addView(notaFinalPresentador);

            totalTiempoPrueba = new TextView(this);
            totalTiempoPrueba.setText(getResources().getString(R.string.tiempoTotExcelReport));
            totalTiempoPrueba.setTextColor(Color.WHITE);
            totalTiempoPrueba.setTextSize(16);
            totalTiempoPrueba.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            totalTiempoPrueba.setPadding(5, 5, 5, 5);
            dataReportesRow.addView(totalTiempoPrueba);

            //RECORREMOS EL TOTAL DE PREGUNTAS ACTUALES DEL EXAMEN SELECCIONADO
            for(ExamenUsuarioCurrentPreguntas examenPreguntasNube: examenUsuarioNube.getExam_conf().get(arrayPosExamConf).getExamUsuarioNubeCurrentConfList().getQuestions()){

                    //PREGUNTA
                    TextView idPreguntaCell = new TextView(this);
                    idPreguntaCell.setText(getResources().getString(R.string.tabHeadPregReport) + examenPreguntasNube.getId());
                    idPreguntaCell.setTextColor(Color.WHITE);
                    idPreguntaCell.setTextSize(16);
                    idPreguntaCell.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    idPreguntaCell.setPadding(5, 5, 5, 5);
                    dataReportesRow.addView(idPreguntaCell);
                    //RESPUESTA SELECCIONADA
                    TextView respuestaSelecPregCell = new TextView(this);
                    respuestaSelecPregCell.setText(getResources().getString(R.string.respSelecExcelReportPrem));
                    respuestaSelecPregCell.setTextColor(Color.WHITE);
                    respuestaSelecPregCell.setTextSize(16);
                    respuestaSelecPregCell.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    respuestaSelecPregCell.setPadding(5, 5, 5, 5);
                    dataReportesRow.addView(respuestaSelecPregCell);

                    if(encuestador.isUser_has_premium_reports()) {//SOLO SE MUESTRA SI ES PREMIUM EL USUARIO
                    //RESPUESTA CORRECTA
                    TextView respuestaCorrectaPregCell = new TextView(this);
                    respuestaCorrectaPregCell.setText(getResources().getString(R.string.respCorrExcelReportPrem));
                    respuestaCorrectaPregCell.setTextColor(Color.WHITE);
                    respuestaCorrectaPregCell.setTextSize(16);
                    respuestaCorrectaPregCell.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    respuestaCorrectaPregCell.setPadding(5, 5, 5, 5);
                    dataReportesRow.addView(respuestaCorrectaPregCell);
                    //TIEMPO DE RESPUESTA
                    /*TextView tiempoRespuestaCell = new TextView(this);
                    tiempoRespuestaCell.setText("TIEMPO DE RESPUESTA");
                    tiempoRespuestaCell.setTextColor(Color.WHITE);
                    tiempoRespuestaCell.setTextSize(16);
                    tiempoRespuestaCell.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tiempoRespuestaCell.setPadding(5, 5, 5, 5);
                    dataReportesRow.addView(tiempoRespuestaCell);*/
                    }
                }

            // Add the TableRow to the TableLayout
            // Add the TableRow to the TableLayout
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            dataReportesRow.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            dataReportesLayout.addView(dataReportesRow, params);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addTableData() {

        /** This function add the data to the table **/
        int contador = 0;

        try {

            TableLayout.LayoutParams params2 = new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            //RECORREMOS EL TOTAL DE PREGUNTAS ACTUALES DEL EXAMEN SELECCIONADO
            for(ReporteExamenesNubeSubmitted reporteExamenNube: reporteExamenesNube.getExamsCreated().get(arrayPosReportExam).getUserSubmittedExam()){

                //ROW DINAMICA
                dataReportesRow = new TableRow(this);
                dataReportesRow.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                //DATOS UNICOS POR EXAMEN
                idExamCell = new TextView(this);
                idExamCell.setText(String.valueOf(reporteExamenNube.getId()));
                idExamCell.setTextColor(Color.WHITE);
                idExamCell.setTextSize(16);
                idExamCell.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                idExamCell.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(idExamCell);

                nombresPresentador = new TextView(this);
                nombresPresentador.setText(reporteExamenNube.getNombres() != null ? reporteExamenNube.getNombres() : "");
                nombresPresentador.setTextColor(Color.WHITE);
                nombresPresentador.setTextSize(16);
                nombresPresentador.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                nombresPresentador.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(nombresPresentador);

                apellidosPresentador = new TextView(this);
                apellidosPresentador.setText(reporteExamenNube.getApellidos() != null ? reporteExamenNube.getApellidos() : "");
                apellidosPresentador.setTextColor(Color.WHITE);
                apellidosPresentador.setTextSize(16);
                apellidosPresentador.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                apellidosPresentador.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(apellidosPresentador);

                dniPresentador = new TextView(this);
                dniPresentador.setText(reporteExamenNube.getDni() != null ? reporteExamenNube.getDni() : "");
                dniPresentador.setTextColor(Color.WHITE);
                dniPresentador.setTextSize(16);
                dniPresentador.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                dniPresentador.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(dniPresentador);

                    diaExamen = new TextView(this);
                    diaExamen.setText(reporteExamenNube.getDia() != null ? reporteExamenNube.getDia() : "");
                    diaExamen.setTextColor(Color.WHITE);
                    diaExamen.setTextSize(16);
                    diaExamen.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    diaExamen.setPadding(5, 5, 5, 5);
                    dataReportesRow.addView(diaExamen);

                    horaInicioExamen = new TextView(this);
                    horaInicioExamen.setText(reporteExamenNube.getHoraInicio() != null ? reporteExamenNube.getHoraInicio() : "");
                    horaInicioExamen.setTextColor(Color.WHITE);
                    horaInicioExamen.setTextSize(16);
                    horaInicioExamen.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    horaInicioExamen.setPadding(5, 5, 5, 5);
                    dataReportesRow.addView(horaInicioExamen);

                    direccionPresentador = new TextView(this);
                    direccionPresentador.setText(reporteExamenNube.getDireccion() != null ? reporteExamenNube.getDireccion() : "");
                    direccionPresentador.setTextColor(Color.WHITE);
                    direccionPresentador.setTextSize(16);
                    direccionPresentador.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    direccionPresentador.setPadding(5, 5, 5, 5);
                    dataReportesRow.addView(direccionPresentador);

                    sexoPresentador = new TextView(this);
                    sexoPresentador.setText(reporteExamenNube.getSexo() != null ? reporteExamenNube.getSexo() : "");
                    sexoPresentador.setTextColor(Color.WHITE);
                    sexoPresentador.setTextSize(16);
                    sexoPresentador.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    sexoPresentador.setPadding(5, 5, 5, 5);
                    dataReportesRow.addView(sexoPresentador);

                notaFinalPresentador = new TextView(this);
                notaFinalPresentador.setText(String.valueOf(reporteExamenNube.getNotaFinal()));
                notaFinalPresentador.setTextColor(Color.WHITE);
                notaFinalPresentador.setTextSize(16);
                notaFinalPresentador.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                notaFinalPresentador.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(notaFinalPresentador);

                totalTiempoPrueba = new TextView(this);
                totalTiempoPrueba.setText(String.valueOf(reporteExamenNube.getTotalTiempoPrueba()));
                totalTiempoPrueba.setTextColor(Color.WHITE);
                totalTiempoPrueba.setTextSize(16);
                totalTiempoPrueba.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                totalTiempoPrueba.setPadding(5, 5, 5, 5);
                dataReportesRow.addView(totalTiempoPrueba);

                for(ReporteExamenesNubeDatos examenDatosNube : reporteExamenNube.getDatosPrueba()){

                    //PREGUNTA
                    TextView idPreguntaCell = new TextView(this);
                    idPreguntaCell.setText(String.valueOf(examenDatosNube.getIdPregunta()));
                    idPreguntaCell.setTextColor(Color.WHITE);
                    idPreguntaCell.setTextSize(16);
                    idPreguntaCell.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    idPreguntaCell.setPadding(5, 5, 5, 5);
                    dataReportesRow.addView(idPreguntaCell);
                    //RESPUESTA SELECCIONADA
                    TextView respuestaSelecPregCell = new TextView(this);
                    respuestaSelecPregCell.setText(String.valueOf(examenDatosNube.getRespuestaSeleccionada()));
                    respuestaSelecPregCell.setTextColor(Color.WHITE);
                    respuestaSelecPregCell.setTextSize(16);
                    respuestaSelecPregCell.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    respuestaSelecPregCell.setPadding(5, 5, 5, 5);
                    dataReportesRow.addView(respuestaSelecPregCell);

                    if(encuestador.isUser_has_premium_reports()) {//SOLO SE MUESTRA SI ES PREMIUM EL USUARIO
                        //RESPUESTA CORRECTA
                        TextView respuestaCorrectaPregCell = new TextView(this);
                        respuestaCorrectaPregCell.setText(String.valueOf(examenDatosNube.getRespuestaCorrecta()));
                        respuestaCorrectaPregCell.setTextColor(Color.WHITE);
                        respuestaCorrectaPregCell.setTextSize(16);
                        respuestaCorrectaPregCell.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                        respuestaCorrectaPregCell.setPadding(5, 5, 5, 5);
                        dataReportesRow.addView(respuestaCorrectaPregCell);
                        //TIEMPO DE RESPUESTA
                        /*TextView tiempoRespuestaCell = new TextView(this);
                        tiempoRespuestaCell.setText(String.valueOf(examenDatosNube.getTiempoRespuesta()));
                        tiempoRespuestaCell.setTextColor(Color.WHITE);
                        tiempoRespuestaCell.setTextSize(16);
                        tiempoRespuestaCell.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tiempoRespuestaCell.setPadding(5, 5, 5, 5);
                        dataReportesRow.addView(tiempoRespuestaCell);*/

                    }

                }
                // Add the TableRow to the TableLayout
                dataReportesRow.setBackgroundColor(getResources().getColor(R.color.textBackDarkGray));
                dataReportesLayout.addView(dataReportesRow, params2);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private boolean exportDataToXls() {

        int contador = 0;
        boolean success = false;
        try {
            // check if available and not read only
            if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                Log.i(TAG, "No est\u00e1 disponible el storage del tel\u00e9fono o tableta");
                return false;
            }

            //NUEVO WORKBOOK EXCEL
            Workbook wb = new HSSFWorkbook();
            //NUEVA CELDA EXCEL
            Cell c;

            //CELL STYLE PARA HEADERS
            CellStyle csData = wb.createCellStyle();
            csData.setFillBackgroundColor(HSSFColor.LEMON_CHIFFON.index);
            csData.setFillPattern(HSSFCellStyle.ALIGN_CENTER);

            //CELL STYLE PARA TODA LA DATA
            CellStyle cs = wb.createCellStyle();
            cs.setFillForegroundColor(HSSFColor.WHITE.index);
            cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            //NUEVA HOJA EXCEL
            Sheet sheet1;
            sheet1 = wb.createSheet(getResources().getString(R.string.tituloExcelReport));

            //CABECERA
            Row row = sheet1.createRow(0);
            c = row.createCell(0);
            c.setCellValue(getResources().getString(R.string.tabHeadExamenReport));
            c.setCellStyle(csData);

            c = row.createCell(1);
            c.setCellValue(getResources().getString(R.string.nombresExcelReportPrem));
            c.setCellStyle(csData);
            //DATOS ENCUESTADO
            c = row.createCell(2);
            c.setCellValue(getResources().getString(R.string.apellidosExcelReportPrem));
            c.setCellStyle(csData);

            c = row.createCell(3);
            c.setCellValue(getResources().getString(R.string.tabHeadDniReport));
            c.setCellStyle(csData);

                c = row.createCell(4);
                c.setCellValue(getResources().getString(R.string.diaExcelReport));
                c.setCellStyle(csData);

                c = row.createCell(5);
                c.setCellValue(getResources().getString(R.string.horaExcelReport));
                c.setCellStyle(csData);

                c = row.createCell(6);
                c.setCellValue(getResources().getString(R.string.direccionExcelReport));
                c.setCellStyle(csData);

                c = row.createCell(7);
                c.setCellValue(getResources().getString(R.string.generoExcelReport));
                c.setCellStyle(csData);

            c = row.createCell(8);
            c.setCellValue(getResources().getString(R.string.notaFinExcelReportPrem));
            c.setCellStyle(csData);

            c = row.createCell(9);
            c.setCellValue(getResources().getString(R.string.tiempoTotExcelReport));
            c.setCellStyle(csData);

            int contadorElementosCabeceraExcel = 10;
            //RECORREMOS EL TOTAL DE PREGUNTAS ACTUALES DEL EXAMEN SELECCIONADO
            for(ExamenUsuarioCurrentPreguntas examenPreguntasNube: examenUsuarioNube.getExam_conf().get(arrayPosExamConf).getExamUsuarioNubeCurrentConfList().getQuestions()){
                    //PREGUNTA
                    c = row.createCell(++contadorElementosCabeceraExcel);
                    c.setCellValue(getResources().getString(R.string.tabHeadPregReport) + examenPreguntasNube.getId());
                    c.setCellStyle(csData);
                    //RESPUESTA SELECCIONADA
                    c = row.createCell(++contadorElementosCabeceraExcel);
                    c.setCellValue(getResources().getString(R.string.respSelecExcelReportPrem));
                    c.setCellStyle(csData);

                    if(encuestador.isUser_has_premium_reports()) {//SOLO SE MUESTRA SI ES PREMIUM EL USUARIO
                    //RESPUESTA CORRECTA
                    c = row.createCell(++contadorElementosCabeceraExcel);
                    c.setCellValue(getResources().getString(R.string.respCorrExcelReportPrem));
                    c.setCellStyle(csData);
                    //TIEMPO DE RESPUESTA
                    /*c = row.createCell(++contadorElementosCabeceraExcel);
                    c.setCellValue("TIEMPO RESPUESTA");
                    c.setCellStyle(csData);*/
                    }
            }

            //RECORREMOS EL TOTAL DE PREGUNTAS ACTUALES DEL EXAMEN SELECCIONADO
            for(ReporteExamenesNubeSubmitted reporteExamenNube: reporteExamenesNube.getExamsCreated().get(arrayPosReportExam).getUserSubmittedExam()){
                //DATOS DE UNICOS POR EXAMEN EN EXCEL (UNA ROW)
                contador++;
                // Generate column headings
                row = sheet1.createRow(contador);
                //ID EXAMEN
                c = row.createCell(0);
                c.setCellValue(reporteExamenNube.getId());
                c.setCellStyle(cs);

                c = row.createCell(1);
                c.setCellValue(reporteExamenNube.getNombres());
                c.setCellStyle(cs);

                c = row.createCell(2);
                c.setCellValue(reporteExamenNube.getApellidos());
                c.setCellStyle(cs);

                c = row.createCell(3);
                c.setCellValue(reporteExamenNube.getDni());
                c.setCellStyle(cs);

                    c = row.createCell(4);
                    c.setCellValue(reporteExamenNube.getDia());
                    c.setCellStyle(cs);

                    c = row.createCell(5);
                    c.setCellValue(reporteExamenNube.getHoraInicio());
                    c.setCellStyle(cs);

                    c = row.createCell(6);
                    c.setCellValue(reporteExamenNube.getDireccion());
                    c.setCellStyle(cs);

                    c = row.createCell(7);
                    c.setCellValue(reporteExamenNube.getSexo());
                    c.setCellStyle(cs);

                c = row.createCell(8);
                c.setCellValue(reporteExamenNube.getNotaFinal());
                c.setCellStyle(cs);

                c = row.createCell(9);
                c.setCellValue(reporteExamenNube.getTotalTiempoPrueba());
                c.setCellStyle(cs);

                //NECESIDAD DE UBICAR CONTADOR EN CELDA POR DEFECTO ANTES DE LOOP
                int contadorElementosCabeceraExcelAux = 10;

                for(ReporteExamenesNubeDatos examenDatosNube : reporteExamenNube.getDatosPrueba()){

                    c = row.createCell(++contadorElementosCabeceraExcelAux);
                    c.setCellValue(examenDatosNube.getIdPregunta());
                    c.setCellStyle(cs);

                    c = row.createCell(++contadorElementosCabeceraExcelAux);
                    c.setCellValue(examenDatosNube.getRespuestaSeleccionada());
                    c.setCellStyle(cs);

                    if(encuestador.isUser_has_premium_reports()) {//SOLO SE MUESTRA SI ES PREMIUM EL USUARIO
                        c = row.createCell(++contadorElementosCabeceraExcelAux);
                        c.setCellValue(examenDatosNube.getRespuestaCorrecta());
                        c.setCellStyle(cs);

                        /*c = row.createCell(++contadorElementosCabeceraExcelAux);
                        c.setCellValue(examenDatosNube.getTiempoRespuesta() + " segundos");
                        c.setCellStyle(cs);*/

                        contadorElementosCabeceraExcelAux += 2;
                    }else{
                        contadorElementosCabeceraExcelAux += 3;
                    }
                }
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
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(ReportePremiumActivity.this, getResources().getString(R.string.msgErrorGenExcelReport), Toast.LENGTH_SHORT).show();
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

        Intent intent = new Intent(ReportePremiumActivity.this, PostLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("SESSION_USER", encuestador);
        intent.putExtra("SESSION_EXAMS", examenUsuarioNube);
        intent.putExtra("PASSWORD_USED", passwordUsedForLogin);
        startActivity(intent);
        finish();
    }

    private boolean sendEmailWithXlsGenerated(String filename){
        boolean enviado = false;

        try {
            new SendMailTask(this).execute(getResources().getString(R.string.emailEmisor),
                    getResources().getString(R.string.emailPassEmisor), encuestador.getUser_email().trim(), Html.fromHtml(getString(R.string.asuntoEmailSignUpSend)),
                    Html.fromHtml(getString(R.string.bodyEmailSignUpSend) + encuestador.getUser_login() +
                            getString(R.string.bodyEmailSignUpSend2) + encuestador.getUser_password() +
                            getString(R.string.bodyEmailSignUpSend3) + encuestador.getUser_secondary_password()), filename);

        }catch (Exception e) {
        Log.d(TAG, "Error: "+e);
            Toast.makeText(this, getResources().getString(R.string.emailSendError), Toast.LENGTH_SHORT).show();
        }
        return enviado;
    }

    //ADMOB INTERSTITIAL
    private void requestNewInterstitial() {

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void getPayment(int product){
        //IN APP BILLING GOOGLE
        if(Constants.isInAppSetupCreated) {
            if(product == 1) {//DESHABILITA PUBLICIDAD
                inAppPayApi.purchaseRemoveAds();//this, encuestador.getUser_id(), 1
            }else if(product == 2){//PRUEBAS ILIMITADAS
                inAppPayApi.purchaseUnlimitedTests();
            }else if(product == 3){//RESPUESTAS MULTIPLES
                inAppPayApi.purchaseMultipleAnswers();
            }else if(product == 4){//REPORTES PREMIUM
                inAppPayApi.purchasePremiumReports();
            }
        }else{
            Toast.makeText(getBaseContext(), getString(R.string.errorTienda), Toast.LENGTH_LONG).show();
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

        MenuItem menuItemCompartir = menu.findItem(R.id.action_compartir);
        MenuItem menuTienda = menu.findItem(R.id.action_store);
        menuItemCompartir.setVisible(true);
        menuTienda.setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_faq:

                LayoutInflater li = LayoutInflater.from(ReportePremiumActivity.this);

                View vistaDialogoFaq = li.inflate(R.layout.dialog_faq_from_menu, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ReportePremiumActivity.this);

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

                LayoutInflater linflater = LayoutInflater.from(ReportePremiumActivity.this);
                View vistaDialogoStore = linflater.inflate(R.layout.dialog_store_from_menu, null);

                AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(
                        ReportePremiumActivity.this);

                alertDialogBuilder2.setView(vistaDialogoStore);
                //VISTAS LAYOUT
                TextView txtTituloProducto = (TextView) vistaDialogoStore.findViewById(R.id.txtTituloProducto);

                Button buttonPayOne = (Button) vistaDialogoStore.findViewById(R.id.buttonPayOne);
                Button buttonPayTwo = (Button) vistaDialogoStore.findViewById(R.id.buttonPayTwo);
                Button buttonPayThree = (Button) vistaDialogoStore.findViewById(R.id.buttonPayThree);
                Button buttonPayFour = (Button) vistaDialogoStore.findViewById(R.id.buttonPayFour);

                if(encuestador.isUser_ads_disabled()) {//SI HAY PAGO MOSTRAMOS MENSAJE Y DESHABILITAMOS BOTON DE PAGO
                   // txtTituloProducto.setText(R.string.compra_realizada_store);
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
                Toast.makeText(ReportePremiumActivity.this, getString(R.string.proxSeccionCompartir), Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
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
    protected void onDestroy() {
        //IN APP BILLING GOOGLE
        inAppPayApi.onDestroy();
        super.onDestroy();
    }
}
