package com.javadoh.toptestquiz.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.javadoh.toptestquiz.R;
import com.javadoh.toptestquiz.utils.Constants;
import com.javadoh.toptestquiz.utils.GeneralHttpTask;
import com.javadoh.toptestquiz.utils.GoogleInAppPayUtils;
import com.javadoh.toptestquiz.utils.bean.DynamicTestResponse;
import com.javadoh.toptestquiz.utils.bean.exam.ExamUsuarioCurrentRespDetalle;
import com.javadoh.toptestquiz.utils.bean.exam.ExamUsuarioNubeConf;
import com.javadoh.toptestquiz.utils.bean.exam.ExamUsuarioNubeCurrentConf;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioCurrentPreguntas;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioCurrentRespuestas;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioNube;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioNube;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by luiseliberal on 10/04/16.
 */
public class CrearPrueba extends AppCompatActivity {

    private static final String TAG = CrearPrueba.class.getName();
    EditText tituloPrueba, examPass, examTotalTimeMinutes, textoPregunta, respuestaA, respuestaB, respuestaC, respuestaD,
            textoUploadImg, respuestaCorrecta, textoPreguntaDyn, respuestaADyn, respuestaBDyn, respuestaCDyn,
            respuestaDDyn, respuestaCorrectaDyn, textoUploadImgDyn;
    Button btnSeleccionarImg, btnUploadData, btnSeleccionarImgDyn, btnRegresar;
    Button btnAdd, btnDelete, btnAddDyn, btnDeleteDyn;
    ProgressDialog progressDialog;
    TextView mensajeCargaDatos, mensajeCargaDatosDyn;
    //VARIABLES DE IMAGENES
    private static final int PICK_IMAGE = 1;
    private static final int SELECT_PHOTO = 100;
    private ImageView imgToUpload, imgToUploadDyn;
    private Bitmap bitmap;
    private String decodedImg, passwordUsedForLogin, imgPathPhoneAux;
    String url = "";
    JSONObject jsonObject;

    private List<ExamUsuarioNubeConf> listaExamUsuarioNubeConf;
    private List<ExamUsuarioCurrentRespDetalle> listaRespuestasDetallePorPregunta;
    private List<ExamenUsuarioCurrentPreguntas> listaPreguntas;
    private List<ExamenUsuarioCurrentRespuestas> listaRespuestas;
    LinearLayout mainLinearLayout, subLayout, subLayoutFloats, subLinearBtnGuardar, linearLayoutDyn, linearRespCorBtnImg, linearImgBtnFloats;
    private int contadorElementosDynCorrelativo = 0;//SUPUESTO ULTIMO ELEMENTO DEL FORM PRINCIPAL PARA PASAR A LOS DINAMICOS
    PerfilUsuarioNube usuarioPerfil;
    ExamenUsuarioNube examenUsuarioPerfil;
    //ADMOB
    //AdView mAdView;
    //ADMOB INTERSTITIAL
    InterstitialAd mInterstitialAd;
    private Toolbar toolbar;
    //IN APP BILLING STORE
    GoogleInAppPayUtils inAppPayApi = new GoogleInAppPayUtils(CrearPrueba.this);
    //FLAG VERSION DESPUES DE KITKAT
    boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    boolean isLollyPop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    private ScrollView scrollView;
    private List<String> listaReporteExamenes;
    private float scaleWindow;
    private DisplayMetrics metrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_prueba_usuario);
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setLogo(R.mipmap.ic_launcher);
            setSupportActionBar(toolbar);

            //MENU AL FINAL DE LA VISTA
            /*actionToolBar = (Toolbar) findViewById(R.id.postLoginBar);
            actionToolBar.inflateMenu(R.menu.menu_post_login);*/

            scrollView = (ScrollView) findViewById(R.id.scrollViewId);
            //BLOQUE DIV DINAMICO
            tituloPrueba = (EditText) findViewById(R.id.tituloPrueba);
            examPass = (EditText) findViewById(R.id.examPass);
            examTotalTimeMinutes = (EditText) findViewById(R.id.examTotalTimeMinutes);
            textoPregunta = (EditText) findViewById(R.id.textoPregunta);
            respuestaA = (EditText) findViewById(R.id.respuestaA);
            respuestaB = (EditText) findViewById(R.id.respuestaB);
            respuestaC = (EditText) findViewById(R.id.respuestaC);
            respuestaD = (EditText) findViewById(R.id.respuestaD);
            respuestaCorrecta = (EditText) findViewById(R.id.respuestaCorrectaId);
            btnSeleccionarImg = (Button) findViewById(R.id.btnSeleccionarImg);
            mensajeCargaDatos = (TextView) findViewById(R.id.mensajeCargaDatos);
            textoUploadImg = (EditText) findViewById(R.id.textoImgUpload);
            imgToUpload = (ImageView) findViewById(R.id.imageToUpload);
            //LUIS 300317 ERROR DE DRAWING CACHE NULL
            imgToUpload.setDrawingCacheEnabled(true);
            /*imgToUpload.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            imgToUpload.layout(0, 0, imgToUpload.getMeasuredWidth(), imgToUpload.getMeasuredHeight());LUIS  */
            imgToUpload.buildDrawingCache(true);

            //BLOQUE PARA ACTIVAR DIVS
            btnAdd = (Button) findViewById(R.id.btn_add);
            btnDelete = (Button) findViewById(R.id.btn_delete);//NO DEBE ELIMINAR EL PRIMER LAYOUT
            btnDelete.setEnabled(false);
            btnDelete.setClickable(false);

            btnUploadData = (Button) findViewById(R.id.btnUploadData);
            btnRegresar = (Button) findViewById(R.id.btnRegresar);

            /*if(!isLollyPop){
                btnSeleccionarImg.setBackgroundColor(getResources().getColor(R.color.color_primary_programatically));
                btnUploadData.setBackgroundColor(getResources().getColor(R.color.color_primary_programatically));
                btnRegresar.setBackgroundColor(getResources().getColor(R.color.color_textgray_programatically));
            }else{
                btnSeleccionarImg.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnUploadData.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnRegresar.setBackgroundColor(getResources().getColor(R.color.textBackDarkGray));
            }*/

            mainLinearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
            subLayout = (LinearLayout) findViewById(R.id.subLinearLayout);
            subLinearBtnGuardar = (LinearLayout) findViewById(R.id.subLinearBtnGuardar);
            subLayoutFloats = (LinearLayout) findViewById(R.id.subLinearLayoutFloats);

            Bundle sesion = getIntent().getExtras();
            usuarioPerfil = (PerfilUsuarioNube) sesion.get("SESSION_USER");
            examenUsuarioPerfil = (ExamenUsuarioNube) sesion.get("SESSION_EXAMS");
            passwordUsedForLogin = sesion.getString("PASSWORD_USED");

            //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
            if(!usuarioPerfil.isUser_ads_disabled()) {
                //ADMOB INICIALIZACION BANNER
                /*mAdView = (AdView) findViewById(R.id.adBannerView);
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                mAdView.loadAd(adRequest);*/
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

            //ACCION BOTON SELECCION IMAGEN
            btnSeleccionarImg.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    selectImageFromGallery();
                }
            });

            //BOTON FLOAT DE ACTIVIDAD DE AÑADIR ELEMENTOS NUEVOS DEL EXAMEN COMO DIVS
            //PRIMER ADD POR DEFECTO
            btnAdd.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //CAMBIAR VALIDACIONES DESPUES DE LAS PRUEBAS
                    if (textoPregunta.getText().length() == 0 || respuestaA.getText().length() == 0 ||
                            respuestaB.getText().length() == 0 ||
                            respuestaC.getText().length() == 0 || respuestaD.getText().length() == 0) {
                        //|| respuestaCorrecta.getText().length() == 0) {

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgValidFormFieldsCreaPru)
                                , Toast.LENGTH_LONG).show();

                    } else {
                        if(imgToUpload.getDrawable() == null){
                            //EVALUAR SI NO COLOCA EL BITMAP EN UN IMAGEVIEW ANTERIOR EN EL QUE NO SE HAYA ESCOGIDO IMG
                            imgToUpload.setImageBitmap(null);
                        }
                        onAddButtonAction();
                        adjustScroll(scrollView);
                    }
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (contadorElementosDynCorrelativo == 0) {
                        onDeleteActionButton(v.getId(), contadorElementosDynCorrelativo);
                    }
                }
            });

            //CREAR INSTANCIA DE BASE DE DATOS SQLITE
            //loginDataBaseAdapter = new com.blogspot.luiseliberal.primertestrecursoslimbicos.utils.LoginDataBaseHelper(this);
            //loginDataBaseAdapter = loginDataBaseAdapter.open();

            // when uploadButton is clicked
            btnUploadData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUploadButtonAction(subLayout);
                }
            });

            btnRegresar.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CrearPrueba.this, PostLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("SESSION_USER", usuarioPerfil);
                    intent.putExtra("SESSION_EXAMS", examenUsuarioPerfil);
                    intent.putExtra("PASSWORD_USED", passwordUsedForLogin);
                    startActivity(intent);
                    finish();
                }
            });

        } catch (Exception e) {
            Log.d(TAG, "ERROR: ", e);
            e.printStackTrace();
        }


        /*actionToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                if (item.getItemId() == R.id.create) {
                    if(passwordUsedForLogin != null) {//ES EL ADMINISTRADOR
                        //CREAR INTENT DE REGISTRO Y COMIENZO DE LA ACTIVIDAD EN LA APP
                        Intent intentCrearPrueba = new Intent(getApplicationContext(), CrearPrueba.class);
                        intentCrearPrueba.putExtra("SESSION_USER", usuarioPerfil);
                        intentCrearPrueba.putExtra("SESSION_EXAMS", examenUsuarioPerfil);
                        intentCrearPrueba.putExtra("PASSWORD_USED", passwordUsedForLogin);
                        startActivity(intentCrearPrueba);
                        finish();
                    }else{
                        Toast.makeText(getBaseContext(), "No tienes privilegios para usar esta opción", Toast.LENGTH_SHORT).show();
                    }
                } else if (item.getItemId() == R.id.make) {
                    //CREAR INTENT DE REGISTRO Y COMIENZO DE LA ACTIVIDAD EN LA APP
                    Intent intentPerfilEncuestado = new Intent(getApplicationContext(), RegistroPerfilEncuestado.class);
                    intentPerfilEncuestado.putExtra("SESSION_USER", usuarioPerfil);
                    intentPerfilEncuestado.putExtra("SESSION_EXAMS", examenUsuarioPerfil);
                    intentPerfilEncuestado.putExtra("PASSWORD_USED", passwordUsedForLogin);
                    startActivity(intentPerfilEncuestado);
                    finish();
                } else if (item.getItemId() == R.id.report) {

                    if(passwordUsedForLogin != null) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(CrearPrueba.this);
                        builder.setTitle("TIPOS DE REPORTES DE EXAMENES");

                        CharSequence[] chars = {};
                        listaReporteExamenes = new ArrayList<>();

                        if (examenUsuarioPerfil != null) {
                            //PARA VER TODOS LOS EXAMENES POR DEFECTO SIEMPRE Y CUANDO EXISTAN EXAMENES
                            listaReporteExamenes.add("TODOS LOS EXAMENES");
                            System.out.println("Exam conf size: " + examenUsuarioPerfil.getExam_conf().size());
                            //AGREGAMOS EL EXAMEN POR DEFECTO PARA VER TODOS
                            for (int i = 0; i < examenUsuarioPerfil.getExam_conf().size(); i++) {
                                //AGREGAMOS EN LA LISTA AUXILIAR EL ID DEL EXAMEN
                                listaReporteExamenes.add(examenUsuarioPerfil.getExam_conf().get(i).getExam_designId() + " - " +
                                        examenUsuarioPerfil.getExam_conf().get(i).getExam_title());
                            }

                            chars = listaReporteExamenes.toArray(new CharSequence[listaReporteExamenes.size()]);

                            builder.setItems(chars,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            //PROCEDEMOS
                                            ProgressBar progressBar = new ProgressBar(CrearPrueba.this);
                                            DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();
                                            String url;
                                            JSONObject jsonObject = new JSONObject();
                                            ReporteExamenesNube reporteExamenesNube = new ReporteExamenesNube();
                                            String examenSeleccionado = listaReporteExamenes.get(which);
                                            String[] splitExamenSeleccionado = examenSeleccionado.split("-");
                                            int idExamenEscogido = Integer.parseInt(splitExamenSeleccionado[0].trim());
                                            //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                                            url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.GET_REPORT_BY_USER_AND_EXAM + "?idUserPpal=" + usuarioPerfil.getUser_id() + "&idExamDesign=" + idExamenEscogido;

                                            new GeneralHttpTask(CrearPrueba.this, progressBar, dynamicTestResponse, "DATA_EXAMS_REPORTS",
                                                    jsonObject, usuarioPerfil, examenUsuarioPerfil, reporteExamenesNube, dialog, examenSeleccionado, passwordUsedForLogin).execute(url);
                                        }
                                    });

                            builder.create().show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Estimado usuario, debe crear una prueba primero para que " +
                                    "aparezca en la lista de examenes disponibles a presentar. Por favor, regrese a la ventana previa.", Toast.LENGTH_LONG).show();
                        }

                    } else {//REPORTE DE USUARIO SECUNDARIO SOLO VE DATA DE PRUEBAS LOCALES SIN EXPORTACION DE NINGUN TIPO
                        Intent intentReportesAdmin = new Intent(getApplicationContext(), ReporteActivity.class);
                        intentReportesAdmin.putExtra("SESSION_USER", usuarioPerfil);
                        intentReportesAdmin.putExtra("SESSION_EXAMS", examenUsuarioPerfil);
                        intentReportesAdmin.putExtra("PASSWORD_USED", passwordUsedForLogin);
                        startActivity(intentReportesAdmin);
                        finish();
                    }
                }else if(item.getItemId() == R.id.goback){
                    Intent intentBack = new Intent(getApplicationContext(), PostLogin.class);
                    intentBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intentBack.putExtra("SESSION_USER", usuarioPerfil);
                    intentBack.putExtra("SESSION_EXAMS", examenUsuarioPerfil);
                    intentBack.putExtra("PASSWORD_USED", passwordUsedForLogin);
                    startActivity(intentBack);
                    finish();
                }
                return false;
            }
        });*/

        try{
            //scaleWindow = getResources().getDisplayMetrics().density;
            metrics = getResources().getDisplayMetrics();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Opens dialog picker, so the user can select image from the gallery. The
     * result is returned in the method <code>onActivityResult()</code>
     */
    public void selectImageFromGallery() {


        if (isKitKat) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.intentSelectPicture)), 1);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.intentSelectPicture)), 1);
        }
            /*Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);*/
    }

    @TargetApi(19)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String path;
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = (CrearPrueba.this).getContentResolver().query(selectedImage, null, null, null, null);

            if (cursor == null) { // Source is Dropbox or other similar local file
                // path
                Log.d(TAG,"PATH IMAGEN: "+selectedImage.getPath());
                decodedImg = selectedImage.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                decodedImg = cursor.getString(idx);
                cursor.close();
            }

            //int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            //TEST
            if(contadorElementosDynCorrelativo > 0){
                textoUploadImgDyn.setText(decodedImg);
            }else {
                textoUploadImg.setText(decodedImg);
            }
            decodeFile(decodedImg);
        }*/


        if (data != null && data.getData() != null && resultCode == RESULT_OK) {

            boolean isImageFromGoogleDrive = false;
            Uri selectedImage = data.getData();
            decodedImg = selectedImage.getPath();

            String file_size = getRealSizeFromUri(getApplicationContext(), selectedImage);
            if (Integer.parseInt(file_size) > 100000) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgErrorFileSize), Toast.LENGTH_LONG).show();
            }
            else{

            if (isKitKat && DocumentsContract.isDocumentUri(CrearPrueba.this, selectedImage)) {

                if ("com.android.externalstorage.documents".equals(selectedImage.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(selectedImage);
                    String[] split = docId.split(":");
                    String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        decodedImg = Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        Pattern DIR_SEPORATOR = Pattern.compile("/");
                        Set<String> rv = new HashSet<>();
                        String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
                        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
                        String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
                        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
                            if (TextUtils.isEmpty(rawExternalStorage)) {
                                rv.add("/storage/sdcard0");
                            } else {
                                rv.add(rawExternalStorage);
                            }
                        } else {
                            String rawUserId;
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                rawUserId = "";
                            } else {
                                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                                String[] folders = DIR_SEPORATOR.split(path);
                                String lastFolder = folders[folders.length - 1];
                                boolean isDigit = false;
                                try {
                                    Integer.valueOf(lastFolder);
                                    isDigit = true;
                                } catch (NumberFormatException ignored) {
                                }
                                rawUserId = isDigit ? lastFolder : "";
                            }
                            if (TextUtils.isEmpty(rawUserId)) {
                                rv.add(rawEmulatedStorageTarget);
                            } else {
                                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                            }
                        }
                        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                            String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                            Collections.addAll(rv, rawSecondaryStorages);
                        }
                        String[] temp = rv.toArray(new String[rv.size()]);

                        for (int i = 0; i < temp.length; i++) {
                            File tempf = new File(temp[i] + "/" + split[1]);
                            if (tempf.exists()) {
                                decodedImg = temp[i] + "/" + split[1];
                            }
                        }
                    }
                } else if ("com.android.providers.downloads.documents".equals(selectedImage.getAuthority())) {
                    String id = DocumentsContract.getDocumentId(selectedImage);
                    Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = {
                            column
                    };

                    try {
                        cursor = getContentResolver().query(contentUri, projection, null, null,
                                null);
                        if (cursor != null && cursor.moveToFirst()) {
                            final int column_index = cursor.getColumnIndexOrThrow(column);
                            decodedImg = cursor.getString(column_index);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                } else if ("com.android.providers.media.documents".equals(selectedImage.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(selectedImage);
                    String[] split = docId.split(":");
                    String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{
                            split[1]
                    };

                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = {
                            column
                    };

                    try {
                        cursor = getContentResolver().query(contentUri, projection, selection, selectionArgs,
                                null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            decodedImg = cursor.getString(column_index);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                } else if ("com.google.android.apps.docs.storage".equals(selectedImage.getAuthority())) {
                    isImageFromGoogleDrive = true;
                }
            } else if ("content".equalsIgnoreCase(selectedImage.getScheme())) {
                Cursor cursor = null;
                String column = "_data";
                String[] projection = {
                        column
                };

                try {
                    cursor = getContentResolver().query(selectedImage, projection, null, null,
                            null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(column);
                        decodedImg = cursor.getString(column_index);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            } else if ("file".equalsIgnoreCase(selectedImage.getScheme())) {
                decodedImg = selectedImage.getPath();
            }


            if (isImageFromGoogleDrive) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));

                    if (contadorElementosDynCorrelativo > 0) {
                        imgToUploadDyn.setImageBitmap(bitmap);
                        textoUploadImgDyn.setText(decodedImg);
                    } else {
                        textoUploadImg.setText(decodedImg);
                        imgToUpload.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                File f = new File(decodedImg);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);

                if (contadorElementosDynCorrelativo > 0) {
                    textoUploadImgDyn.setText(decodedImg);
                } else {
                    textoUploadImg.setText(decodedImg);
                }
            }
            //FUNCIONA
            decodeFile(decodedImg);

         }//FIN VALIDACION SIZE
        }

        //IN APP BILLING GOOGLE
        inAppPayApi.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Request: " + requestCode + ", Result: " + resultCode + ", data: " + data);
        if (resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "IN APP BILL OK O CAMERA OK");
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i(TAG, "IN APP BILL: The user canceled. O CAMARA CANCELED");
        }

        super.onActivityResult(requestCode, resultCode, data);

    }


    /**
     * The method decodes the image file to avoid out of memory issues. Sets the
     * selected image in to the ImageView.
     *
     * @param filePath
     */
    public void decodeFile(String filePath) {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 2048;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        //PROBANDO
        if(contadorElementosDynCorrelativo > 0) {
            imgToUploadDyn.setImageBitmap(bitmap);
        }else{
            imgToUpload.setImageBitmap(bitmap);
            //ImageView imgTest = (ImageView) findViewById(R.id.imageToUpload);
            //imgTest.setImageBitmap(bitmap);
        }

        /*ByteArrayOutputStream baos0 = new ByteArrayOutputStream();

        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos0);
        byte[] imageBytes0 = baos0.toByteArray();

        image.setImageBitmap(bmp);

        encodedImage= Base64.encodeToString(imageBytes0, Base64.DEFAULT);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CrearPrueba.this, PostLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("SESSION_USER", usuarioPerfil);
        intent.putExtra("SESSION_EXAMS", examenUsuarioPerfil);
        intent.putExtra("PASSWORD_USED", passwordUsedForLogin);

        startActivity(intent);
        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        if(!usuarioPerfil.isUser_ads_disabled()) {
            //ADMOB INTERSTITIAL
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
        finish();
    }

    //METODO QUE MANEJA LA INCLUSION DE UN NUEVO LAYOUT DINAMICO EN LA VISTA E INSERT DEL LAYOUT ANTERIOR AL MAPA DE CARGA
    private void onAddButtonAction() {

        try {
            Context c = CrearPrueba.this;
            //REMOVEMOS EL LINEAR DEL BOTON GUARDAR Y REGRESAR PARA MOVERLO AL FINAL DE LOS NUEVOS ELEMENTOS
            subLayout.removeView(subLinearBtnGuardar);

            //NUEVOS ELEMENTOS EN BASE A LOS ORIGINALES
            //0
            textoPreguntaDyn = new EditText(c);
            textoPreguntaDyn.setHint(getResources().getString(R.string.hintPreguntaCreaPru));
            textoPreguntaDyn.setId(contadorElementosDynCorrelativo++);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 25, 0, 0);
            textoPreguntaDyn.setLayoutParams(params);
            textoPreguntaDyn.setTextSize(14);
            subLayout.addView(textoPreguntaDyn);
            //RESPUESTAS
            //1
            respuestaADyn = new EditText(c);
            respuestaADyn.setId(contadorElementosDynCorrelativo++);
            respuestaADyn.setHint(getResources().getString(R.string.hintResACreaPru));
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            respuestaADyn.setLayoutParams(params1);
            respuestaADyn.setTextSize(14);
            subLayout.addView(respuestaADyn);
            //2
            respuestaBDyn = new EditText(c);
            respuestaBDyn.setId(contadorElementosDynCorrelativo++);
            respuestaBDyn.setHint(getResources().getString(R.string.hintResBCreaPru));
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            respuestaBDyn.setLayoutParams(params2);
            respuestaBDyn.setTextSize(14);
            subLayout.addView(respuestaBDyn);
            //3
            respuestaCDyn = new EditText(c);
            respuestaCDyn.setId(contadorElementosDynCorrelativo++);
            respuestaCDyn.setHint(getResources().getString(R.string.hintResCCreaPru));
            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            respuestaCDyn.setLayoutParams(params3);
            respuestaCDyn.setTextSize(14);
            subLayout.addView(respuestaCDyn);
            //4
            respuestaDDyn = new EditText(c);
            respuestaDDyn.setId(contadorElementosDynCorrelativo++);
            respuestaDDyn.setHint(getResources().getString(R.string.hintResDCreaPru));
            LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            respuestaDDyn.setLayoutParams(params4);
            respuestaDDyn.setTextSize(14);
            subLayout.addView(respuestaDDyn);
            //5

            linearRespCorBtnImg = new LinearLayout(c);
            linearRespCorBtnImg.setOrientation(LinearLayout.HORIZONTAL);
            linearRespCorBtnImg.setId(contadorElementosDynCorrelativo++);
            linearRespCorBtnImg.setWeightSum(2f);

            respuestaCorrectaDyn = new EditText(c);
            respuestaCorrectaDyn.setId(contadorElementosDynCorrelativo++);
            respuestaCorrectaDyn.setHint(getResources().getString(R.string.hintRespCorrCreaPru));
            respuestaCorrectaDyn.setInputType(InputType.TYPE_CLASS_NUMBER);
            respuestaCorrectaDyn.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f);
            params5.weight = 1.1f;
            respuestaCorrectaDyn.setLayoutParams(params5);
            respuestaCorrectaDyn.setTextSize(14);

            linearRespCorBtnImg.addView(respuestaCorrectaDyn);
            //subLayout.addView(respuestaCorrectaDyn);
            //SECCION IMAGEN
            //6
            if(btnSeleccionarImgDyn != null && subLayout.getChildCount() > 20) {
                btnSeleccionarImgDyn.setEnabled(false);
                btnSeleccionarImgDyn.setClickable(false);
                //btnSeleccionarImgDyn.setBackgroundColor(getResources().getColor(R.color.textBackDarkGray));
                btnSeleccionarImgDyn.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey_box));
            }else if(btnSeleccionarImg != null){
                btnSeleccionarImg.setEnabled(false);
                btnSeleccionarImg.setClickable(false);
                //btnSeleccionarImg.setBackgroundColor(getResources().getColor(R.color.textBackDarkGray));
                btnSeleccionarImg.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey_box));
            }

            btnSeleccionarImgDyn = new Button(c);
            btnSeleccionarImgDyn.setId(contadorElementosDynCorrelativo++);
            btnSeleccionarImgDyn.setBackground(getResources().getDrawable(R.drawable.btn_rounded_box_blue));
            btnSeleccionarImgDyn.setTextSize(14);
            btnSeleccionarImgDyn.setText(getResources().getString(R.string.btnCargaImgCreaPru));
            btnSeleccionarImgDyn.setTextColor(getResources().getColor(R.color.colorSecondaryText));
            LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (metrics.density * 40f), 0.9f);
            params6.setMargins(0, 0, 15, 0);
            params6.weight = 0.9f;
            btnSeleccionarImgDyn.setLayoutParams(params6);

            btnSeleccionarImgDyn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    selectImageFromGallery();
                }
            });

            linearRespCorBtnImg.addView(btnSeleccionarImgDyn);

            subLayout.addView(linearRespCorBtnImg);
            //7
            textoUploadImgDyn = new EditText(c);
            textoUploadImgDyn.setId(contadorElementosDynCorrelativo++);
            LinearLayout.LayoutParams params7 = new LinearLayout.LayoutParams(0, 0);
            textoUploadImgDyn.setSingleLine(true);
            textoUploadImgDyn.setLayoutParams(params7);
            subLayout.addView(textoUploadImgDyn);
            //8
            linearImgBtnFloats = new LinearLayout(c);
            linearImgBtnFloats.setOrientation(LinearLayout.HORIZONTAL);
            linearImgBtnFloats.setId(contadorElementosDynCorrelativo++);
            linearImgBtnFloats.setWeightSum(3f);
            LinearLayout.LayoutParams paramsLin = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            paramsLin.setMargins(0, 20, 0, 0);
            linearImgBtnFloats.setLayoutParams(paramsLin);

            imgToUploadDyn = new ImageView(c);
            imgToUploadDyn.setId(contadorElementosDynCorrelativo++);
            //LUIS 300317 ERROR DE DRAWING CACHE NULL
            imgToUploadDyn.setDrawingCacheEnabled(true);
            /*
            imgToUploadDyn.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            imgToUploadDyn.layout(0, 0, imgToUploadDyn.getMeasuredWidth(), imgToUploadDyn.getMeasuredHeight());
            int dpWidthInPx  = (int) (metrics.density * 150f);
            int dpHeightInPx = (int) (metrics.density * 100f);LUIS  */

            imgToUploadDyn.buildDrawingCache(true);
            LinearLayout.LayoutParams params8 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.8f);
            params8.weight = 1.8f;
            params8.leftMargin = (int) (metrics.density * 10f);
            params8.gravity = Gravity.LEFT;
            imgToUploadDyn.setLayoutParams(params8);

            linearImgBtnFloats.addView(imgToUploadDyn);
            //subLayout.addView(imgToUploadDyn);
            //9
            mensajeCargaDatosDyn = new TextView(c);
            mensajeCargaDatosDyn.setId(contadorElementosDynCorrelativo++);
            mensajeCargaDatosDyn.setTextColor(getResources().getColor(R.color.colorPrimaryText));
            mensajeCargaDatosDyn.setWidth(0);
            mensajeCargaDatosDyn.setHeight(0);
            LinearLayout.LayoutParams params9 = new LinearLayout.LayoutParams(0, 0, 0f);
            params9.weight = 0f;
            mensajeCargaDatosDyn.setLayoutParams(params9);
            linearImgBtnFloats.addView(mensajeCargaDatosDyn);
            //FLOATING BUTTONS ADD EDIT DELETE
            //10
            linearLayoutDyn = new LinearLayout(c);
            linearLayoutDyn.setOrientation(LinearLayout.HORIZONTAL);
            linearLayoutDyn.setId(contadorElementosDynCorrelativo++);
            LinearLayout.LayoutParams paramLL = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f);
            paramLL.gravity = Gravity.RIGHT;
            paramLL.weight = 1f;
            linearLayoutDyn.setLayoutParams(paramLL);

            if(btnAddDyn != null){
                btnAddDyn.setEnabled(false);
                btnAddDyn.setClickable(false);
                btnAddDyn.setBackground(getResources().getDrawable(R.drawable.btn_disabled_circle));

            }else if(btnAdd != null){
                btnAdd.setEnabled(false);
                btnAdd.setClickable(false);
                btnAdd.setBackground(getResources().getDrawable(R.drawable.btn_disabled_circle));
            }

            if(btnDeleteDyn != null){
                btnDeleteDyn.setEnabled(false);
                btnDeleteDyn.setClickable(false);
                btnDeleteDyn.setBackground(getResources().getDrawable(R.drawable.btn_disabled_circle));

            }else if(btnDelete != null){
                btnDelete.setEnabled(false);
                btnDelete.setClickable(false);
                btnDelete.setBackground(getResources().getDrawable(R.drawable.btn_disabled_circle));
            }

            btnAddDyn = new Button(c);
            btnDeleteDyn = new Button(c);

            //11
            btnAddDyn.setId(contadorElementosDynCorrelativo++);
            btnAddDyn.setBackground(getResources().getDrawable(R.drawable.btn_green_circle));
            btnAddDyn.setText("+");
            btnAddDyn.setTextSize(30);
            btnAddDyn.setTextColor(getResources().getColor(R.color.colorSecondaryText));
            btnAddDyn.setWidth(40);
            btnAddDyn.setHeight(40);
            LinearLayout.LayoutParams params10 = new LinearLayout.LayoutParams((int) (metrics.density * 45f), (int) (metrics.density * 45f));
            params10.setMargins((int) (metrics.density * 80f), 6, 6, 6);
            btnAddDyn.setLayoutParams(params10);
            btnAddDyn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //LUIS CAMBIAR VALIDACIONES DESPUES DE PRUEBAS
                    if (textoPreguntaDyn.getText().length() < 1 || respuestaADyn.getText().length() == 0 ||
                            respuestaBDyn.length() == 0 ||
                            respuestaCDyn.length() == 0 || respuestaDDyn.length() == 0) {
                        //|| respuestaCorrectaDyn.length() == 0) {

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgValidTextNextPreg), Toast.LENGTH_LONG).show();
                    } else {
                        if (imgToUploadDyn.getDrawable() == null) {
                            //EVALUAR SI NO COLOCA EL BITMAP EN UN IMAGEVIEW ANTERIOR EN EL QUE NO SE HAYA ESCOGIDO IMG
                            imgToUploadDyn.setImageBitmap(null);
                        } else if (imgToUpload.getDrawable() == null) {
                            //EVALUAR SI NO COLOCA EL BITMAP EN UN IMAGEVIEW ANTERIOR EN EL QUE NO SE HAYA ESCOGIDO IMG
                            imgToUpload.setImageBitmap(null);
                        }
                        onAddButtonAction();
                        adjustScroll(scrollView);
                    }
                }
            });
            linearLayoutDyn.addView(btnAddDyn);

            //12
            btnDeleteDyn.setId(contadorElementosDynCorrelativo++);
            btnDeleteDyn.setBackground(getResources().getDrawable(R.drawable.btn_red_circle));
            btnDeleteDyn.setText("X");
            btnDeleteDyn.setTextSize(26);
            btnDeleteDyn.setTextColor(getResources().getColor(R.color.colorSecondaryText));
            btnDeleteDyn.setMaxWidth(40);
            btnDeleteDyn.setMaxHeight(40);
            LinearLayout.LayoutParams params12 = new LinearLayout.LayoutParams((int) (metrics.density * 45f), (int) (metrics.density * 45f));
            params12.setMargins(6, 6, 6, 6);
            btnDeleteDyn.setLayoutParams(params12);
            btnDeleteDyn.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    onDeleteActionButton(v.getId(), contadorElementosDynCorrelativo);
                }
            });

            linearLayoutDyn.addView(btnDeleteDyn);
            //AGREGAMOS EL LINEAR LAYOUT DE FLOATS HORIZONTAL AL SUBLINEAR LAYOUT VERTICAL
            linearImgBtnFloats.addView(linearLayoutDyn);

            subLayout.addView(linearImgBtnFloats);
            //COLOCAMOS EL LINEAR DEL BOTON GUARDAR DEBAJO DE LOS ELEMENTOS DINAMICOS
            subLayout.addView(subLinearBtnGuardar);

        } catch (Exception e) {
            Log.d(TAG, "ERROR: ", e);
            e.printStackTrace();
        }
    }

    //METODO QUE MANEJA LA ELIMINACION DE LAYOUTS DINAMICAMENTE Y EXCLUSION DE ELEMENTOS EN EL MAPA DE CARGA
    private void onDeleteActionButton(int btnDeleteId, int contadorElementosDyn) {

        ViewGroup parent;
        System.out.println("Id boton Delete: " + btnDeleteId + "contadorElementosDynCorrel: " + contadorElementosDynCorrelativo);

        try {
            if (contadorElementosDynCorrelativo == 0) {
                /*View textoPreguntaView = findViewById(R.id.textoPregunta);
                parent = (ViewGroup) textoPreguntaView.getParent();
                parent.removeView(textoPreguntaView);

                View respuestaAView = findViewById(R.id.respuestaA);
                parent = (ViewGroup) respuestaAView.getParent();
                parent.removeView(respuestaAView);

                View respuestaBView = findViewById(R.id.respuestaB);
                parent = (ViewGroup) respuestaBView.getParent();
                parent.removeView(respuestaBView);

                View respuestaCView = findViewById(R.id.respuestaC);
                parent = (ViewGroup) respuestaCView.getParent();
                parent.removeView(respuestaCView);

                View respuestaDView = findViewById(R.id.respuestaD);
                parent = (ViewGroup) respuestaDView.getParent();
                parent.removeView(respuestaDView);

                View respuestaCorrectaView = findViewById(R.id.respuestaCorrectaId);
                parent = (ViewGroup) respuestaCorrectaView.getParent();
                parent.removeView(respuestaCorrectaView);

                View btnSelectImageUpView = findViewById(R.id.btnSeleccionarImg);
                parent = (ViewGroup) btnSelectImageUpView.getParent();
                parent.removeView(btnSelectImageUpView);

                View textoImgUploadView = findViewById(R.id.textoImgUpload);
                parent = (ViewGroup) textoImgUploadView.getParent();
                parent.removeView(textoImgUploadView);

                View imgUploadView = findViewById(R.id.imageToUpload);
                parent = (ViewGroup) imgUploadView.getParent();
                parent.removeView(imgUploadView);

                View mensajeCargaDatosView = findViewById(R.id.mensajeCargaDatos);
                parent = (ViewGroup) mensajeCargaDatosView.getParent();
                parent.removeView(mensajeCargaDatosView);

                View linearLayoutFloatView = findViewById(R.id.subLinearLayoutFloats);
                parent = (ViewGroup) linearLayoutFloatView.getParent();
                parent.removeView(linearLayoutFloatView);

                View floatAddButtonView = findViewById(R.id.btn_add);
                parent = (ViewGroup) floatAddButtonView.getParent();
                parent.removeView(floatAddButtonView);

                View floatDeleteButtonView = findViewById(R.id.btn_delete);
                parent = (ViewGroup) floatDeleteButtonView.getParent();
                parent.removeView(floatDeleteButtonView);

                View linearRespCorrBtnImg = findViewById(R.id.linearRespCorBtnImg);
                parent = (ViewGroup) linearRespCorrBtnImg.getParent();
                parent.removeView(linearRespCorrBtnImg);

                View linearImgBtnFloats = findViewById(R.id.linearImgBtnFloats);
                parent = (ViewGroup) linearImgBtnFloats.getParent();
                parent.removeView(linearImgBtnFloats);*/

                Toast.makeText(this, getResources().getString(R.string.msgValidDelFirstPreg), Toast.LENGTH_LONG).show();

            } else {
                //SOLO BORRRANDO EL ULTIMO FORMULARIO SE TOMA EL CONTADOR Y SE RESTA EL NUMERO DE ELEMENTOS PARA POSICIONAR EL ID
                View textoPreguntaView = findViewById(btnDeleteId - 14);
                parent = (ViewGroup) textoPreguntaView.getParent();
                parent.removeView(textoPreguntaView);

                View respuestaAView = findViewById(btnDeleteId - 13);
                parent = (ViewGroup) respuestaAView.getParent();
                parent.removeView(respuestaAView);

                View respuestaBView = findViewById(btnDeleteId - 12);
                parent = (ViewGroup) respuestaBView.getParent();
                parent.removeView(respuestaBView);

                View respuestaCView = findViewById(btnDeleteId - 11);
                parent = (ViewGroup) respuestaCView.getParent();
                parent.removeView(respuestaCView);

                View respuestaDView = findViewById(btnDeleteId - 10);
                parent = (ViewGroup) respuestaDView.getParent();
                parent.removeView(respuestaDView);

                View respuestaCorrectaView = findViewById(btnDeleteId - 8);
                parent = (ViewGroup) respuestaCorrectaView.getParent();
                parent.removeView(respuestaCorrectaView);

                View btnSelectImageUpView = findViewById(btnDeleteId - 7);
                parent = (ViewGroup) btnSelectImageUpView.getParent();
                parent.removeView(btnSelectImageUpView);

                View textoImgUploadView = findViewById(btnDeleteId - 6);
                parent = (ViewGroup) textoImgUploadView.getParent();
                parent.removeView(textoImgUploadView);

                //NUEVA LINEAR HORIZONTAL A
                View linearRespCorrBtnImg = findViewById(btnDeleteId - 9);
                parent = (ViewGroup) linearRespCorrBtnImg.getParent();
                parent.removeView(linearRespCorrBtnImg);

                View imgUploadView = findViewById(btnDeleteId - 4);
                parent = (ViewGroup) imgUploadView.getParent();
                parent.removeView(imgUploadView);

                View mensajeCargaDatosView = findViewById(btnDeleteId - 3);
                parent = (ViewGroup) mensajeCargaDatosView.getParent();
                parent.removeView(mensajeCargaDatosView);

                View floatAddButtonView = findViewById(btnDeleteId - 1);
                parent = (ViewGroup) floatAddButtonView.getParent();
                parent.removeView(floatAddButtonView);

                View floatDeleteButtonView = findViewById(btnDeleteId);
                parent = (ViewGroup) floatDeleteButtonView.getParent();
                parent.removeView(floatDeleteButtonView);

                View linearLayoutFloatView = findViewById(btnDeleteId - 2);
                parent = (ViewGroup) linearLayoutFloatView.getParent();
                parent.removeView(linearLayoutFloatView);

                //SEGUNDA LINEAR AGREGADA
                View linearImgBtnFloats = findViewById(btnDeleteId - 5);
                parent = (ViewGroup) linearImgBtnFloats.getParent();
                parent.removeView(linearImgBtnFloats);
                if(subLayout.getChildCount() > 12) {//LO MINIMO
                    textoPreguntaDyn = (EditText) subLayout.getChildAt((subLayout.getChildCount() - 1) - 7);
                    respuestaADyn = (EditText) subLayout.getChildAt((subLayout.getChildCount() - 1) - 6);
                    respuestaBDyn = (EditText) subLayout.getChildAt((subLayout.getChildCount() - 1) - 5);
                    respuestaCDyn = (EditText) subLayout.getChildAt((subLayout.getChildCount() - 1) - 4);
                    respuestaDDyn = (EditText) subLayout.getChildAt((subLayout.getChildCount() - 1) - 3);
                    LinearLayout linearRespCorrBtnDyn = (LinearLayout) subLayout.getChildAt((subLayout.getChildCount() - 1) - 2);
                    respuestaCorrectaDyn = (EditText) linearRespCorrBtnDyn.getChildAt(0);
                    btnSeleccionarImgDyn = (Button) linearRespCorrBtnDyn.getChildAt(1);
                    if(btnSeleccionarImgDyn != null){
                        btnSeleccionarImgDyn.setEnabled(true);
                        btnSeleccionarImgDyn.setClickable(true);
                        btnSeleccionarImgDyn.setBackground(getResources().getDrawable(R.drawable.btn_rounded_box_blue));
                    }
                    textoUploadImgDyn = (EditText) subLayout.getChildAt((subLayout.getChildCount() - 1) - 1);
                    LinearLayout linearImgBtnFloatsDyn = (LinearLayout) subLayout.getChildAt(subLayout.getChildCount() - 1);
                    imgToUploadDyn = (ImageView) linearImgBtnFloatsDyn.getChildAt(0);
                    mensajeCargaDatosDyn = (TextView) linearImgBtnFloatsDyn.getChildAt(1);
                    LinearLayout linearFloatsDyn = (LinearLayout) linearImgBtnFloatsDyn.getChildAt(2);
                    btnAddDyn = (Button) linearFloatsDyn.getChildAt(0);
                    if (btnAddDyn != null) {
                        btnAddDyn.setEnabled(true);
                        btnAddDyn.setClickable(true);
                        btnAddDyn.setBackground(getResources().getDrawable(R.drawable.btn_green_circle));
                    }
                    btnDeleteDyn = (Button) linearFloatsDyn.getChildAt(1);
                    if (btnDeleteDyn != null) {
                        btnDeleteDyn.setEnabled(true);
                        btnDeleteDyn.setClickable(true);
                        btnDeleteDyn.setBackground(getResources().getDrawable(R.drawable.btn_red_circle));
                    }
                }else{
                    //ACTIVAMOS EL LAYOUT INICIAL
                    if(btnSeleccionarImg != null){
                        btnSeleccionarImg.setEnabled(true);
                        btnSeleccionarImg.setClickable(true);
                        btnSeleccionarImg.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                    if (btnAdd != null) {
                        btnAdd.setEnabled(true);
                        btnAdd.setClickable(true);
                        btnAdd.setBackground(getResources().getDrawable(R.drawable.btn_green_circle));
                    }
                    if (btnDelete != null) {
                        btnDelete.setEnabled(true);
                        btnDelete.setClickable(true);
                        btnDelete.setBackground(getResources().getDrawable(R.drawable.btn_red_circle));
                    }

                }
            }

        } catch (Exception e) {
            Log.d(TAG, "Error: ", e);
            e.printStackTrace();
        }
    }

    //METODO QUE MANEJA LA SUBIDA AL SERVIDOR DE LA PRUEBA INGRESADA MEDIANTE PUSH POST
    private void onUploadButtonAction(LinearLayout subLinearLayout) {

        final LinearLayout layoutContenedor = subLinearLayout;
        try{
            final AlertDialog.Builder mensajeAlertaSubida = new AlertDialog.Builder(CrearPrueba.this);
            mensajeAlertaSubida.setTitle(getResources().getString(R.string.tituloMsgConfirmCreaPru));
            mensajeAlertaSubida.setMessage(getResources().getString(R.string.msgDialogConfirmCreaPru));

            //ACCION MENSAJE ALERTA ACEPTAR
            mensajeAlertaSubida.setPositiveButton(getResources().getString(R.string.btnDialogConfAcepCreaPru), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //new ImageUploadTask(tituloPrueba, textoPregunta, respuestaA, respuestaB, respuestaC,respuestaD, usuarioPerfil).execute();
                            boolean flagUpdate = false;
                            String metodoRest = "";
                            ProgressBar progressBar = new ProgressBar(CrearPrueba.this);
                            DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();

                            //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                            if (examenUsuarioPerfil != null) {
                                flagUpdate = true;
                                url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.UPDATE_EXAMS + examenUsuarioPerfil.getUser_id();
                                metodoRest = "UPDATE_EXAMS_USER";
                            } else {
                                url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.ADD_NEW_EXAM;
                                metodoRest = "ADD_NEW_EXAM";
                            }

                            ExamenUsuarioNube examenUsuarioNube = new ExamenUsuarioNube();
                            ExamUsuarioNubeConf examenUsuarioNubeConf = new ExamUsuarioNubeConf();
                            String tituloPruebaAux = "";

                            try {
                                //BEAN DE EXAMENES POR USUARIO (NIVEL 1)
                                examenUsuarioNube.setUser_id(usuarioPerfil.getUser_id());

                                //BEAN EXAMEN USUARIO NUBE CONF (NIVEL 2)
                                if (examenUsuarioPerfil == null) {
                                    examenUsuarioPerfil = new ExamenUsuarioNube();
                                }

                                if (examenUsuarioPerfil.getExam_conf() != null) {
                                    examenUsuarioNubeConf.setExam_designId(examenUsuarioPerfil.getExam_conf().size() + 1);
                                } else {
                                    examenUsuarioNubeConf.setExam_designId(0);
                                }
                                examenUsuarioNubeConf.setExam_time_by_question(Double.parseDouble(examTotalTimeMinutes.getText().toString()));
                                examenUsuarioNubeConf.setExam_password(examPass.getText().toString());

                                tituloPruebaAux = tituloPrueba.getText().toString().trim();
                                tituloPruebaAux = tituloPruebaAux.replace(" ", "_");
                                examenUsuarioNubeConf.setExam_title(tituloPruebaAux);

                                //BEAN EXAMEN USUARIO CURRENT CONF (NIVEL 3)
                                ExamUsuarioNubeCurrentConf examenUsuarioNubeCurrentConf = new ExamUsuarioNubeCurrentConf();
                                listaExamUsuarioNubeConf = new ArrayList<ExamUsuarioNubeConf>();
                                listaPreguntas = new ArrayList<ExamenUsuarioCurrentPreguntas>();
                                listaRespuestas = new ArrayList<ExamenUsuarioCurrentRespuestas>();
                                //COMIENZAN LAS ITERACIONES SOBRE LISTAS DE PREGUNTAS Y RESPUESTAS (NIVEL 4)
                                ExamenUsuarioCurrentPreguntas examenUsuarioCurrentPreguntas = null;
                                ExamenUsuarioCurrentRespuestas examenUsuarioCurrentRespuestas = null;
                                ExamUsuarioCurrentRespDetalle examenUsuarioCurrentRespDetalle = null;
                                int contadorPreguntasAux = 0;
                                int contadorRespuestasAux = 0;
                                //PARA EL TRATO DE LAS IMAGENES CODIFICADAS CON BASE 64
                                HashMap<String, String> mapaImagenesBase64 = new HashMap<String, String>();
                                //ArrayList<String> listaImagenesBase64 = new ArrayList<String>();
                                String imgNameFromLocalPath = "";

                                //RECORREMOS TODAS LAS VISTAS PRESENTES EN EL LINEAR LAYOUT PPAL A PARTIR DEL 2 (DESPUES DE LOS DOS TITULOS PPALES
                                for (int x = 0; x < layoutContenedor.getChildCount(); x++) {

                                    if ((x % 8) == 0) {//TEXTO PREGUNTA ID

                                        if (layoutContenedor.getChildAt(x) instanceof EditText) {

                                            String txtPregunta = ((EditText) layoutContenedor.getChildAt(x)).getText().toString();
                                            Log.d(TAG, "Textos preguntas: " + txtPregunta);

                                            examenUsuarioCurrentPreguntas = new ExamenUsuarioCurrentPreguntas();
                                            examenUsuarioCurrentPreguntas.setId(contadorPreguntasAux);
                                            examenUsuarioCurrentPreguntas.setDesc(txtPregunta);
                                            listaRespuestasDetallePorPregunta = new ArrayList<>();
                                            contadorPreguntasAux++;
                                            contadorRespuestasAux = 0;
                                        }

                                    } else if ((x % 8) == 5) {//RESPUESTA CORRECTA ID

                                        if (layoutContenedor.getChildAt(x) instanceof LinearLayout) {

                                            String respCorrecta;
                                            LinearLayout dinamicLinearLayImgResCor = ((LinearLayout) layoutContenedor.getChildAt(x));
                                            //if (x == 5) {
                                            //    respCorrecta = ((EditText) linearRespCorBtnImg.getChildAt(0)).getText().toString();
                                            //} else {
                                            //dinamicLinearLayImgResCor = ((LinearLayout) layoutContenedor.getChildAt(x));
                                            respCorrecta = ((EditText) dinamicLinearLayImgResCor.getChildAt(0)).getText().toString();
                                            //}

                                            Log.d(TAG, "Respuesta Correcta: " + respCorrecta);

                                            examenUsuarioCurrentRespuestas = new ExamenUsuarioCurrentRespuestas();
                                            if (examenUsuarioCurrentPreguntas == null) {
                                                examenUsuarioCurrentPreguntas = new ExamenUsuarioCurrentPreguntas();
                                            }
                                            examenUsuarioCurrentRespuestas.setQuestion_id(examenUsuarioCurrentPreguntas.getId());
                                            examenUsuarioCurrentRespuestas.setCorrect_answer(Integer.parseInt(respCorrecta));

                                            examenUsuarioCurrentRespuestas.setAnswers(listaRespuestasDetallePorPregunta);
                                            listaRespuestas.add(examenUsuarioCurrentRespuestas);
                                        }

                                    } else if ((x % 8) == 6) {//EL 6 ES EL TEXTO DE LA IMAGEN EN LOCAL
                                        //TOMAMOS EL PATH DE LA IMAGEN ENCODED
                                        if (layoutContenedor.getChildAt(x) instanceof EditText) {
                                            imgNameFromLocalPath = ((EditText) layoutContenedor.getChildAt(x)).getText().toString();
                                            imgPathPhoneAux = imgNameFromLocalPath;
                                            imgNameFromLocalPath = imgNameFromLocalPath.substring(imgNameFromLocalPath.lastIndexOf("/") + 1);
                                            examenUsuarioCurrentPreguntas.setImgPregPathServer(Constants.URL_FOLDER_APP_SERVER +
                                                    usuarioPerfil.getUser_login() + "/" + tituloPruebaAux + "/" + imgNameFromLocalPath);
                                            //QUESTION ID CURRENT RESPUESTAS
                                            listaPreguntas.add(examenUsuarioCurrentPreguntas);
                                        }

                                    } else if ((x % 8) == 7) {//LO NUEVO LINEAR EN EL 7 y SubLinear en el 0
                                        //EL 8 ES EL IMAGEVIEW DE LA IMAGEN EN LOCAL
                                        //TOMAMOS EL PATH DE LA IMAGEN ENCODED
                                        if (layoutContenedor.getChildAt(x) instanceof LinearLayout) {

                                            LinearLayout subLinearLayoutImgFloats = ((LinearLayout) layoutContenedor.getChildAt(x));
                                            Bitmap bm = null;

                                            if (x > 8) {

                                                if (subLinearLayoutImgFloats.getChildAt(0) instanceof ImageView) {
                                                    //LUIS 300317 ERROR DRAWING CACHE NULL
                                                    //bm = Bitmap.createBitmap(subLinearLayoutImgFloats.getChildAt(0).getDrawingCache());
                                                    bm = BitmapFactory.decodeFile(imgPathPhoneAux,null);
                                                    subLinearLayoutImgFloats.getChildAt(0).setDrawingCacheEnabled(false);
                                                }

                                            } else {
                                                LinearLayout subLinearLayoutAux = ((LinearLayout) subLinearLayoutImgFloats.getChildAt(0));

                                                if (subLinearLayoutAux.getChildAt(0) instanceof ImageView) {
                                                    //LUIS 300317 ERROR DRAWING CACHE NULL
                                                    //bm = Bitmap.createBitmap(subLinearLayoutAux.getChildAt(0).getDrawingCache());
                                                    bm = BitmapFactory.decodeFile(imgPathPhoneAux,null);
                                                    subLinearLayoutAux.getChildAt(0).setDrawingCacheEnabled(false);
                                                }
                                            }

                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                            if (bm != null) {

                                                if(imgNameFromLocalPath.indexOf(".png") > 0){
                                                    bm.compress(Bitmap.CompressFormat.PNG, 80, baos);
                                                }else {
                                                    bm.compress(Bitmap.CompressFormat.JPEG, 80, baos); //bm is the bitmap object
                                                }
                                                byte[] b = baos.toByteArray();

                                                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                                                //AGREGAMOS LA IMAGEN CODIFICADA A LA LISTA QUE ENVIAREMOS AL ASYNCTASK POR SEPARADO
                                                mapaImagenesBase64.put(imgNameFromLocalPath, encodedImage);
                                                //listaImagenesBase64.add(encodedImage);
                                        /*String imgNameFromLocalPath = ((EditText) layoutContenedor.getChildAt(x)).getText().toString();
                                        imgNameFromLocalPath = imgNameFromLocalPath.substring(imgNameFromLocalPath.lastIndexOf("/") + 1);

                                        examenUsuarioCurrentPreguntas.setImgPregPathServer("http://www.javadoh.com/landings/plantasmedicinales/content/" +
                                                usuarioPerfil.getUser_login() + "/" + tituloPrueba + "/" + imgNameFromLocalPath);
                                        //QUESTION ID CURRENT RESPUESTAS
                                        listaPreguntas.add(examenUsuarioCurrentPreguntas);*/
                                            }
                                        }


                                    } else if ((x % 8) > 0 && (x % 8) < 5) {

                                        if (layoutContenedor.getChildAt(x) instanceof EditText) {

                                            String respuesta = ((EditText) layoutContenedor.getChildAt(x)).getText().toString();

                                            Log.d(TAG, "Respuestas +" + x + ": " + ((EditText) layoutContenedor.getChildAt(x)).getText());

                                            examenUsuarioCurrentRespDetalle = new ExamUsuarioCurrentRespDetalle();
                                            examenUsuarioCurrentRespDetalle.setId(contadorRespuestasAux);
                                            examenUsuarioCurrentRespDetalle.setDesc(respuesta);
                                            if (listaRespuestasDetallePorPregunta == null) {
                                                listaRespuestasDetallePorPregunta = new ArrayList<ExamUsuarioCurrentRespDetalle>();
                                            }
                                            listaRespuestasDetallePorPregunta.add(examenUsuarioCurrentRespDetalle);
                                            contadorRespuestasAux++;
                                        }
                                    }
                                }

                                examenUsuarioNubeCurrentConf.setQuestions(listaPreguntas);
                                examenUsuarioNubeCurrentConf.setAnswers(listaRespuestas);

                                //ESTO ES POR CADA CONF QUE ENCUENTRE HAY QUE ARREGLARLOS SOLO FUNCIONA PARA 1 EXAMEN
                                examenUsuarioNubeConf.setExamUsuarioNubeCurrentConfList(examenUsuarioNubeCurrentConf);
                                listaExamUsuarioNubeConf.add(examenUsuarioNubeConf);

                                examenUsuarioNube.setExam_conf(listaExamUsuarioNubeConf);

                                //PARSE JAVA BEAN EXAMENES A GSON Y JSON
                                Gson gson = new Gson();
                                String json;

                                if (flagUpdate) {
                                    //SOLO CUERPO DE EXAM_CONF CUANDO SEA UN UPDATE
                                    Type type = new TypeToken<ExamUsuarioNubeConf>() {
                                    }.getType();
                                    json = gson.toJson(examenUsuarioNubeConf, type);
                                } else {
                                    //EL ARREGLO COMPLETO DE EXAMENES POR PRIMERA VEZ
                                    Type type = new TypeToken<ExamenUsuarioNube>() {
                                    }.getType();
                                    json = gson.toJson(examenUsuarioNube, type);
                                }
                                jsonObject = new JSONObject(json);

                                //INVOCAMOS A LA TAREA ASYNCRONA ENVIANDO NUESTROS DATOS Y BEAN PARA SER GUARDADOS EN EL SERVIDOR
                                new GeneralHttpTask(CrearPrueba.this, progressBar, dynamicTestResponse, metodoRest, jsonObject, usuarioPerfil, mapaImagenesBase64, CrearPrueba.this, passwordUsedForLogin).execute(url);

                            } catch (Exception e) {
                                Log.d(TAG, "Error: ", e);
                                Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            } finally {
                                examenUsuarioNube = null;
                                examenUsuarioNubeConf = null;
                                listaExamUsuarioNubeConf = null;
                                listaPreguntas = null;
                                listaRespuestas = null;
                                listaRespuestasDetallePorPregunta = null;
                            }
                        }
                    }

            );//FIN ACCION MENSAJE DE ALERTA AL ACEPTAR

            //ACCION MENSAJE DE ALERTA AL CANCELAR
            mensajeAlertaSubida.setNegativeButton(getResources().getString(R.string.btnDialogConfCancCreaPru), new DialogInterface.OnClickListener()

                    {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    }

            );

            //MOSTRAR
            mensajeAlertaSubida.show();

        }catch (Exception e) {e.printStackTrace();Log.d(TAG, "Error:", e);}
    }//FIN ACCION UPLOAD DATA EXAMEN

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
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_buscador, menu);
        //inflater.inflate(R.menu.img_upload_menu, menu);
        //getMenuInflater().inflate(R.menu.menu_buscador, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem menuFaq = menu.findItem(R.id.action_faq);
        MenuItem menuItemPassEdit = menu.findItem(R.id.action_perfil_pass_edit);
        MenuItem menuItemCompartir = menu.findItem(R.id.action_compartir);
        MenuItem menuItemTienda = menu.findItem(R.id.action_store);
        MenuItem menuNews = menu.findItem(R.id.action_news);
        //MenuItem menuGalery = menu.findItem(R.id.ic_menu_gallery);

        menuItemPassEdit.setVisible(true);
        menuNews.setVisible(false);
        menuItemTienda.setVisible(false);
        menuFaq.setVisible(true);


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_faq:

                LayoutInflater li = LayoutInflater.from(CrearPrueba.this);

                View vistaDialogoFaq = li.inflate(R.layout.dialog_faq_from_menu, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        CrearPrueba.this);

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

                LayoutInflater linflater = LayoutInflater.from(CrearPrueba.this);
                View vistaDialogoStore = linflater.inflate(R.layout.dialog_store_from_menu, null);

                AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(
                        CrearPrueba.this);

                alertDialogBuilder2.setView(vistaDialogoStore);
                //VISTAS LAYOUT
                TextView txtTituloProducto = (TextView) vistaDialogoStore.findViewById(R.id.txtTituloProducto);

                Button buttonPayOne = (Button) vistaDialogoStore.findViewById(R.id.buttonPayOne);
                Button buttonPayTwo = (Button) vistaDialogoStore.findViewById(R.id.buttonPayTwo);
                Button buttonPayThree = (Button) vistaDialogoStore.findViewById(R.id.buttonPayThree);
                Button buttonPayFour = (Button) vistaDialogoStore.findViewById(R.id.buttonPayFour);

                if(usuarioPerfil.isUser_ads_disabled()) {//SI HAY PAGO MOSTRAMOS MENSAJE Y DESHABILITAMOS BOTON DE PAGO
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

            case R.id.action_perfil_pass_edit:

                LayoutInflater liActionPerfil = LayoutInflater.from(CrearPrueba.this);
                final View vistaDialogo = liActionPerfil.inflate(R.layout.login_dialog_password_change, null);

                final EditText etxtLoginUser = (EditText)vistaDialogo.findViewById(R.id.etxtLoginUser);
                final EditText etxtPassPpal = (EditText)vistaDialogo.findViewById(R.id.etxtPassPpal);
                final EditText etxtNewPassPpal = (EditText)vistaDialogo.findViewById(R.id.etxtNewPassPpal);
                final EditText etxtNewPassPpalConfirm = (EditText)vistaDialogo.findViewById(R.id.etxtNewPassPpalConfirm);
                final EditText etxtNewPassSecondary = (EditText)vistaDialogo.findViewById(R.id.etxtNewPassSecondary);
                final EditText etxtNewPassSecondaryConfirm = (EditText)vistaDialogo.findViewById(R.id.etxtNewPassSecondaryConfirm);

                AlertDialog.Builder alertDialogBuilderPerfil = new AlertDialog.Builder(
                        CrearPrueba.this);

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

                                        if(usuarioPerfil.getUser_password() == passwordPpal || usuarioPerfil.getUser_login() ==
                                                userLogin){

                                            if(newPassPpal.length() > 6 && confirmNewPassPpal.length() > 6 &&
                                                    newPassSecondary.length() > 6 && confirmNewPassSecondary.length() > 6) {

                                                if (newPassPpal == confirmNewPassPpal) {

                                                    if (newPassSecondary == confirmNewPassSecondary) {

                                                        //ENVIO DE DATOS A LA BASE DE DATOS REMOTA MONGO
                                                        try {
                                                            ProgressBar progressBar = new ProgressBar(CrearPrueba.this);
                                                            DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();

                                                            //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                                                            url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.CHANGE_PASSWORDS + usuarioPerfil.getUser_id();
                                                            JSONObject jsonObject = new JSONObject();

                                                            jsonObject.put("user_login", usuarioPerfil.getUser_login());
                                                            jsonObject.put("user_password", passwordPpal);
                                                            jsonObject.put("user_new_admin_password", newPassPpal);
                                                            jsonObject.put("user_secondary_password", newPassSecondary);

                                                            new GeneralHttpTask(CrearPrueba.this, progressBar, dynamicTestResponse, "CHANGE_PASSWORD", jsonObject, usuarioPerfil, dialog).execute(url);

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            Log.d(TAG, getString(R.string.errorEmailEnvio), e);
                                                        } finally {
                                                            //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
                                                            if (!usuarioPerfil.isUser_ads_disabled()) {
                                                                //ADMOB INTERSTITIAL
                                                                if (mInterstitialAd.isLoaded()) {
                                                                    mInterstitialAd.show();
                                                                }
                                                            }
                                                        }

                                                    } else {
                                                        Toast.makeText(CrearPrueba.this, getString(R.string.passSecCambioNoCoincide),
                                                                Toast.LENGTH_SHORT).show();}
                                                } else {
                                                    Toast.makeText(CrearPrueba.this, getString(R.string.passPpalCambioNoCoincide),
                                                            Toast.LENGTH_SHORT).show();}
                                            }else{Toast.makeText(CrearPrueba.this, getString(R.string.passLongitudError),
                                                    Toast.LENGTH_SHORT).show();}
                                        }else{Toast.makeText(CrearPrueba.this, getString(R.string.passLoginActualIncorrecto),
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
                AlertDialog alertDialog3 = alertDialogBuilderPerfil.create();
                // show it
                alertDialog3.show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        //IN APP BILLING GOOGLE
        inAppPayApi.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        if(!usuarioPerfil.isUser_ads_disabled()) {
            //ADMOB INTERSTITIAL
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

    private void adjustScroll(View v){
        v.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void getPayment(int product){
        //IN APP BILLING GOOGLE
        if(Constants.isInAppSetupCreated) {
            if(product == 1) {//DESHABILITA PUBLICIDAD
                inAppPayApi.purchaseRemoveAds();//this, usuarioPerfil.getUser_id(), 1
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

    private String getRealSizeFromUri(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Audio.Media.SIZE };
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}