package com.javadoh.toptestquiz.activities.pruebas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.javadoh.toptestquiz.activities.pruebas.bean.PreguntasBean;
import com.javadoh.toptestquiz.R;
import com.javadoh.toptestquiz.utils.Constants;
import com.javadoh.toptestquiz.utils.LoginDataBaseHelper;
import com.javadoh.toptestquiz.utils.bean.MemoryBean;
import com.javadoh.toptestquiz.utils.bean.exam.ExamUsuarioNubeConf;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioNube;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioEncuestado;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioNube;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by luiseliberal on 06-07-2015.
 */
public class PaginaFragmento extends Fragment {

    public static final String PERFIL_USUARIO = "SESSION_USER";
    public static final String PERFIL_EXAMENES = "SESSION_EXAMS";
    public static final String ID_EXAM = "SESSION_EXAMID";

    public static final String ID_FRAGMENTO = "ID_FRAGMENTO";
    public static final String ID_PREGUNTA = "ID_PREGUNTA";
    public static final String TEXTO_PREGUNTA = "TEXTO_PREGUNTA";
    public static final String IMG_PREGUNTA_PATH = "IMAGEN_PREGUNTA";
    public static final String TIEMPO_EXAMEN = "TIEMPO_PREGUNTA";
    public static final String LISTA_RESPUESTAS_PREDEF = "RESPUESTAS_PREDEF";
    public static final String TIEMPO_CRONO = "TIEMPO_CRONO";
    //DATOS DE SESION DE ENCUESTADO
    /*public static final String DIA = "DIA_ENCUESTADO";
    public static final String HORA = "HORA_ENCUESTADO";
    public static final String LOCACION = "LOCACION_ENCUESTADO";
    public static final String GENERO = "GENERO_ENCUESTADO";
    public static final String EDAD = "EDAD_ENCUESTADO";
    public static final String CLASE_USUARIO = "CLASE_USUARIO_ENCUESTADO";
    public static final String COMUNA = "COMUNA_ENCUESTADO";
    public static final String FRECUENCIA = "FRECUENCIA_ENCUESTADO";
    public static final String MOTIVO = "MOTIVO_ENCUESTADO";
    public static final String OCUPACION = "OCUPACION_ENCUESTADO";
    public static final String OTROS1 = "OTROS1_ENCUESTADO";
    public static final String OTROS2 = "OTROS2_ENCUESTADO";
    public static final String OTROS3 = "OTROS3_ENCUESTADO";
    public static final String OTROS4 = "OTROS4_ENCUESTADO";
    public static final String OTROS5 = "OTROS5_ENCUESTADO";*/

    private Chronometer crono;
    private int fragmentoId;
    private int radioButtonValue = 0;
    private String radioButtonResponse = "";
    RadioGroup respuestasPreDefRdio;
    RadioButton radioRespBoton1, radioRespBoton2, radioRespBoton3, radioRespBoton4;
    LoginDataBaseHelper loginDataBaseHelper;
    private static long timeElapsed = 0;
    private static int seconds = 0;

    private static PerfilUsuarioNube usuarioPerfilSesion;
    private static ExamenUsuarioNube examenesPerfilSesion;
    private static PerfilUsuarioEncuestado datosEncuestado;
    private static int examenActualId;
    private static String passwordUsedForLogin;
    private static int rowExamConfNube;
    //FLAG VERSION DESPUES DE KITKAT
    boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH;
    private DisplayMetrics metrics;

    public static final PaginaFragmento newInstance(PreguntasBean preguntasBean, Bundle sesion, int contador){
        PaginaFragmento pf = new PaginaFragmento();
        Bundle bdl = new Bundle(5);
        bdl.putInt(ID_FRAGMENTO, contador);
        bdl.putInt(ID_PREGUNTA, preguntasBean.getIdPregunta());
        bdl.putString(TEXTO_PREGUNTA, preguntasBean.getTextoPregunta());
        bdl.putString(IMG_PREGUNTA_PATH, preguntasBean.getPathImagenPregunta());

        usuarioPerfilSesion = (PerfilUsuarioNube) sesion.get("SESSION_USER");
        examenesPerfilSesion = (ExamenUsuarioNube) sesion.get("SESSION_EXAMS");
        examenActualId = sesion.getInt("SESSION_EXAMID");
        datosEncuestado = (PerfilUsuarioEncuestado) sesion.get("SESSION_ENCUESTADO");
        passwordUsedForLogin = sesion.getString("PASSWORD_USED");

        /*bdl.putParcelableArrayList(PERFIL_USUARIO, (ArrayList<? extends Parcelable>) sesion.get("SESSION_USER"));
        bdl.putParcelableArrayList(PERFIL_EXAMENES, (ArrayList<? extends Parcelable>) sesion.get("SESSION_EXAMS"));
        bdl.putInt(ID_EXAM, sesion.getInt("SESSION_EXAMID"));*/
        //DATOS DE ENCUESTADO
        /*bdl.putString(DIA, sesion.getString("DIA_ENCUESTADO"));
        bdl.putString(HORA, sesion.getString("HORA_ENCUESTADO"));
        bdl.putString(LOCACION, sesion.getString("LOCACION_ENCUESTADO"));
        bdl.putInt(GENERO, sesion.getInt("GENERO_ENCUESTADO"));
        bdl.putInt(EDAD, sesion.getInt("EDAD_ENCUESTADO"));
        bdl.putString(CLASE_USUARIO, sesion.getString("CLASE_USUARIO_ENCUESTADO"));
        bdl.putString(COMUNA, sesion.getString("COMUNA_ENCUESTADO"));
        bdl.putString(FRECUENCIA, sesion.getString("FRECUENCIA_ENCUESTADO"));
        bdl.putString(MOTIVO, sesion.getString("MOTIVO_ENCUESTADO"));
        bdl.putString(OCUPACION, sesion.getString("OCUPACION_ENCUESTADO"));
        bdl.putString(OTROS1, sesion.getString("OTROS1_ENCUESTADO"));
        bdl.putString(OTROS2, sesion.getString("OTROS2_ENCUESTADO"));
        bdl.putString(OTROS3, sesion.getString("OTROS3_ENCUESTADO"));
        bdl.putString(OTROS4, sesion.getString("OTROS4_ENCUESTADO"));
        bdl.putString(OTROS5, sesion.getString("OTROS5_ENCUESTADO"));*/


        for(int x = 0; x < examenesPerfilSesion.getExam_conf().size(); x++){
            if(examenActualId == examenesPerfilSesion.getExam_conf().get(x).getExam_designId()){
                rowExamConfNube = x;
            }
        }
        //SETEAMOS EL TIEMPO DEL EXAMEN
        bdl.putDouble(TIEMPO_EXAMEN, examenesPerfilSesion.getExam_conf().get(examenActualId).getExam_time_by_question());

        pf.setArguments(bdl);

        return pf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //CAPTURAMOS LOS VALORES SETEADOS EN LOS ARGUMENTOS
        fragmentoId = getArguments().getInt(ID_FRAGMENTO);
        final int idPregunta = getArguments().getInt(ID_PREGUNTA);
        final String textoPregunta = getArguments().getString(TEXTO_PREGUNTA);
        final String imagenPreguntaPath = getArguments().getString(IMG_PREGUNTA_PATH);
        final double tiempoExamen = getArguments().getDouble(TIEMPO_EXAMEN);
        //DATOS DE ENCUESTADO
        /*final String[] datosEncuestado =  new String[7];
        datosEncuestado[0] = getArguments().getString(DIA);
        datosEncuestado[1] = getArguments().getString(HORA);
        datosEncuestado[2] = getArguments().getString(LOCACION);
        datosEncuestado[3] = getArguments().getString(GENERO);
        datosEncuestado[4] = getArguments().getString(EDAD);
        datosEncuestado[5] = getArguments().getString(CLASE_USUARIO);
        datosEncuestado[6] = getArguments().getString(COMUNA);
        datosEncuestado[7] = getArguments().getString(FRECUENCIA);
        datosEncuestado[8] = getArguments().getString(MOTIVO);
        datosEncuestado[9] = getArguments().getString(OCUPACION);
        datosEncuestado[10] = getArguments().getString(OTROS1);
        datosEncuestado[11] = getArguments().getString(OTROS2);
        datosEncuestado[12] = getArguments().getString(OTROS3);
        datosEncuestado[13] = getArguments().getString(OTROS4);
        datosEncuestado[14] = getArguments().getString(OTROS5);*/

        Button btnPreguntaSiguiente, btnPreguntaAnterior;
        TextView textoPreguntaView;

        //INSTANCIACION DE BD
        loginDataBaseHelper = new LoginDataBaseHelper(MemoryBean.getContextBase());
        loginDataBaseHelper.open();

        crono = (Chronometer) getActivity().findViewById(R.id.calling_crono);

        View v = inflater.inflate(R.layout.prueba_ppal, container, false);

        //SETEAMOS LA PREGUNTA CORRESPONDIENTE A LA PAGINA FRAGMENTO
        textoPreguntaView = (TextView)v.findViewById(R.id.textoPregunta);
        textoPreguntaView.setText(textoPregunta);

        try {
            //scaleWindow = getResources().getDisplayMetrics().density;
            metrics = getResources().getDisplayMetrics();


            //TITULO DEL EXAMEN
            TextView tituloPrueba = (TextView) v.findViewById(R.id.tituloPruebaPpal);
            String tituloAux = examenesPerfilSesion.getExam_conf().get(examenActualId).getExam_title();
            tituloAux = tituloAux.replace("_", " ");
            tituloPrueba.setText(tituloAux);

            //CARGAMOS LA IMAGEN CON PICASSO EN EL LAYOUT DE LA PRUEBA
            ImageView myImage = (ImageView)v.findViewById(R.id.imagenPregunta);

            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(GradientDrawable.RECTANGLE);
            gradientDrawable.setColor(getResources().getColor(R.color.colorSecondaryText));

            Picasso.with(getContext()).load(imagenPreguntaPath).placeholder(gradientDrawable).into(myImage);
            //Picasso.with(getContext()).load(imagenPreguntaPath).resize((int) (metrics.density * 250f), (int) (metrics.density * 150f)).into(myImage);
                    //.fit().centerCrop().into(myImage);
                    //.resize((int) (metrics.density * 200f), (int) (metrics.density * 150f))

            respuestasPreDefRdio = (RadioGroup)v.findViewById(R.id.respuestasPreDefRdio);

                for (int y = 0; y < examenesPerfilSesion.getExam_conf().get(examenActualId).getExamUsuarioNubeCurrentConfList().getAnswers().get(idPregunta).getAnswers().size(); y++) {
                    switch (y) {
                        case 0:
                            radioRespBoton1 = (RadioButton) v.findViewById(R.id.radioButtonResp1);
                            radioRespBoton1.setText(examenesPerfilSesion.getExam_conf().get(examenActualId).getExamUsuarioNubeCurrentConfList().getAnswers().get(idPregunta).getAnswers().get(y).getDesc());
                            break;
                        case 1:
                            radioRespBoton2 = (RadioButton) v.findViewById(R.id.radioButtonResp2);
                            radioRespBoton2.setText(examenesPerfilSesion.getExam_conf().get(examenActualId).getExamUsuarioNubeCurrentConfList().getAnswers().get(idPregunta).getAnswers().get(y).getDesc());
                            break;
                        case 2:
                            radioRespBoton3 = (RadioButton) v.findViewById(R.id.radioButtonResp3);
                            radioRespBoton3.setText(examenesPerfilSesion.getExam_conf().get(examenActualId).getExamUsuarioNubeCurrentConfList().getAnswers().get(idPregunta).getAnswers().get(y).getDesc());
                            break;
                        case 3:
                            radioRespBoton4 = (RadioButton) v.findViewById(R.id.radioButtonResp4);
                            radioRespBoton4.setText(examenesPerfilSesion.getExam_conf().get(examenActualId).getExamUsuarioNubeCurrentConfList().getAnswers().get(idPregunta).getAnswers().get(y).getDesc());
                            break;
                    }
                }

            //EVENTOS DE RADIO BUTTONS
            respuestasPreDefRdio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub

                    if (radioRespBoton1.isChecked()) {
                        radioButtonValue = 1;
                        radioButtonResponse = radioRespBoton1.getText().toString();
                    } else if (radioRespBoton2.isChecked()) {
                        radioButtonValue = 2;
                        radioButtonResponse = radioRespBoton2.getText().toString();
                    } else if (radioRespBoton3.isChecked()) {
                        radioButtonValue = 3;
                        radioButtonResponse = radioRespBoton3.getText().toString();
                    } else if (radioRespBoton4.isChecked()) {
                        radioButtonValue = 4;
                        radioButtonResponse = radioRespBoton4.getText().toString();
                    } else {
                        radioButtonValue = 0;
                    }

                    if (radioButtonValue != 0) {
                        //DEBE CONTINUAR HASTA EL FINAL DE LA PRUEBA
                        //stopCrono();//SE DETIENE EL CRONOMETRO

                        //SELLAMOS TODOS LOS RADIO BUTTON UNA VEZ SELECCIONADA LA RESPUESTA
                        //LUIS 19122016 LIBERAMOS SELLO PARA PERMITIR LAS RESPUESTAS
                        /*for (int i = 0; i < respuestasPreDefRdio.getChildCount(); i++) {
                            respuestasPreDefRdio.getChildAt(i).setEnabled(false);
                        }*/

                        try {
                            /**INSERTAR DATA EN BD AL RESPONDER**/
                            //boolean validarRegistro = loginDataBaseHelper.validarRegistroExistenteInsert(usuarioPerfilSesion.getUser_login(), MemoryBean.getIdExamen(), idPregunta);

                            //SI NO EXISTE EL REGISTRO PROCEDEMOS A INSERTAR
                            //if (!validarRegistro) {

                                //FORMATO AL CRONOMETRO DE ANDROID PASADO A SEGUNDOS
                                /*timeElapsed = SystemClock.elapsedRealtime() - crono.getBase();
                                int hours = (int) (timeElapsed / 3600000);
                                int minutes = (int) (timeElapsed - hours * 3600000) / 60000;
                                seconds = (int) (timeElapsed - hours * 3600000 - minutes * 60000) / 1000;*/


                                System.out.println("FRAG ID: " + fragmentoId + ", IDPREG: " + idPregunta + ", TEXTPREG: " + textoPregunta + "IMGPREG: " + imagenPreguntaPath +
                                        ", ID_RESPUESTA: " + radioButtonValue + ", TIEMPRESP: " + tiempoExamen + ", USUARIO: " + usuarioPerfilSesion.getUser_login() + ", SEGUNDOS CRONO: " + seconds + ", CRONO: " + crono.getText() + " TEST CRONO: " + (SystemClock.currentThreadTimeMillis() - crono.getBase()));

                                //MAPA DE PREGUNTAS RESPONDIDAS SE REMUEVE SI EXISTE PREVIAMENTE LA RESPUESTA DE LA PREGUNTA
                                if (Constants.getMapaPreguntasRespuestasContestadas().containsKey(idPregunta)) {
                                    Constants.getMapaPreguntasRespuestasContestadas().remove(idPregunta);
                                }
                                Constants.getMapaPreguntasRespuestasContestadas().put(idPregunta, textoPregunta + "#" + radioButtonValue + "#" + radioButtonResponse);

                        //}else{
                        //    Toast.makeText(getActivity(), "Ya existe el registro de esta respuesta", Toast.LENGTH_SHORT).show();
                       // }

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });

            //GET REFERENCIAS DE BOTONES Y DIBUJARLAS EN LA PAGINA
            btnPreguntaSiguiente = (Button) v.findViewById(R.id.btnPreguntaSiguiente);
            btnPreguntaAnterior = (Button) v.findViewById(R.id.btnPreguntaAnterior);

            /*if(!isKitKat){
                btnPreguntaSiguiente.setBackgroundColor(getResources().getColor(R.color.color_primary_programatically));
                btnPreguntaAnterior.setBackgroundColor(getResources().getColor(R.color.color_textgray_programatically));
            }*/

            //ACCION DEL BOTON PREGUNTA SIGUIENTE
            btnPreguntaSiguiente.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    //AVANZAR UN FRAGMENTO
                    ((PruebaPrincipal)getActivity()).setCurrentItem(fragmentoId+1, true);

                    //REINICIAR EL CRONO PARA EL PROXIMO FRAGMENTO
                    //crono.setBase(SystemClock.elapsedRealtime());

                    //SI EL FRAGMENTO ES EL ULTIMO EN BASE A LA LISTA DE PREGUNTAS VAMOS A LA PAGINA DE DESPEDIDA
                    if((fragmentoId+2) > examenesPerfilSesion.getExam_conf().get(examenActualId).getExamUsuarioNubeCurrentConfList().getQuestions().size()){

                        //DIALOGO DE CONFIRMACION DE FIN DE LA PRUEBA
                        try{
                            final AlertDialog.Builder mensajeAlertaFin = new AlertDialog.Builder(getContext());
                            mensajeAlertaFin.setTitle(getResources().getString(R.string.tituloMsgConfirmCreaPru));
                            mensajeAlertaFin.setMessage(getResources().getString(R.string.msgDialogConfirmFinPru));

                            //ACCION MENSAJE ALERTA ACEPTAR
                            mensajeAlertaFin.setPositiveButton(getResources().getString(R.string.btnDialogConfEndPru), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            int examenPresentado = loginDataBaseHelper.validarUltimoExamenPresentadoUsuario(datosEncuestado, usuarioPerfilSesion, examenActualId);

                                            //ENVIO A BASE DE DATOS DE LA RESPUESTA EN LINEA
                                            for (Map.Entry<Integer, String> entry : Constants.getMapaPreguntasRespuestasContestadas().entrySet())
                                            {//COMENTADO TEXTO PREGUNTA Y TEXTO RESPUESTA
                                                StringTokenizer st = new StringTokenizer(entry.getValue(),"#");
                                                String textoPregunta = "";
                                                int respuestaValor = 0;
                                                String respuestaTexto = "";
                                                while(st.hasMoreTokens()){//PREG1#1#Si, PREG2#2#NO
                                                    textoPregunta = st.nextToken();
                                                    respuestaValor = Integer.parseInt(st.nextToken());
                                                    respuestaTexto = st.nextToken();
                                                }

                                                //CALCULAMOS LOS SEGUNDOS TOTALES QUE SE TARDO EN RESPONDER LA PRUEBA
                                                timeElapsed = SystemClock.elapsedRealtime() - crono.getBase();
                                                int hours = (int) (timeElapsed / 3600000);
                                                int minutes = (int) (timeElapsed - hours * 3600000) / 60000;
                                                seconds = (int) (timeElapsed - hours * 3600000 - minutes * 60000) / 1000;

                                                datosEncuestado.setSegundosTotalesPrueba(seconds);

                                                loginDataBaseHelper.sendDataReport(datosEncuestado, usuarioPerfilSesion, examenActualId,
                                                        examenPresentado, entry.getKey(), textoPregunta, respuestaValor,
                                                        respuestaTexto, seconds);
                                            }

                                            //ENVIO A BASE DE DATOS DE LOS DATOS DE ENCUESTADO
                                            loginDataBaseHelper.sendDataEncuestado(datosEncuestado, usuarioPerfilSesion);

                                            //CREAR INTENT DE PAGINA FINAL
                                            Intent intentCloseExam = new Intent(MemoryBean.getContextBase().getApplicationContext(), PruebaDespedida.class);
                                            intentCloseExam.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intentCloseExam.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intentCloseExam.putExtra("SESSION_USER", usuarioPerfilSesion);
                                            intentCloseExam.putExtra("SESSION_EXAMS", examenesPerfilSesion);
                                            intentCloseExam.putExtra("SESSION_USER_ENCUESTADO", datosEncuestado);
                                            intentCloseExam.putExtra("SESSION_EXAM_ACTUAL_ID", examenActualId);
                                            intentCloseExam.putExtra("PASSWORD_USED", passwordUsedForLogin);
                                            startActivity(intentCloseExam);

                                        }
                                    }

                            );//FIN ACCION MENSAJE DE ALERTA AL ACEPTAR

                            //ACCION MENSAJE DE ALERTA AL CANCELAR
                            mensajeAlertaFin.setNegativeButton(getResources().getString(R.string.btnDialogConfCancCreaPru), new DialogInterface.OnClickListener()

                                    {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    }

                            );

                            //MOSTRAR
                            mensajeAlertaFin.show();

                        }catch (Exception e) {e.printStackTrace();}

                    }

                }
            });

            //ACCION DEL BOTON PREGUNTA ANTERIOR
            btnPreguntaAnterior.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //RETROCEDER UN FRAGMENTO
                    if(fragmentoId > 0) {
                        ((PruebaPrincipal) getActivity()).setCurrentItem(fragmentoId - 1, true);
                    }else{
                        Toast.makeText(getActivity(), getResources().getString(R.string.msgNoQuestionsBack), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

        return v;
    }

    public Bitmap loadBitmapFromAssets(Context context, String path)
    {
        InputStream stream = null;
        try
        {
            stream = context.getAssets().open(path);
            return BitmapFactory.decodeStream(stream);
        }
        catch (Exception ignored) {} finally
        {
            try
            {
                if(stream != null)
                {
                    stream.close();
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    /*public boolean tiempoLlegoALimite(Chronometer chronometer) {

        boolean tiempoAcabado = false;
        long timeElapsed = SystemClock.elapsedRealtime() - crono.getBase();
        int hours = (int) (timeElapsed / 3600000);

        //SI EL TIEMPO EN MINUTOS ES MAYOR QUE EL PARAMETRIZADO EN LA CREACION DEL EXAMEN
        if ((timeElapsed - hours + 3600000) / 60000 > examenesPerfilSesion.getExam_conf().get(rowExamConfNube).getExam_time_by_question()){
            // Create Intent and start the new Activity here
            for (int i = 0; i < respuestasPreDefRdio.getChildCount(); i++) {
                respuestasPreDefRdio.getChildAt(i).setEnabled(false);
            }

            tiempoAcabado = true;

            //AVANZAR UN FRAGMENTO
            //((PruebaPrincipal)getActivity()).setCurrentItem(fragmentoId+1, true);

            //REINICIAR EL CRONO PARA EL PROXIMO FRAGMENTO
            //crono.setBase(SystemClock.elapsedRealtime());

            //SI EL FRAGMENTO ES EL ULTIMO EN BASE A LA LISTA DE PREGUNTAS VAMOS A LA PAGINA DE DESPEDIDA
            /*if((fragmentoId+2) > examenesPerfilSesion.getExam_conf().get(examenActualId).getExamUsuarioNubeCurrentConfList().getQuestions().size()){
                //CREAR INTENT DE PAGINA FINAL
                Intent intentCloseExam = new Intent(MemoryBean.getContextBase().getApplicationContext(), PruebaDespedida.class);
                startActivity(intentCloseExam);
            }
        }

        return tiempoAcabado;
    }*/
}